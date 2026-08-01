sed -i '/icon = Icons.Default.BugReport/a \
            )\
        }\
        item {\
            Card(\
                colors = CardDefaults.cardColors(containerColor = AegisSurface),\
                shape = RoundedCornerShape(16.dp),\
                modifier = Modifier.fillMaxWidth()\
            ) {\
                Column(modifier = Modifier.padding(16.dp)) {\
                    Button(\
                        onClick = { launcher.launch(arrayOf("text/plain", "application/octet-stream")) },\
                        colors = ButtonDefaults.buttonColors(containerColor = AegisPrimaryCyan),\
                        modifier = Modifier.fillMaxWidth()\
                    ) {\
                        Icon(imageVector = Icons.Default.Description, contentDescription = null, modifier = Modifier.size(18.dp))\
                        Spacer(modifier = Modifier.width(8.dp))\
                        Text("Import Log File (.txt/.log)", fontWeight = FontWeight.Bold)\
                    }\
                    if (parsedFindings.isNotEmpty()) {\
                        Spacer(modifier = Modifier.height(16.dp))\
                        Text("Log Analysis Findings:", color = AegisTextPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)\
                        Spacer(modifier = Modifier.height(8.dp))\
                        Box(modifier = Modifier.height(200.dp).fillMaxWidth()) {\
                            LazyColumn(modifier = Modifier.fillMaxSize()) {\
                                items(parsedFindings) { finding ->\
                                    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).background(AegisDarkBg).padding(8.dp)) {\
                                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {\
                                            Text("Line ${finding.line}", color = AegisTextSecondary, fontSize = 10.sp)\
                                            SeverityBadge(severity = finding.severity)\
                                        }\
                                        Text(finding.patternMatched, color = if (finding.severity == "CRITICAL") AegisDangerRed else AegisPrimaryCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)\
                                        Text(finding.content, color = AegisTextPrimary, fontSize = 10.sp, fontFamily = FontFamily.Monospace)\
                                    }\
                                }\
                            }\
                        }\
                    }\
                }' app/src/main/java/com/example/ui/screens/ForensicsScreen.kt
