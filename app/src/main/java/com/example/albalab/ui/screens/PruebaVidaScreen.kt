package com.example.albalab.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NoPhotography
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.albalab.navigation.Screen
import com.example.albalab.ui.components.BarraSuperior
import com.example.albalab.viewmodel.MainViewModel
import com.example.albalab.viewmodel.PruebaVidaViewModel
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.delay

// Prueba de vida: valida que frente a la cámara hay una persona y no una foto
@Composable
fun PruebaVidaScreen(
    mainViewModel: MainViewModel,
    pruebaVidaViewModel: PruebaVidaViewModel = viewModel()
) {
    val context = LocalContext.current
    val paso by pruebaVidaViewModel.pasoActual.collectAsState()
    val mensaje by pruebaVidaViewModel.mensaje.collectAsState()
    val completada by pruebaVidaViewModel.completada.collectAsState()
    val gestos by pruebaVidaViewModel.gestos.collectAsState()

    // Permiso de cámara: se revisa y, si falta, se pide (igual que en la guía 13)
    var permisoConcedido by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }
    val permisoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        permisoConcedido = concedido
        if (!concedido) {
            Toast.makeText(context, "Sin permiso de cámara no se puede hacer la prueba", Toast.LENGTH_LONG).show()
        }
    }
    LaunchedEffect(Unit) {
        if (!permisoConcedido) {
            permisoLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Al completar los 3 gestos espera un momento y entra a la app
    LaunchedEffect(completada) {
        if (completada) {
            delay(800)
            mainViewModel.navigateTo(Screen.Inicio, popUpTo = Screen.Verificacion, inclusive = true)
        }
    }

    val colorBorde by animateColorAsState(
        targetValue = if (completada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
        animationSpec = tween(250),
        label = "Color del borde"
    )

    Scaffold(
        topBar = { BarraSuperior(titulo = "Prueba de vida", onVolver = { mainViewModel.navigateBack() }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Paso ${minOf(paso + 1, 3)} de 3",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            // AnimatedContent: cada instrucción nueva entra con una animación suave
            AnimatedContent(
                targetState = mensaje,
                transitionSpec = {
                    (fadeIn(tween(200)) + scaleIn(initialScale = 0.9f, animationSpec = tween(200))) togetherWith
                        fadeOut(tween(120))
                },
                label = "Instrucción"
            ) { texto ->
                Text(
                    text = texto,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center
                )
            }
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(CircleShape)
                    .border(4.dp, colorBorde, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (permisoConcedido) {
                    VistaCamara(pruebaVidaViewModel)
                } else {
                    Icon(
                        imageVector = Icons.Filled.NoPhotography,
                        contentDescription = "Cámara sin permiso",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = completada,
                    enter = fadeIn(tween(200)) + scaleIn(tween(200))
                ) {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Prueba superada",
                        modifier = Modifier.size(96.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            // Una barrita por gesto: se pinta de violeta cuando se cumple
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                gestos.forEachIndexed { indice, _ ->
                    val color by animateColorAsState(
                        targetValue = if (indice < paso || completada) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant,
                        animationSpec = tween(200),
                        label = "Progreso"
                    )
                    Box(
                        modifier = Modifier
                            .width(48.dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(color)
                    )
                }
            }
            Text(
                text = "Los gestos se eligen al azar en cada intento, así una foto o un video grabado no sirven.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            if (permisoConcedido) {
                OutlinedButton(onClick = { pruebaVidaViewModel.reiniciar() }) {
                    Text("Reiniciar")
                }
            } else {
                Button(onClick = { permisoLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Permitir cámara")
                }
            }
        }
    }
}

// Vista previa de la cámara frontal (CameraX). Cada cuadro se manda al detector de rostros
@Composable
private fun VistaCamara(viewModel: PruebaVidaViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Detector de ML Kit con clasificación: entrega ojos abiertos y sonrisa (de 0 a 1)
    val detector = remember {
        FaceDetection.getClient(
            FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build()
        )
    }

    // Al salir de la pantalla se apaga la cámara y se libera el detector
    DisposableEffect(Unit) {
        onDispose {
            ProcessCameraProvider.getInstance(context).get().unbindAll()
            detector.close()
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val proveedorFuturo = ProcessCameraProvider.getInstance(ctx)
            proveedorFuturo.addListener({
                try {
                    val proveedor = proveedorFuturo.get()
                    val preview = Preview.Builder().build()
                    preview.setSurfaceProvider(previewView.surfaceProvider)
                    val analisis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                    analisis.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { cuadro ->
                        analizarCuadro(cuadro, detector, viewModel)
                    }
                    proveedor.unbindAll()
                    proveedor.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_FRONT_CAMERA, preview, analisis)
                } catch (e: Exception) {
                    Toast.makeText(ctx, "No se pudo abrir la cámara frontal", Toast.LENGTH_LONG).show()
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}

// Pasa un cuadro de la cámara a ML Kit y envía los resultados al ViewModel
@androidx.annotation.OptIn(ExperimentalGetImage::class)
private fun analizarCuadro(
    cuadro: ImageProxy,
    detector: FaceDetector,
    viewModel: PruebaVidaViewModel
) {
    val imagen = cuadro.image
    if (imagen == null) {
        cuadro.close()
        return
    }
    val entrada = InputImage.fromMediaImage(imagen, cuadro.imageInfo.rotationDegrees)
    detector.process(entrada)
        .addOnSuccessListener { rostros ->
            val rostro = rostros.firstOrNull()
            viewModel.procesarRostro(
                cantidadRostros = rostros.size,
                ojoIzquierdo = rostro?.leftEyeOpenProbability,
                ojoDerecho = rostro?.rightEyeOpenProbability,
                sonrisa = rostro?.smilingProbability,
                giroLateral = rostro?.headEulerAngleY ?: 0f,
                giroVertical = rostro?.headEulerAngleX ?: 0f
            )
        }
        .addOnCompleteListener { cuadro.close() }
}
