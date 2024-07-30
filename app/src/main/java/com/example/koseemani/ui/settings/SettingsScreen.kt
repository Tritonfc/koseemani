package com.example.koseemani.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.koseemani.ui.reusable.KoseeToolBar


@Composable
fun SettingsScreen(modifier: Modifier = Modifier) {
    var enableVolumeListener by remember { mutableStateOf(false) }
    val settingsList = listOf(
        SettingItem(title = "Profile Settings", iconType = SettingIconType.Arrow, subTitle = null),
        SettingItem(
            title = "Volume Button Access",
            iconType = SettingIconType.SwitchItem(isChecked = enableVolumeListener),
            subTitle = "Enable to use the volume button as a panic \n" + "button shortcut.",
        ),
        SettingItem(title = "Notifications", iconType = SettingIconType.Arrow, subTitle = null),
        SettingItem(title = "FAQs", iconType = SettingIconType.Arrow, subTitle = null),
        SettingItem(title = "About Koseemani", iconType = SettingIconType.Arrow, subTitle = null),
        SettingItem(title = "Support", iconType = SettingIconType.Arrow, subTitle = null),
        SettingItem(title = "Logout", iconType = SettingIconType.None, subTitle = null),
    )
    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        KoseeToolBar(title = "Settings")
        Column {
            settingsList.forEach { settingItem ->
                Column {
                    SettingItemView(settingItem = settingItem, onSwitchClicked = {
                        enableVolumeListener = it
                    }, navigateToPage = null)
                    Divider(color = Color.LightGray)
                }


            }
        }

    }

}

@Composable
fun SettingItemView(
    modifier: Modifier = Modifier,
    settingItem: SettingItem,
    onSwitchClicked: (Boolean) -> Unit,
    navigateToPage: (() -> Unit)?
) {
    val titleStyle = MaterialTheme.typography.titleMedium
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (settingItem.subTitle.isNullOrEmpty()) {
            Text(settingItem.title, style = titleStyle)
        } else {
            Column {
                Text(text = settingItem.title, style = titleStyle)
                Text(
                    text = settingItem.subTitle,
                    style = TextStyle(
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.W300,
                        fontSize = 10.sp,
                        letterSpacing = 0.5.sp,
                        color = Color.LightGray


                    ),
                )

            }
        }
        if (settingItem.iconType != SettingIconType.None) {
            getIconType(iconType = settingItem.iconType, onSwitchClicked = { isChecked ->
                onSwitchClicked(isChecked)
            })
        }
    }
}

@Composable
fun getIconType(
    modifier: Modifier = Modifier,
    iconType: SettingIconType,
    onSwitchClicked: (Boolean) -> Unit?
) {
    when (iconType) {
        SettingIconType.Arrow -> {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = "Navigate",
                tint = Color.LightGray
            )
        }

        is SettingIconType.SwitchItem -> {
            Switch(
                checked = iconType.isChecked,
                onCheckedChange = {
                    onSwitchClicked(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                    uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                )
            )
        }

        else -> {

        }
    }
}

data class SettingItem(
    val title: String,
    val subTitle: String?,
    val iconType: SettingIconType,

    )

sealed class SettingIconType {
    object Arrow : SettingIconType()
    data class SwitchItem(val isChecked: Boolean) : SettingIconType()

    object None : SettingIconType()
}

