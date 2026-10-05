package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TechNode
import com.example.ui.theme.AmberForge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldScience
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun LaboratoryScreen(
    node: TechNode,
    resources: Map<String, Int>,
    onCraftSuccess: (TechNode) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator }

    // Physical thermodynamic simulation parameters
    val targetTemp = node.targetTemperature
    val tolerance = node.toleranceRange
    val minTemp = (targetTemp * 0.2f).coerceAtLeast(20f)
    val maxTemp = targetTemp * 1.5f

    var currentTemp by remember { mutableFloatStateOf(minTemp) }
    var isPressingBellows by remember { mutableStateOf(false) }
    var sweetSpotSeconds by remember { mutableFloatStateOf(0f) }
    val requiredSeconds = 5.0f

    var gameState by remember { mutableStateOf("PLAYING") } // PLAYING, VICTORY, FAILED
    var failureReason by remember { mutableStateOf("") }

    // Thermal simulation loop (runs at ~30 FPS)
    LaunchedEffect(gameState, isPressingBellows) {
        while (gameState == "PLAYING") {
            delay(33L)
            val dt = 0.033f

            // Heating & cooling physics
            if (isPressingBellows) {
                val heatingPower = (maxTemp - currentTemp) * 1.6f
                currentTemp += heatingPower * dt
            } else {
                val coolingRate = (currentTemp - minTemp) * 1.1f
                currentTemp -= coolingRate * dt
            }
            currentTemp = currentTemp.coerceIn(minTemp, maxTemp)

            // Check if in sweet spot
            val inSweetSpot = currentTemp in (targetTemp - tolerance)..(targetTemp + tolerance)
            if (inSweetSpot) {
                sweetSpotSeconds += dt
                // Subtle haptic pulse
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                if (sweetSpotSeconds >= requiredSeconds) {
                    gameState = "VICTORY"
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 100, 50, 200), -1))
                    }
                }
            } else {
                sweetSpotSeconds = (sweetSpotSeconds - dt * 0.8f).coerceAtLeast(0f)
            }

            // Extreme condition failures
            if (currentTemp >= maxTemp - 15f && isPressingBellows) {
                // Overheat critical failure
                failureReason = "¡Sobrecalentamiento crítico! El crisol superó la temperatura límite y el material se volatilizó."
                gameState = "FAILED"
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "LABORATORIO TÉRMICO",
                        color = AmberForge,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = node.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Objective Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CONTROL DE TEMPERATURA Y SÍNTESIS",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mantén la temperatura del crisol en la zona óptima ($targetTemp°C ± $tolerance°C) durante 5 segundos continuos activando el fuelle para completar la síntesis.",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Central Temperature Dial & Crucible Graphic
            Box(
                modifier = Modifier
                    .size(230.dp),
                contentAlignment = Alignment.Center
            ) {
                val tempRatio = ((currentTemp - minTemp) / (maxTemp - minTemp)).coerceIn(0f, 1f)
                val sweetMinRatio = ((targetTemp - tolerance - minTemp) / (maxTemp - minTemp)).coerceIn(0f, 1f)
                val sweetMaxRatio = ((targetTemp + tolerance - minTemp) / (maxTemp - minTemp)).coerceIn(0f, 1f)
                val isInSweetSpot = currentTemp in (targetTemp - tolerance)..(targetTemp + tolerance)

                // Dial Canvas
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 18f
                    val arcSize = Size(size.width - strokeWidth * 2, size.height - strokeWidth * 2)
                    val arcOffset = Offset(strokeWidth, strokeWidth)
                    val startAngle = 135f
                    val sweepAngle = 270f

                    // Background track
                    drawArc(
                        color = Color(0xFF1E293B),
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        topLeft = arcOffset,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Sweet spot arc
                    val sweetStart = startAngle + (sweetMinRatio * sweepAngle)
                    val sweetSweep = (sweetMaxRatio - sweetMinRatio) * sweepAngle
                    drawArc(
                        color = EmeraldScience.copy(alpha = 0.5f),
                        startAngle = sweetStart,
                        sweepAngle = sweetSweep,
                        useCenter = false,
                        topLeft = arcOffset,
                        size = arcSize,
                        style = Stroke(width = strokeWidth + 6f, cap = StrokeCap.Butt)
                    )

                    // Current progress arc
                    val activeColor = when {
                        currentTemp > targetTemp + tolerance -> DangerRed
                        isInSweetSpot -> EmeraldScience
                        else -> AmberForge
                    }
                    drawArc(
                        color = activeColor,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle * tempRatio,
                        useCenter = false,
                        topLeft = arcOffset,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                // Center readout
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = if (isInSweetSpot) EmeraldScience else if (currentTemp > targetTemp + tolerance) DangerRed else AmberForge,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = "${currentTemp.toInt()}°C",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Objetivo: ${targetTemp.toInt()}°C",
                        color = if (isInSweetSpot) EmeraldScience else TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stability Progress Bar
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ESTABILIDAD DE SÍNTESIS",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${String.format("%.1f", sweetSpotSeconds)} / ${requiredSeconds.toInt()}s",
                        color = if (sweetSpotSeconds > 0) EmeraldScience else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { (sweetSpotSeconds / requiredSeconds).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = EmeraldScience,
                    trackColor = DarkSurfaceElevated
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action / Bellows Control Button
            if (gameState == "PLAYING") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(84.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (isPressingBellows) Color(0xFFD97706) else AmberForge
                        )
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    isPressingBellows = true
                                    tryAwaitRelease()
                                    isPressingBellows = false
                                }
                            )
                        }
                        .testTag("bellows_touch_target"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = null,
                            tint = DarkBackground,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (isPressingBellows) "¡INYECTANDO OXÍGENO!" else "MANTÉN PRESIONADO: ACCIONAR FUELLE",
                                color = DarkBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Suelta para enfriar • Mantén para calentar",
                                color = DarkBackground.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else if (gameState == "VICTORY") {
                // Victory Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldScience),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = EmeraldScience,
                            modifier = Modifier.size(42.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "¡SÍNTESIS EXITOSA!",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Has dominado el equilibrio térmico para forjar ${node.title}. ¡La tecnología ha sido desbloqueada!",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                onCraftSuccess(node)
                                onBack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldScience, contentColor = DarkBackground),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("claim_lab_reward_btn")
                        ) {
                            Text("Reclamar Tecnología (+80 XP)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Failure Box
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = DarkSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, DangerRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = DangerRed,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Falla Térmica",
                            color = DangerRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = failureReason,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = {
                                currentTemp = minTemp
                                sweetSpotSeconds = 0f
                                gameState = "PLAYING"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberForge, contentColor = DarkBackground),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("retry_lab_btn")
                        ) {
                            Text("Reintentar Síntesis", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}
