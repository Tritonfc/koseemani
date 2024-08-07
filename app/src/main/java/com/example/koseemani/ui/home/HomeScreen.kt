package com.example.koseemani.ui.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.koseemani.R
import com.example.koseemani.data.remote.GoogleDriveHelper
import com.example.koseemani.di.KoseeViewmodelProvider
import com.example.koseemani.service.VideoRecordService
import com.example.koseemani.ui.contacts.ContactsViewModel
import com.example.koseemani.utils.SMSManager
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    snackbarHostState: SnackbarHostState,
    videoRecordService: VideoRecordService?,
    contactsViewModel: ContactsViewModel = viewModel(factory = KoseeViewmodelProvider.viewModelFactory),
    onSOSClicked: () -> Unit
) {
    val contactsUiState by contactsViewModel.homeUiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope { Dispatchers.Default }
//    val sosReceiver = SOSBroadcastReceiver()
//    val filter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")

    var isRecording by remember {
        mutableStateOf(false)

    }
    val context = LocalContext.current
    var isPermitted by remember {
        mutableStateOf(false)
    }

    var videoFilePath by remember {
        mutableStateOf("")
    }
    var currLocation by rememberSaveable {
        mutableStateOf("")
    }
    val smsPermissionState = rememberPermissionState(Manifest.permission.SEND_SMS)
    val locationPermissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,


            )
    )

    val startForResult =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                if (result.data != null) {
                    val task: Task<GoogleSignInAccount> =
                        GoogleDriveHelper.getSignedInFromAccount(intent)

                    if (task.isSuccessful) {
                        uploadVideoToDrive(scope, context, videoFilePath) { videoLink ->
                            val contacts = contactsUiState.contacts.map {contact->
                                contact.phoneNumber.filterNot {char->
                                    char.isWhitespace()
                                }
                            }.toList()
                            val firstMessagePart =
                                "SOS,I am in danger and located at: $currLocation."
                            val secondMessagePart = "Here's a clip of me:"
                            val messagesPart = arrayListOf(
                                firstMessagePart,
                                secondMessagePart,
                                videoLink

                            )
                            SMSManager.sendSOSMessage(
                                messages = messagesPart,
                                emergencyContacts = contacts,

                                )




                            isRecording = false
                        }
                    } else {
                        Toast.makeText(context, "Google Login Failed", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                isRecording = false
                Toast.makeText(context, "Google Login Result failed!", Toast.LENGTH_LONG).show()
                Log.d("GOOGLE LOGIN", result.resultCode.toString())
            }
        }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                if(contactsUiState.contacts.isEmpty()){
                    snackbarImpl("Please add an emergency contact",scope, snackbarHostState = snackbarHostState)

                }else{
                    onSOSClicked()
                    isRecording = true
                }

//                context.registerReceiver(sosReceiver,filter)
//                SMSManager.sendSOSMessage(
//                    "SOS, I am in danger. I am currently located at $currLocation",
//                    emergencyContacts = testContacts
//
//                )

            } else {


            }

        })
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val areGranted =
            if (permissions.isEmpty()) locationPermissionState.allPermissionsGranted else permissions.values.reduce { acc, next -> acc && next }
        if (areGranted) {
            isPermitted = true
        } else {
            // Show dialog
        }


    }


    val safetyList = listOf<SafetyInsightItem>(
        SafetyInsightItem(
            title = "Stay vigilant",
            desc = "Practice environmental awareness",
            image = R.drawable.stay_vigilant_raw
        ),

        SafetyInsightItem(
            title = "Tell a friend",
            desc = "Share your whereabouts",
            image = R.drawable.tell_a_friend_raw
        )

    )

    if (videoRecordService != null) {
        videoRecordService.getVideoFile = { videoUri ->

            videoFilePath = videoUri


            startForResult.launch(GoogleDriveHelper.getGoogleSignInClient(context).signInIntent)


        }

    }

    Column(
        modifier = modifier.padding(horizontal = 24.dp),

        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        NameHeadline(userName = "Fisayo")
        Spacer(modifier = Modifier.height(24.dp))


        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {

                CurrentLocationField {
                    currLocation = it
                }
            }


            else -> {
                // You can directly ask for the permission.
                // The registered ActivityResultCallback gets the result of this request.
                LaunchedEffect(Unit) {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,

                            )
                    )
                }
            }

        }





        Spacer(modifier = Modifier.height(32.dp))
        InstructionText(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = painterResource(id = R.drawable.sos_button_vector),
            contentDescription = "Button",
            alignment = Alignment.Center,
            modifier = Modifier
                .align(alignment = Alignment.CenterHorizontally)
                .size(160.dp)
                .clickable(onClick = {
                    if (locationPermissionState.allPermissionsGranted) {
                        requestPermissionLauncher.launch(Manifest.permission.SEND_SMS)
                    } else {
                        handleLocationPermissions(locationPermissionState) {
                            snackbarImpl(it, scope,"Request Permission", snackbarHostState,) {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                    )
                                )

                            }
                        }
                    }

                }
                )


                .fillMaxWidth(),

            )

        Spacer(modifier = Modifier.height(16.dp))

        SafetyRowLabel()
        Spacer(modifier = Modifier.height(16.dp))
        SafetyInsightRow(safetyList = safetyList)


    }
    if (isRecording) {
        RecordingVideoOverlay()
    }
}

fun uploadVideoToDrive(
    scope: CoroutineScope, context:
    Context, videoFilePath: String, onCompletedUpload: (String) -> Unit
) {
    scope.launch(Dispatchers.IO) {
        GoogleDriveHelper.uploadVideoToDrive(context,videoFilePath){
            onCompletedUpload(it)
        }
    }
}

@Composable
fun NameHeadline(modifier: Modifier = Modifier, userName: String) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(text = "Hi  $userName", style = MaterialTheme.typography.labelSmall)

        Row {
            Image(
                modifier = Modifier
                    .size(30.dp)
                    .clip(CircleShape),
                painter = painterResource(id = R.drawable.sample_profile),
                contentDescription = "My profile pic"
            )
            Spacer(modifier = Modifier.width(8.dp))

            Icon(imageVector = Icons.Outlined.Notifications, contentDescription = null)


        }

    }

}

@Composable
fun InstructionText(modifier: Modifier = Modifier) {
    val spanStyle = SpanStyle(
        fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp
    )
    Text(buildAnnotatedString {
        withStyle(
            style = spanStyle
        ) {
            append("Press the ")
        }
        withStyle(
            style = spanStyle.copy(color = MaterialTheme.colorScheme.primary)
        ) {
            append("Button \n")
        }
        withStyle(
            style = spanStyle
        ) {
            append("to alert your \ncontacts")
        }

    })
}


@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrentLocationField(
    modifier: Modifier = Modifier,
    onTextChanged: (String) -> Unit

) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    val locationEnabled = isLocationEnabled(context)
    var locationInfo by remember {
        mutableStateOf("")
    }

    if (locationEnabled) {
        scope.launch(Dispatchers.IO) {
            locationClient.lastLocation.addOnSuccessListener { location ->
                locationInfo = if (location == null) {
                    "No last known location. Try fetching the current location first"
                } else {
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val list: MutableList<Address>? =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    list?.get(0)?.getAddressLine(0) ?: "No location found"

                }

            }
            onTextChanged(locationInfo)
        }
    } else {
        locationInfo = "Please enable device location to see current location"
    }

    TextField(
        modifier = modifier.fillMaxWidth(),
        value = locationInfo,
        singleLine = true,
        onValueChange = {
            onTextChanged(it)
        },
        readOnly = true,
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.ic_location_icon),
                contentDescription = "Map Icon"
            )
        },
        isError = !locationEnabled,

        textStyle = MaterialTheme.typography.titleSmall,
        colors = TextFieldDefaults.textFieldColors(
            containerColor = MaterialTheme.colorScheme.surface,
            textColor = Color(0x99000000)
        )
    )

}

private fun isLocationEnabled(context: Context): Boolean {

    val locationManager: LocationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}

@Composable
fun SafetyRowLabel(modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        Text("Safety Insights", style = MaterialTheme.typography.labelMedium)

        TextButton(onClick = { /*TODO*/ }) {
            Text(text = "View all", style = MaterialTheme.typography.titleSmall)
        }
    }

}

@Composable
fun SafetyInsightRow(modifier: Modifier = Modifier, safetyList: List<SafetyInsightItem>) {
    LazyRow(horizontalArrangement = Arrangement.SpaceEvenly, modifier = modifier.fillMaxWidth()) {
        items(safetyList) { safetyItem ->
            SafetyInsightCard(safetyItem = safetyItem, modifier = Modifier.padding(end = 16.dp))

        }

    }

}

@Composable
fun SafetyInsightCard(modifier: Modifier = Modifier, safetyItem: SafetyInsightItem) {
    Column(
        modifier = modifier.clickable(onClick = {

        })

    ) {

        Image(
            painter = painterResource(id = safetyItem.image),
            contentDescription = null,
            modifier = Modifier
                .width(180.dp)
                .height(90.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = safetyItem.title, style = MaterialTheme.typography.titleMedium)
            Icon(imageVector = Icons.Outlined.ArrowForward, contentDescription = null)
        }

        Text(
            text = safetyItem.desc,
            style = TextStyle(
                fontFamily = FontFamily.Default,
                fontWeight = FontWeight.W300,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                letterSpacing = 0.5.sp,
                color = Color.Black
            ),
        )


    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
//    HomeScreen(
//        modifier = Modifier
//            .fillMaxWidth()
//            .fillMaxHeight()
//    )
}

@OptIn(ExperimentalPermissionsApi::class)

fun snackbarImpl(
    message: String,
    scope: CoroutineScope,
    actionLabel : String = "",
    snackbarHostState: SnackbarHostState,

    onActionClicked: (() -> Unit)? = null,
) {

    scope.launch {
        val result = snackbarHostState.showSnackbar(
            message = message,
            actionLabel = actionLabel,
            duration = SnackbarDuration.Indefinite
        )

        when (result) {
            SnackbarResult.ActionPerformed -> {

                if (onActionClicked != null) {
                    onActionClicked()
                }

            }

            SnackbarResult.Dismissed -> {
                /* Handle snackbar dismissed */
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun checkLocationPermission(locationPermissionState: MultiplePermissionsState) {
    locationPermissionState.launchMultiplePermissionRequest()
}

@Composable
fun RecordingVideoOverlay(modifier: Modifier = Modifier) {
    Surface(
        color = Color.Black, modifier = modifier

            .fillMaxSize()
            .alpha(0.9f)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.left_wifi_vector),
                contentDescription = "left signal",
            )
            Image(
                painter = painterResource(id = R.drawable.sos_button_activated),
                contentDescription = "button"
            )
            Image(
                painter = painterResource(id = R.drawable.right_wifi_vector),
                contentDescription = "right signal",
            )
        }


    }
}

@OptIn(ExperimentalPermissionsApi::class)
fun handleLocationPermissions(
    locationPermissionsState: MultiplePermissionsState,
    showSnackBar: (String) -> Unit
) {
    val allPermissionsRevoked =
        locationPermissionsState.permissions.size ==
                locationPermissionsState.revokedPermissions.size

    if (!allPermissionsRevoked) {
        // If not all the permissions are revoked, it's because the user accepted the COARSE
        // location permission, but not the FINE one.
        showSnackBar(
            "Yay! Thanks for letting me access your approximate location. " +
                    "But you know what would be great? If you allow me to know where you " +
                    "exactly are. Thank you!"
        )

    } else if (locationPermissionsState.shouldShowRationale) {
        // Both location permissions have been denied

        showSnackBar(
            "Getting your exact location is important for this app. " +
                    "Please grant us fine location. Thank you :D"
        )
    } else if (locationPermissionsState.revokedPermissions.size == locationPermissionsState.permissions.size) {
        // First time the user sees this feature or the user doesn't want to be asked again

        showSnackBar("This app requires location permission")
    }


}


data class SafetyInsightItem(
    val title: String,
    val desc: String,
    val image: Int,
)