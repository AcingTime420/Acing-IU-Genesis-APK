# ===================================================================
# General & Kotlin ProGuard Configuration
# ===================================================================
-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
-renamesourcefileattribute SourceFile
-dontwarn javax.annotation.**

# ===================================================================
# Room Database Keep Rules
# ===================================================================
-dontwarn androidx.room.paging.**
-keep class androidx.room.** { *; }
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }
-keep @androidx.room.TypeConverter class * { *; }
-keep @androidx.room.Database class * { *; }
-keepclassmembers class * {
    @androidx.room.PrimaryKey *;
    @androidx.room.ColumnInfo *;
    @androidx.room.Embedded *;
    @androidx.room.Relation *;
    @androidx.room.TypeConverters *;
}
-keep class * extends androidx.room.migration.Migration { *; }
-keep class * extends androidx.room.RoomOpenHelper$Delegate { *; }
-keep class *_Impl { *; }

# ===================================================================
# Moshi JSON Keep Rules
# ===================================================================
-keep class com.squareup.moshi.** { *; }
-dontwarn com.squareup.moshi.**
-keep @com.squareup.moshi.JsonClass class * { *; }
-keep @com.squareup.moshi.JsonQualifier @interface * { *; }
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
    @com.squareup.moshi.FromJson *;
    @com.squareup.moshi.ToJson *;
}
# Keep generated Moshi JsonAdapters and reflective adapter constructors
-keep class *JsonAdapter {
    public <init>(com.squareup.moshi.Moshi);
    public <init>(com.squareup.moshi.Moshi, java.lang.reflect.Type[]);
    public <init>(java.lang.reflect.Type[], java.util.Set);
    public <init>(...);
}

# ===================================================================
# Firebase & Google Play Services Keep Rules
# ===================================================================
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Firebase Component Registrars and internal reflection
-keep class * implements com.google.firebase.components.ComponentRegistrar
-keep class * implements com.google.firebase.appcheck.AppCheckProviderFactory
-keep class com.google.firebase.appcheck.** { *; }
-keep class com.google.firebase.ai.** { *; }

# Firebase entity and serialization fields
-keepclassmembers class * {
    @com.google.firebase.firestore.PropertyName <fields>;
    @com.google.firebase.firestore.PropertyName <methods>;
    @com.google.firebase.firestore.Exclude <fields>;
    @com.google.firebase.firestore.Exclude <methods>;
    @com.google.firebase.firestore.ServerTimestamp <fields>;
}

# ===================================================================
# Retrofit, OkHttp & Coroutines
# ===================================================================
-keepclassmembers class * {
    @retrofit2.http.* <methods>;
}
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-dontwarn okio.**
-keep class kotlinx.coroutines.** { *; }

# ===================================================================
# TensorFlow Lite
# ===================================================================
-keep class org.tensorflow.lite.** { *; }
-dontwarn org.tensorflow.lite.**

# ===================================================================
# AndroidX WorkManager Worker Initialization
# ===================================================================
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
-keep class * extends androidx.work.Worker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# ===================================================================
# Application Data Models, Security & Forensics Entities
# ===================================================================
-keep class com.example.data.** { *; }
-keep class com.example.security.** { *; }
-keep class com.example.forensics.** { *; }
-keep class com.example.trust.** { *; }
