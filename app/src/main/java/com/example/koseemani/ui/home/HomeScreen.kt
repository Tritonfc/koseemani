package com.example.koseemani.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.koseemani.R

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
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
    Column(
        modifier = modifier.padding(horizontal = 24.dp),

        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        NameHeadline(userName = "Fisayo")
        Spacer(modifier = Modifier.height(24.dp))
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

                .fillMaxWidth(),

            )
        Spacer(modifier = Modifier.height(16.dp))

        SafetyRowLabel()
        Spacer(modifier = Modifier.height(16.dp))
        SafetyInsightRow(safetyList = safetyList)


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

            Icon(imageVector = Icons.Outlined.Notifications, contentDescription = null)


        }

    }

}

@Composable
fun InstructionText(modifier: Modifier = Modifier) {
    Text(buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W600, color = Color.Black, fontSize = 18.sp
            )
        ) {
            append("Press the ")
        }
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W600,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 18.sp
            )
        ) {
            append("Button \n")
        }
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W600, color = Color.Black, fontSize = 18.sp
            )
        ) {
            append("to alert your \n")
        }
        withStyle(
            style = SpanStyle(
                fontWeight = FontWeight.W600, color = Color.Black, fontSize = 18.sp
            )
        ) {
            append("contacts")
        }
    })
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
        Row() {
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
    HomeScreen(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    )
}

data class SafetyInsightItem(
    val title: String,
    val desc: String,
    val image: Int,
)