package com.wizeline.panamexicans.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.wizeline.panamexicans.MainActivity
import com.wizeline.panamexicans.R
import com.wizeline.panamexicans.data.shareddata.SharedDataRepository
import com.wizeline.panamexicans.presentation.theme.Orange
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent


const val WEEKLY_MILES = 10000F

class PanAmexAppWidget : GlanceAppWidget() {

    companion object {
        val pref_miles_widget = intPreferencesKey("widget_miles_key")
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SharedDataRepositoryEntryPoint {
        fun getSharedDataRepository(): SharedDataRepository
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val sharedDataRepositoryEntry =
            EntryPointAccessors.fromApplication(
                appContext,
                SharedDataRepositoryEntryPoint::class.java,
            )
        val sharedDataRepository = sharedDataRepositoryEntry.getSharedDataRepository()
        val miles = sharedDataRepository.getMilesCounter()

        provideContent {
            val prefs = currentState<Preferences>()
            val milesValue = prefs[pref_miles_widget] ?: miles
            PanAmexicansWidget(miles = milesValue.toInt())
        }
    }

    @Composable
    private fun PanAmexicansWidget(miles: Int) {
        Box(modifier = GlanceModifier.fillMaxSize()) {
            Image(
                modifier = GlanceModifier.fillMaxSize(),
                provider = ImageProvider(R.drawable.splash_background),
                contentScale = ContentScale.Crop,
                contentDescription = ""
            )

            Box(
                modifier = GlanceModifier.background(ColorProvider(Color(0x99000000)))
                    .cornerRadius(16.dp)
            ) {
                Column(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .padding(10.dp)
                        .clickable(actionStartActivity<MainActivity>()),
                    verticalAlignment = Alignment.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    WeeklyTasks()
                    MilesProgress(miles = miles)
                    JoinedSharedRide()

                }
            }

        }
    }

    @Composable
    private fun MilesProgress(miles: Int) {
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "Weekly miles progress",
                modifier = GlanceModifier.padding(top = 12.dp, start = 5.dp),
                style = TextStyle(color = ColorProvider(color = Color.White))
            )
        }
        val progressPercentage = miles / WEEKLY_MILES
        LinearProgressIndicator(
            modifier = GlanceModifier.fillMaxWidth().height(10.dp).padding(horizontal = 5.dp),
            progress = progressPercentage,
            color = ColorProvider(Orange),
            backgroundColor = ColorProvider(Color.Black)
        )
        Row(
            modifier = GlanceModifier.fillMaxWidth().padding(bottom = 10.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "$miles / ${WEEKLY_MILES.toInt()}",
                modifier = GlanceModifier.padding(top = 12.dp),
                style = TextStyle(color = ColorProvider(color = Color.White))
            )
        }
    }

    @Composable
    private fun JoinedSharedRide() {
        Row(modifier = GlanceModifier.fillMaxWidth()) {
            Text(
                text = " ✅ You have joined a shared ride",
                style = TextStyle(color = ColorProvider(color = Color.White))
            )
        }
    }

    @Composable
    private fun WeeklyTasks() {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(modifier = GlanceModifier.padding(end = 20.dp)) {
                Text(
                    text = "Weekly Tasks",
                    style = TextStyle(
                        color = ColorProvider(color = Color.White),
                        fontSize = 18.sp
                    ),
                )
                Text(
                    text = "2/2 tasks completed",
                    style = TextStyle(
                        color = ColorProvider(color = Color.White),
                        fontSize = 12.sp
                    ),
                )
            }
            Row {
                Text(
                    text = "3 weeks streak",
                    style = TextStyle(
                        color = ColorProvider(color = Color.White),
                        fontSize = 12.sp
                    ),
                )
                Spacer(GlanceModifier.width(8.dp))
                Image(
                    modifier = GlanceModifier.size(16.dp),
                    provider = ImageProvider(R.drawable.ic_streak),
                    contentDescription = null
                )
            }
        }
    }

    @Preview
    @Composable
    private fun WidgetPreview() {
        PanAmexicansWidget(miles = 2000)
    }
}
