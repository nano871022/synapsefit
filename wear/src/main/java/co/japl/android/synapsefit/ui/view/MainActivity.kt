package co.japl.android.synapsefit.ui.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import co.japl.android.synapsefit.WearDependencyProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WearDependencyProvider.initialize(applicationContext)
        setContent {
            WearNavHost()
        }
    }
}
