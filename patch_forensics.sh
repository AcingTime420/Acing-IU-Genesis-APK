sed -i '/import androidx.compose.runtime.setValue/a \
import androidx.activity.compose.rememberLauncherForActivityResult\
import androidx.activity.result.contract.ActivityResultContracts\
import androidx.compose.ui.platform.LocalContext\
import kotlinx.coroutines.launch\
import com.example.forensics.LogParserService\
import com.example.forensics.LogFinding' app/src/main/java/com/example/ui/screens/ForensicsScreen.kt
