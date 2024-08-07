package com.example.koseemani.ui.contacts

import android.Manifest
import android.app.Activity
import android.app.Instrumentation.ActivityResult
import android.content.ContentResolver
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.example.koseemani.R
import com.example.koseemani.data.local.Contact
import com.example.koseemani.di.KoseeViewmodelProvider
import com.example.koseemani.ui.theme.borderColor
import com.example.koseemani.ui.theme.lightGrey
import com.google.accompanist.permissions.rememberPermissionState

@Composable
fun ContactsScreen(
    modifier: Modifier = Modifier,
    contactsViewModel: ContactsViewModel = viewModel(factory = KoseeViewmodelProvider.viewModelFactory)
) {
    val uiState by contactsViewModel.homeUiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val contactsResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickContact()
    ) { result ->
        // on below line we are checking if data is not null.
        if (result != null) {
            // on below line we are getting contact data
            val contentResolver: ContentResolver = context.contentResolver
//                val contactData: Uri? = result.data?.data

            // on below line we are creating a cursor
            val cursorNull: Cursor? = contentResolver.query(result, null, null, null, null)

            // on below line we are moving cursor.
            cursorNull?.let { cursor ->
                cursor.moveToFirst()
                var number = ""

                val contactName: String =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val id =
                    cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts._ID))
                val phones: Cursor? = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + id, null, null
                )
                if (phones != null) {
                    while (phones.moveToNext()) {
                        number =
                            phones.getString(phones.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER))
//                        Log.d("Number", number)
                    }
                    phones.close()
                }
                contactsViewModel.addNewContact(Contact(name = contactName, phoneNumber = number))
                cursorNull.close()

            }


        }


    }
    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                contactsResult.launch()

            } else {


            }

        })
    Column(modifier = modifier) {

        TitleHeadline {
            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
        Spacer(modifier = Modifier.height(16.dp))

        ContactsList(contactsUiState = uiState) { contactToDelete ->
            contactsViewModel.deleteContact(contactToDelete)

        }

    }
}

fun getContactsIntent(): Intent {
    val intent = Intent(Intent.ACTION_GET_CONTENT)
    intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
    return intent
}

@Composable
fun TitleHeadline(modifier: Modifier = Modifier, onAddContact: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = "Emergency Contacts", style = MaterialTheme.typography.labelMedium.copy(
                color = Color.Black, fontWeight = FontWeight.Bold
            )
        )



        OutlinedButton(
            onClick = { onAddContact() },
            modifier = modifier
                .width(70.dp)
                .height(30.dp),
            border = BorderStroke(width = 1.dp, color = borderColor)
        ) {

            Text("Add", style = MaterialTheme.typography.titleSmall)
            Spacer(modifier = Modifier.width(4.dp))
            Icon(imageVector = Icons.Outlined.Add, contentDescription = null)
        }


    }

}

@Composable
fun ContactsList(
    modifier: Modifier = Modifier,
    contactsUiState: ContactsUiState,
    onDeleteContact: (Contact) -> Unit
) {
    LazyColumn() {
        items(contactsUiState.contacts) { contact ->
            ContactItem(contact = contact, modifier = Modifier.padding(bottom = 16.dp)) {
                onDeleteContact(contact)
            }


        }
    }

}

@Composable
fun ContactItem(modifier: Modifier = Modifier, contact: Contact, onDeleteIcon: () -> Unit) {
    Box(
        modifier = modifier
            .border(
                BorderStroke(width = 1.dp, brush = SolidColor(borderColor)),
                shape = RoundedCornerShape(3.dp)
            )
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .fillMaxWidth()
        ) {
            Column {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = lightGrey)
                )

            }

            Row {
                Image(
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = "Edit icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = "Edit icon",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(18.dp)
                        .clickable {
                            onDeleteIcon()
                        }
                )
            }




        }
    }

}