sed -i '/val isAnalyzingLog/a \
    val context = LocalContext.current\
    val scope = androidx.compose.runtime.rememberCoroutineScope()\
    var parsedFindings by remember { mutableStateOf<List<LogFinding>>(emptyList()) }\
    val logParser = remember { LogParserService() }\
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->\
        if (uri != null) {\
            scope.launch {\
                parsedFindings = logParser.parseLogFile(context, uri)\
            }\
        }\
    }' app/src/main/java/com/example/ui/screens/ForensicsScreen.kt
