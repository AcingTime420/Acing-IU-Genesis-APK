sed -i '/fun AcingGenesisApp(viewModel: AcingViewModel) {/a \
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()\
    if (!isAuthenticated) {\
        AuthScreen(viewModel = viewModel)\
        return\
    }' app/src/main/java/com/example/MainActivity.kt
