package com.wizeline.panamexicans.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.data.models.UserData

@Composable
fun ProfileRoot(viewModel: ProfileViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        uiState = uiState,
        onEvent = { event ->
            when (event) {
                else -> Unit
            }
        }
    )
}

@Composable
fun ProfileScreen(
    onEvent: (ProfileUiEvents) -> Unit,
    uiState: ProfileUiState
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 16.dp)
    ) {
        ProfileHeader(
            UserData(
                firstName = "Alberto",
                lastName = "Carrillo",
                email = "jacpalberto@hotmail.com"
            )
        )
    }
}

@Composable
fun ProfileHeader(user: UserData) {
    Row {
        Box(modifier = Modifier.size(98.dp)) {
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(98.dp),
                painter = painterResource(id = R.drawable.ic_circle),
                contentDescription = null
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user.photoUrl)
                    .crossfade(true)
                    .transformations(CircleCropTransformation())
                    .build(),
                contentDescription = null,

                modifier = Modifier
                    .align(Alignment.Center)
                    .size(85.dp),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_profile_placeholder)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier
            .weight(1f)
            .align(Alignment.CenterVertically)) {
            Text(
                maxLines = 1,
                text = user.firstName + " " + user.lastName,
                fontSize = 28.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = user.email.orEmpty(),
                fontSize = 16.sp,
                maxLines = 2
            )
        }
        Box(
            Modifier
                .size(40.dp)
                .clickable { }
                .background(MaterialTheme.colorScheme.background, CircleShape)
                .align(Alignment.CenterVertically)
        ) {
            return
            IconButton(
                onClick = { },
                colors = IconButtonDefaults.iconButtonColors()
                    .copy(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.background
                    )
            ) {
                Icon(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center),
                    painter = painterResource(id = R.drawable.ic_edit),
                    contentDescription = null
                )
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(onEvent = {}, uiState = ProfileUiState())
}
