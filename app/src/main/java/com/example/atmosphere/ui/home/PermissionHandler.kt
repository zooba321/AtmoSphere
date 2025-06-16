package com.example.atmosphere.ui.common

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun HandleLocationPermission(
    onPermissionGranted: () -> Unit,
    content: @Composable () -> Unit
) {
    // 我們請求的是粗略位置權限，這對 IP 定位已經足夠
    val locationPermissionState = rememberPermissionState(
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (locationPermissionState.status.isGranted) {
        // 如果權限已授予，直接顯示主內容
        content()
    } else {
        // 如果權限未授予，顯示請求介面
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1A1C1E)) // 使用一個深色背景
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            val textToShow = if (locationPermissionState.status.shouldShowRationale) {
                // 如果使用者之前拒絕過一次，顯示更詳細的解釋
                "位置權限是 AtmoSphere 的核心。我們需要它來自動獲取您所在地的天氣，為您呈現一個活生生的數位天氣容器。"
            } else {
                // 首次請求時的簡短說明
                "歡迎來到 AtmoSphere！請授予位置權限，讓我們為您點亮天空。"
            }

            Text(
                text = textToShow,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { locationPermissionState.launchPermissionRequest() }) {
                Text("授予權限")
            }
        }

        // 當權限狀態改變時，如果已授予，觸發回呼
        // 注意：這個 LaunchedEffect 是為了處理使用者在系統設定中手動開啟權限後返回 App 的情況
        LaunchedEffect(locationPermissionState.status) {
            if (locationPermissionState.status.isGranted) {
                onPermissionGranted()
            }
        }
    }
}