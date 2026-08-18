package com.example.ui.apps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.GalaxySystemState
import com.example.system.GalaxyViewModel
import com.example.ui.theme.*

@Composable
fun CalculatorApp(
    viewModel: GalaxyViewModel,
    state: GalaxySystemState,
    modifier: Modifier = Modifier
) {
    var displayExpression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("0") }

    fun calculateResult(expr: String): String {
        try {
            val clean = expr.replace("×", "*").replace("÷", "/")
            if (clean.isBlank()) return "0"
            // Simple arithmetic parser
            val tokens = mutableListOf<String>()
            var currentNum = ""
            for (char in clean) {
                if (char in "+-*/%") {
                    if (currentNum.isNotEmpty()) {
                        tokens.add(currentNum)
                        currentNum = ""
                    }
                    tokens.add(char.toString())
                } else {
                    currentNum += char
                }
            }
            if (currentNum.isNotEmpty()) tokens.add(currentNum)

            if (tokens.isEmpty()) return "0"

            var total = tokens[0].toDoubleOrNull() ?: 0.0
            var i = 1
            while (i < tokens.size - 1) {
                val op = tokens[i]
                val nextVal = tokens[i + 1].toDoubleOrNull() ?: 0.0
                when (op) {
                    "+" -> total += nextVal
                    "-" -> total -= nextVal
                    "*" -> total *= nextVal
                    "/" -> total = if (nextVal != 0.0) total / nextVal else 0.0
                    "%" -> total = total % nextVal
                }
                i += 2
            }
            return if (total % 1.0 == 0.0) total.toLong().toString() else String.format(java.util.Locale.US, "%.4f", total).trimEnd('0').trimEnd('.')
        } catch (_: Exception) {
            return "Lỗi"
        }
    }

    Scaffold(
        modifier = modifier
            .testTag("samsung_calculator_app")
            .fillMaxSize(),
        containerColor = if (state.isDarkMode) OneUIDarkBg else OneUILightBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Calculator Display Screen
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = displayExpression,
                    color = if (state.isDarkMode) OneUIDarkTextSecondary else OneUILightTextSecondary,
                    fontSize = 22.sp,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    text = resultText,
                    color = if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary,
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Light,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Divider(
                color = if (state.isDarkMode) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Button Matrix (4x5)
            val buttons = listOf(
                listOf("C", "()", "%", "÷"),
                listOf("7", "8", "9", "×"),
                listOf("4", "5", "6", "-"),
                listOf("1", "2", "3", "+"),
                listOf("+/-", "0", ".", "=")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        row.forEach { label ->
                            val isOperator = label in listOf("÷", "×", "-", "+", "=")
                            val isSpecial = label in listOf("C", "()", "%", "+/-")

                            val bgColor = when {
                                label == "=" -> Color(0xFF2ECC71)
                                isOperator -> if (state.isDarkMode) Color(0xFF2B3340) else Color(0xFFE2EAF8)
                                isSpecial -> if (state.isDarkMode) Color(0xFF252830) else Color(0xFFE8ECEF)
                                else -> if (state.isDarkMode) OneUIDarkCard else OneUILightCard
                            }

                            val textColor = when {
                                label == "=" -> Color.White
                                isOperator -> SamsungBlue
                                isSpecial -> if (label == "C") Color(0xFFFF3B30) else (if (state.isDarkMode) Color.White else Color.Black)
                                else -> if (state.isDarkMode) OneUIDarkTextPrimary else OneUILightTextPrimary
                            }

                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(bgColor)
                                    .clickable {
                                        viewModel.vibrateShort(15)
                                        when (label) {
                                                "C" -> {
                                                    displayExpression = ""
                                                    resultText = "0"
                                                }
                                                "=" -> {
                                                    if (displayExpression.isNotEmpty()) {
                                                        resultText = calculateResult(displayExpression)
                                                    }
                                                }
                                                "+/-" -> {
                                                    if (displayExpression.startsWith("-")) {
                                                        displayExpression = displayExpression.removePrefix("-")
                                                    } else {
                                                        displayExpression = "-$displayExpression"
                                                    }
                                                    resultText = calculateResult(displayExpression)
                                                }
                                                else -> {
                                                    displayExpression += label
                                                    if (label !in listOf("÷", "×", "-", "+", "%")) {
                                                        resultText = calculateResult(displayExpression)
                                                    }
                                                }
                                            }
                                        },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = textColor,
                                    fontSize = 24.sp,
                                    fontWeight = if (isOperator || label == "=") FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
