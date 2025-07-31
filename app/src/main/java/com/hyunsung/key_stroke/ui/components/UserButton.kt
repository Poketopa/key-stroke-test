package com.hyunsung.key_stroke.ui.components

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun UserButton(
    userId: String,
    modifier: Modifier = Modifier,
    onClick: (String) -> Unit
) {
    Button(
        onClick = { onClick(userId) },
        modifier = modifier
    ) {
        Text(text = "사용자 $userId 테스트")
    }
}
