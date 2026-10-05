package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TechNode
import com.example.service.FeynmanEvaluation
import com.example.ui.theme.AmberForge
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.EmeraldScience
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

data class FeynmanChatMessage(
    val sender: String, // "USER" or "AI"
    val text: String,
    val isPassed: Boolean? = null
)

@Composable
fun FeynmanTestScreen(
    node: TechNode,
    isEvaluating: Boolean,
    lastResult: FeynmanEvaluation?,
    onSubmitExplanation: (String) -> Unit,
    onBack: () -> Unit
) {
    BackHandler(onBack = onBack)

    var userText by remember { mutableStateOf("") }
    val chatHistory = remember {
        mutableStateListOf(
            FeynmanChatMessage(
                sender = "AI",
                text = "Bienvenido a la prueba de Feynman sobre ${node.title}. Imagina que estás varado en la naturaleza y debes explicarle a un aprendiz exactamente cómo y por qué funciona esto desde sus primeros principios físicos/químicos. No uses definiciones memorizadas vacías; describe los mecanismos microscópicos o de energía que hacen que suceda."
            )
        )
    }

    // Append AI response when evaluation completes
    androidx.compose.runtime.LaunchedEffect(lastResult) {
        lastResult?.let { eval ->
            val aiMessage = if (eval.isPassed) {
                "${eval.feedback}\n\n¡Has demostrado comprensión profunda desde primeros principios! La maestría de ${node.title} ha sido certificada (+150 XP)."
            } else {
                "${eval.feedback}\n\nPregunta Socrática: ${eval.socraticQuestion ?: "¿Cuál es la causa física fundamental de este fenómeno?"}"
            }
            chatHistory.add(FeynmanChatMessage(sender = "AI", text = aiMessage, isPassed = eval.isPassed))
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
                .padding(16.dp)
        ) {
            // Header
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FILTRO DE FEYNMAN (IA)",
                            color = CyanAccent,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = node.title,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Chat & Feedback Scroll Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                chatHistory.forEach { msg ->
                    val isAi = msg.sender == "AI"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = if (isAi) Arrangement.Start else Arrangement.End
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isAi) 2.dp else 12.dp,
                                bottomEnd = if (isAi) 12.dp else 2.dp
                            ),
                            color = when {
                                msg.isPassed == true -> Color(0xFF0F3A2A)
                                isAi -> DarkSurfaceElevated
                                else -> CyanAccent.copy(alpha = 0.2f)
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                when {
                                    msg.isPassed == true -> EmeraldScience
                                    isAi -> DarkSurfaceBorder
                                    else -> CyanAccent
                                }
                            ),
                            modifier = Modifier.fillMaxWidth(0.92f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isAi) Icons.Default.Psychology else Icons.Default.Science,
                                        contentDescription = null,
                                        tint = if (msg.isPassed == true) EmeraldScience else if (isAi) CyanAccent else TextPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isAi) "TUTOR SOCRÁTICO (GEMINI)" else "TU EXPLICACIÓN",
                                        color = if (msg.isPassed == true) EmeraldScience else if (isAi) CyanAccent else TextSecondary,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = msg.text,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 19.sp
                                )
                            }
                        }
                    }
                }

                // AI Evaluating Indicator
                if (isEvaluating) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkSurface,
                            border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = CyanAccent,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "El tutor socrático está analizando tu razonamiento...",
                                    color = CyanAccent,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input Form
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = userText,
                        onValueChange = { userText = it },
                        placeholder = {
                            Text(
                                text = "Escribe tu explicación desde primeros principios (ej. 'Al calentar la malaquita, el monóxido de carbono roba los átomos de oxígeno porque...')",
                                color = TextMuted,
                                fontSize = 12.5.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanAccent,
                            unfocusedBorderColor = DarkSurfaceBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            cursorColor = CyanAccent
                        ),
                        minLines = 3,
                        maxLines = 5,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("feynman_text_input")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${userText.length} caracteres",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )

                        Button(
                            onClick = {
                                if (userText.isNotBlank() && !isEvaluating) {
                                    val textToSend = userText
                                    chatHistory.add(FeynmanChatMessage(sender = "USER", text = textToSend))
                                    userText = ""
                                    onSubmitExplanation(textToSend)
                                }
                            },
                            enabled = userText.isNotBlank() && !isEvaluating,
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent, contentColor = DarkBackground),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("submit_feynman_btn")
                        ) {
                            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Evaluar Comprensión", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
