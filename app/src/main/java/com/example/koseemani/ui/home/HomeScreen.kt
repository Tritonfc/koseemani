package com.example.koseemani.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HomeScreen(modifier: Modifier = Modifier){
    Column(verticalArrangement = Arrangement.Center) {
        Box(modifier = modifier ){
            Text(text = "Home Screen")

        }

    }
}
