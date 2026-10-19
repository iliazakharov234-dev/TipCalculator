package com.example.tipcalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tipcalculator.ui.theme.TipCalculatorTheme

// ============================================================
//  ЛОГИКА: скидка по количеству блюд
// ============================================================
fun discountFor(dishCount: Int): Int = when {
    dishCount <= 2 -> 3
    dishCount in 3..5 -> 5
    dishCount in 6..10 -> 7
    else -> 10
}

// ============================================================
//  MAIN ACTIVITY
// ============================================================
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TipCalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TipScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

// ============================================================
//  UI
// ============================================================
@Composable
fun TipScreen(modifier: Modifier = Modifier) {
    var orderSum by remember { mutableStateOf("") }
    var dishCount by remember { mutableStateOf("") }
    var tipPercent by remember { mutableFloatStateOf(0f) }

    val dishes = dishCount.toIntOrNull() ?: 0
    val discount = discountFor(dishes)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Заголовок
        Text(
            "Калькулятор чаевых",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
66666
        // Сумма заказа
        OutlinedTextField(
            value = orderSum,
            onValueChange = { new ->
                if (new.all { it.isDigit() || it == '.' || it == ',' }) orderSum = new
            },
            label = { Text("Сумма заказа") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Количество блюд
        OutlinedTextField(
            value = dishCount,
            onValueChange = { new ->
                if (new.all { it.isDigit() }) dishCount = new
            },
            label = { Text("Количество блюд") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Слайдер «Чаевые»
        Text("Чаевые: ${tipPercent.toInt()}%", fontSize = 16.sp)
        Slider(
            value = tipPercent,
            onValueChange = { tipPercent = it },
            valueRange = 0f..25f,
            steps = 24,        // 0..25 с шагом 1
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("0", fontSize = 12.sp)
            Text("25", fontSize = 12.sp)
        }

        // Скидка (радиокнопки)
        Text("Скидка:", fontSize = 16.sp, fontWeight = FontWeight.Medium)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(3, 5, 7, 10).forEach { percent ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.selectable(
                        selected = discount == percent,
                        onClick = { /* нельзя выбрать вручную */ }
                    )
                ) {
                    RadioButton(
                        selected = discount == percent,
                        onClick = null      // отключаем ручной выбор
                    )
                    Text("$percent%", fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Итоговый результат
        if (orderSum.isNotEmpty() && dishes > 0) {
            val sum = orderSum.replace(',', '.').toDoubleOrNull() ?: 0.0
            val tip = sum * tipPercent / 100
            val discountAmount = sum * discount / 100
            val total = sum + tip - discountAmount

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text("Сумма: ${"%.2f".format(sum)}")
                    Text("Чаевые ($tipPercent%): ${"%.2f".format(tip)}")
                    Text("Скидка ($discount%): −${"%.2f".format(discountAmount)}")
                    Divider()
                    Text(
                        "Итого: ${"%.2f".format(total)}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TipScreenPreview() {
    TipCalculatorTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            TipScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}