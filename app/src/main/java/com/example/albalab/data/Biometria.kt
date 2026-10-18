package com.example.albalab.data

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

// Muestra el diálogo de biometría del sistema (huella, rostro o PIN del teléfono)
object Biometria {

    fun autenticar(
        activity: FragmentActivity,
        autenticadores: Int,
        titulo: String,
        onExito: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Primero se revisa si el teléfono tiene ese método configurado
        val manager = BiometricManager.from(activity)
        if (manager.canAuthenticate(autenticadores) != BiometricManager.BIOMETRIC_SUCCESS) {
            onError("Este método no está configurado en el teléfono")
            return
        }

        val prompt = BiometricPrompt(
            activity,
            ContextCompat.getMainExecutor(activity),
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    onExito()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    onError(errString.toString())
                }
            }
        )

        val info = BiometricPrompt.PromptInfo.Builder()
            .setTitle(titulo)
            .setSubtitle("Alba Lab Conecta")
            .setAllowedAuthenticators(autenticadores)

        // Si no se permite el PIN del teléfono, el diálogo necesita un botón para cancelar
        if (autenticadores == BiometricManager.Authenticators.BIOMETRIC_STRONG) {
            info.setNegativeButtonText("Cancelar")
        }

        prompt.authenticate(info.build())
    }
}
