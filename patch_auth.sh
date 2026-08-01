sed -i '/val isAuditingGovernance/a \
    private val _isAuthenticated = MutableStateFlow(false)\
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()\
\
    fun setAuthenticated(auth: Boolean) {\
        _isAuthenticated.value = auth\
    }' app/src/main/java/com/example/ui/AcingViewModel.kt
