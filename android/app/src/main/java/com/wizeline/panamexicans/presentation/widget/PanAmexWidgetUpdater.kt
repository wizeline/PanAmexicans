package com.wizeline.panamexicans.presentation.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState

interface PanAmexWidgetUpdater {
    suspend fun update(uiState: PanAmexWidgetUiState)
}

class PanAmexWidgetUpdaterImpl(
    private val context: Context,
) : PanAmexWidgetUpdater {
    override suspend fun update(uiState: PanAmexWidgetUiState) {
        GlanceAppWidgetManager(context = context).getGlanceIds(PanAmexAppWidget::class.java)
            .forEach { glanceId ->
                updateAppWidgetState(context, glanceId) { prefs ->
                    prefs[PanAmexAppWidget.pref_miles_widget] = uiState.miles
                }
                PanAmexAppWidget().update(context, glanceId)
            }
    }
}
