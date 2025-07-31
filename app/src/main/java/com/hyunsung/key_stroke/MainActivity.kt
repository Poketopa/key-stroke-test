package com.hyunsung.key_stroke

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hyunsung.key_stroke.data.Constants
import com.hyunsung.key_stroke.ui.TypingDNAActivity
import com.hyunsung.key_stroke.ui.theme.KeystrokeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KeystrokeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    UserSelectionScreen(
                        onSelectUser = { userId ->
                            navigateToTypingTest(userId)
                        }
                    )
                }
            }
        }
    }

    private fun navigateToTypingTest(userId: String) {
        val intent = Intent(this, TypingDNAActivity::class.java).apply {
            putExtra(Constants.EXTRA_USER_ID, userId)
        }
        startActivity(intent)
    }
}

@Composable
fun UserSelectionScreen(onSelectUser: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "TypingDNA 사용자 구분 테스트",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "테스트할 사용자를 선택하세요",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Button(
                    onClick = { onSelectUser(Constants.getUserA()) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("사용자 A 테스트", fontSize = 16.sp)
                }
                
                Button(
                    onClick = { onSelectUser(Constants.getUserB()) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("사용자 B 테스트", fontSize = 16.sp)
                }
                
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                
                Button(
                    onClick = { onSelectUser("verify") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text("유저 검증하기", fontSize = 16.sp)
                }
            }
        }
    }
}
