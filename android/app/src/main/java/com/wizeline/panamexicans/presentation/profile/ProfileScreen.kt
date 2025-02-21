package com.wizeline.panamexicans.presentation.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.data.models.UserData
import com.wizeline.panamexicans.navigation.AppNavRoute
import com.wizeline.panamexicans.presentation.composables.PrimaryColorButton
import com.wizeline.panamexicans.presentation.theme.Golden
import com.wizeline.panamexicans.presentation.theme.Orange

@Composable
fun ProfileRoot(navController: NavHostController, viewModel: ProfileViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    LaunchedEffect(viewModel.uiAction) {
        viewModel.uiAction.collect { event ->
            when (event) {
                is ProfileUiAction.OnAccountLoggedOut -> {
                    navController.navigate(AppNavRoute.LOGIN.toString()) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    ProfileScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
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
        ProfileHeader(uiState.userData, uiState.isPremium)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Enable your premium features", fontSize = 18.sp)
                Switch(
                    modifier = Modifier.semantics { contentDescription = "Demo with icon" },
                    checked = uiState.isPremium,
                    onCheckedChange = { onEvent(ProfileUiEvents.OnTooglePremium) },
                    thumbContent = {
                        if (uiState.isPremium) {
                            // Icon isn't focusable, no need for content description
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = null,
                                modifier = Modifier.size(SwitchDefaults.IconSize),
                            )
                        }
                    }
                )
            }
            WeeklyProgressCard(
                tasksCompleted = 2,
                totalTasks = 2,
                weeklyMiles = 10_000,
                weeklyGoalMiles = 13_000,
                streakWeeks = 3,
                isSharedRideJoined = uiState.hasJoinedSharedSession,
            )
        }
        Box(Modifier.fillMaxWidth()) {
            PrimaryColorButton(
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                "Logout",
                onClick = { onEvent(ProfileUiEvents.OnLogoutClicked) })
        }
    }
}


@Composable
fun WeeklyProgressCard(
    tasksCompleted: Int,
    totalTasks: Int,
    weeklyMiles: Int,
    weeklyGoalMiles: Int,
    streakWeeks: Int,
    isSharedRideJoined: Boolean,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .padding(vertical = 16.dp)
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.tertiaryContainer
                        )
                    )
                )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "Weekly Tasks", style = MaterialTheme.typography.headlineMedium)
                        Text(
                            text = "$tasksCompleted / $totalTasks tasks completed",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = "$streakWeeks weeks streak",
                            textAlign = TextAlign.End,
                            style = MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.onSecondaryContainer)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Image(
                            modifier = Modifier.size(32.dp),
                            painter = painterResource(id = R.drawable.ic_streak),
                            contentDescription = null
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Weekly Miles Progress",
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                val milesProgress = weeklyMiles.toFloat() / weeklyGoalMiles
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(7.dp),
                    color = Orange,
                    progress = { milesProgress },
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    text = "$weeklyMiles / $weeklyGoalMiles miles",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "You have joined a shared ride",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Joined",
                        tint = (if (isSharedRideJoined) Color(0xFF4CAF50) else Color.LightGray)
                    )
                }

            }
        }
    }
}

@Composable
fun ProfileHeader(user: UserData?, isPremium: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(102.dp)) {
            if (isPremium) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(102.dp),
                    tint = Golden,
                    painter = painterResource(id = R.drawable.ic_circle),
                    contentDescription = null
                )
            }
            Image(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(98.dp),
                painter = painterResource(id = R.drawable.ic_circle),
                contentDescription = null
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(user?.photoUrl)
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
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text(
                maxLines = 1,
                text = user?.firstName.orEmpty() + " " + user?.lastName.orEmpty(),
                fontSize = 28.sp,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = user?.email.orEmpty(),
                fontSize = 16.sp,
                maxLines = 2
            )
        }
        if (isPremium) {
            Icon(
                modifier = Modifier.size(32.dp),
                tint = Golden,
                painter = painterResource(id = R.drawable.ic_premium),
                contentDescription = null
            )
        } else {
            Spacer(modifier = Modifier.size(32.dp))
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(onEvent = {}, uiState = ProfileUiState())
}
