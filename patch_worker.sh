sed -i '/import androidx.activity.viewModels/a \
import androidx.work.ExistingPeriodicWorkPolicy\
import androidx.work.PeriodicWorkRequestBuilder\
import androidx.work.WorkManager\
import com.example.worker.SecuritySnapshotWorker\
import java.util.concurrent.TimeUnit' app/src/main/java/com/example/MainActivity.kt

sed -i '/setContent {/i \
        val workRequest = PeriodicWorkRequestBuilder<SecuritySnapshotWorker>(24, TimeUnit.HOURS).build()\
        WorkManager.getInstance(this).enqueueUniquePeriodicWork("DailySecuritySnapshot", ExistingPeriodicWorkPolicy.KEEP, workRequest)' app/src/main/java/com/example/MainActivity.kt
