package com.example.koseemani.ui.settings

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.koseemani.data.remote.GoogleDriveHelper
import com.example.koseemani.datastore.getVolumeEnabled
import com.example.koseemani.datastore.setVolumeEnabled
import com.example.koseemani.datastore.settingsDataStore
import com.example.koseemani.ui.home.uploadVideoToDrive
import com.example.koseemani.ui.reusable.KoseeToolBar
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SettingsScreen(modifier: Modifier = Modifier, onSwitchClicked: (Boolean) -> Unit) {
    var enableVolumeListener by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope { Dispatchers.Default }
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
    val context = LocalContext.current
    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleDriveHelper.getSignedInFromAccount(intent)

                    if (task.isSuccessful) {
                        saveVolumeListenPref(context, true, scope)
                        onSwitchClicked(enableVolumeListener)

                    }
                } else {
                    enableVolumeListener = false
                    Toast.makeText(context, "Google Login failed!", Toast.LENGTH_LONG).show()
                }
            } else {
                enableVolumeListener = false
                Toast.makeText(context, "Google Login Result failed!", Toast.LENGTH_LONG).show()
                Log.d("GOOGLE LOGIN", result.resultCode.toString())
            }
        }

    LaunchedEffect(Unit) {
        getVolumeEnabled(context).collect {
            enableVolumeListener = it
        }
    }

    Column(modifier = modifier.padding(horizontal = 24.dp)) {
        KoseeToolBar(title = "Settings")
        Column {
            settingsList.forEach { settingItem ->
                Column {
                    SettingItemView(settingItem = settingItem, onSwitchClicked = { enable ->
                        if (enable) {
                            startForResult.launch(GoogleDriveHelper.getGoogleSignInClient(context).signInIntent)
                        } else {
                            onSwitchClicked(enable)
                            saveVolumeListenPref(context, enable, scope)

                        }
                        enableVolumeListener = enable

                    }, navigateToPage = null)
                    Divider(color = Color.LightGray)
                }


            }
        }

    }

}

fun saveVolumeListenPref(context: Context, volumeEnabled: Boolean, coroutineScope: CoroutineScope) {
    coroutineScope.launch {
        setVolumeEnabled(context, volumeEnabled)
    }
}


@Composable
fun SettingItemView(
    modifier: Modifier = Modifier,
    settingItem: SettingItem,
    onSwitchClicked: (Boolean) -> Unit,
    navigateToPage: (() -> Unit)?
) {
    val titleStyle = MaterialTheme.typography.titleMedium.copy(
        color = if (settingItem.title == "Logout") MaterialTheme.colorScheme.error else Color.Black,
        fontWeight = FontWeight.Bold
    )
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 64.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (settingItem.subTitle.isNullOrEmpty()) {
            Text(
                settingItem.title, style = titleStyle
            )
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
    modifier: Modifier = Modifier, iconType: SettingIconType, onSwitchClicked: (Boolean) -> Unit?
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
                checked = iconType.isChecked, onCheckedChange = {
                    onSwitchClicked(it)
                }, colors = SwitchDefaults.colors(
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
    data object Arrow : SettingIconType()
    data class SwitchItem(val isChecked: Boolean) : SettingIconType()

    data object None : SettingIconType()
}

