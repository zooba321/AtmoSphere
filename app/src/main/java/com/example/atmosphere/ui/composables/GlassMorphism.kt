package com.example.atmosphere.ui.composables

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@SuppressLint("NewApi")
fun Modifier.glassMorphism(
    shape: Shape,
    backgroundColor: Color = Color.White.copy(alpha = 0.1f),
    borderColor: Color = Color.White.copy(alpha = 0.2f),
) = this.then(
    Modifier
        .clip(shape)
        .then(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                Modifier.graphicsLayer(
                    renderEffect = android.graphics.RenderEffect
                        .createBlurEffect(20f, 20f, android.graphics.Shader.TileMode.DECAL)
                        .asComposeRenderEffect()
                )
            } else {
                Modifier
            }
        )
        .background(
            color = backgroundColor,
            shape = shape
        )
        .border(
            width = 1.dp,
            color = borderColor,
            shape = shape
        )
)