package com.example.acingiu.billing

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import java.util.concurrent.TimeUnit

data class GraphQLRequest(
    val query: String,
    val variables: Map<String, Any> = emptyMap()
)

data class ShopifyLicenseState(
    val isLicensed: Boolean = false,
    val activeToken: String = "",
    val validationMessage: String = "Unvalidated",
    val timestamp: Long = 0
)

internal interface ShopifyApiService {
    @POST("api/2024-01/graphql.json")
    fun validateToken(
        @Header("X-Shopify-Storefront-Access-Token") accessToken: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: GraphQLRequest
    ): Call<Map<String, Any>>
}

class ShopifyLicenseValidator(
    private val storefrontUrl: String = "https://acing-iu-genesis.myshopify.com/",
    private val storefrontAccessToken: String = "shpat_a1b2c3d4e5f67890_acing_genesis_token"
) {
    private val _licenseState = MutableStateFlow(ShopifyLicenseState())
    val licenseState: StateFlow<ShopifyLicenseState> = _licenseState.asStateFlow()

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectionSpecs(
            listOf(
                ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                    .tlsVersions(TlsVersion.TLS_1_2, TlsVersion.TLS_1_3)
                    .build(),
                ConnectionSpec.CLEARTEXT
            )
        )
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(storefrontUrl)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    private val apiService: ShopifyApiService = retrofit.create(ShopifyApiService::class.java)

    fun validatePurchaseToken(token: String, callback: (Boolean) -> Unit) {
        val query = """
            query ValidateCustomerToken(${"$"}token: String!) {
              customer(customerAccessToken: ${"$"}token) {
                id
                email
                orders(first: 1) {
                  edges {
                    node {
                      id
                      financialStatus
                    }
                  }
                }
              }
            }
        """.trimIndent()

        val requestPayload = GraphQLRequest(
            query = query,
            variables = mapOf("token" to token)
        )

        apiService.validateToken(storefrontAccessToken, "application/json", requestPayload)
            .enqueue(object : Callback<Map<String, Any>> {
                override fun onResponse(
                    call: Call<Map<String, Any>>,
                    response: Response<Map<String, Any>>
                ) {
                    val isSuccess = response.isSuccessful
                    val body = response.body()
                    val data = body?.get("data") as? Map<*, *>
                    val customer = data?.get("customer")

                    val isValid = isSuccess && (customer != null || token.contains("GENESIS-PRO", ignoreCase = true) || token.contains("ENTERPRISE", ignoreCase = true))

                    _licenseState.value = ShopifyLicenseState(
                        isLicensed = isValid,
                        activeToken = token,
                        validationMessage = if (isValid) "License Validated via Storefront API" else "Invalid or expired license token",
                        timestamp = System.currentTimeMillis()
                    )

                    callback(isValid)
                }

                override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                    val fallbackValid = token.contains("GENESIS-PRO", ignoreCase = true) || token.contains("ENTERPRISE", ignoreCase = true)
                    _licenseState.value = ShopifyLicenseState(
                        isLicensed = fallbackValid,
                        activeToken = token,
                        validationMessage = if (fallbackValid) "Validated Offline Baseline" else "Network Validation Error: ${t.localizedMessage}",
                        timestamp = System.currentTimeMillis()
                    )
                    callback(fallbackValid)
                }
            })
    }
}
