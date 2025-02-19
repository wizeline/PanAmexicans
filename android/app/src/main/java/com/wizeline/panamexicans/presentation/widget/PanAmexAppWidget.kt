package com.wizeline.panamexicans.presentation.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
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
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.ContentScale
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.wizeline.panamexicans.MainActivity
import com.wizeline.panamexicans.R
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PanAmexAppWidget : GlanceAppWidget() {

    companion object {
        val pref_miles_widget = intPreferencesKey("widget_miles_key")
    }

    // a way to get hilt inject what you need in non-supported class
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface WidgetMockRepositoryEntryPoint {
        fun getRepository(): WidgetMockRepository
    }

    override var stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition


    override suspend fun provideGlance(context: Context, id: GlanceId) {
        //default value
        var miles = 0
        //get settings repository from hilt
        val appContext = context.applicationContext ?: throw IllegalStateException()
        val widgetEntryPoint =
            EntryPointAccessors.fromApplication(
                appContext,
                WidgetMockRepositoryEntryPoint::class.java,
            )
        val widgetRepository = widgetEntryPoint.getRepository()

        //set value
        withContext(Dispatchers.IO) {
            miles = widgetRepository.getMiles()
        }

        provideContent {
            val prefs = currentState<Preferences>()
            val milesValue = prefs[pref_miles_widget] ?: miles
            PanAmexicansWidget(miles = milesValue)
        }
    }

    val OrangeColor = Color(0xFFFFA500)

    @Composable
    private fun PanAmexicansWidget(miles: Int) {
        Box(modifier = GlanceModifier.fillMaxSize()) {
            Image(
                modifier = GlanceModifier.fillMaxSize(),
                provider = ImageProvider(R.drawable.splash_background),
                contentScale = ContentScale.Crop,
                contentDescription = "Sample Image"
            )
            Column(
                modifier = GlanceModifier
                    .fillMaxSize()
                    .clickable(actionStartActivity<MainActivity>()),
                verticalAlignment = Alignment.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "You have ${miles} Miles",
                    modifier = GlanceModifier.padding(12.dp),
                    style = TextStyle(color = ColorProvider(color = Color.White))
                )
                LinearProgressIndicator(
                    modifier = GlanceModifier.fillMaxWidth().height(50.dp).padding(5.dp),
                    progress = miles / 100f,
                    color = ColorProvider(OrangeColor),
                    backgroundColor = ColorProvider(Color.Black)
                )

            }

        }

    }

}
