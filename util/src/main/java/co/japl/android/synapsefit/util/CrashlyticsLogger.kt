package co.japl.android.synapsefit.util

@Suppress("TooGenericExceptionCaught", "SwallowedException")
fun logNonFatalException(throwable: Throwable) {
    try {
        val clazz = Class.forName("com.google.firebase.crashlytics.FirebaseCrashlytics")
        val getInstanceMethod = clazz.getMethod("getInstance")
        val instance = getInstanceMethod.invoke(null)
        val recordExceptionMethod = clazz.getMethod("recordException", Throwable::class.java)
        recordExceptionMethod.invoke(instance, throwable)
    } catch (e: Exception) {
        System.err.println("Crashlytics recordException failure: ${e.message}")
    }
}
