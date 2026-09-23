package co.japl.android.synapsefit.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.japl.android.synapsefit.BuildConfig
import co.japl.android.synapsefit.WearDependencyProvider
import com.google.firebase.crashlytics.FirebaseCrashlytics

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
        WearDependencyProvider.initialize(applicationContext)
        setContent {
            WearNavHost()
        }
    }
}
