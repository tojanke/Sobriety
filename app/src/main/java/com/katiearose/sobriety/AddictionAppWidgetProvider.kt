package com.katiearose.sobriety

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.katiearose.sobriety.activities.Main
import com.katiearose.sobriety.utils.convertRangeToString
import com.katiearose.sobriety.utils.convertMilestoneToString
import java.time.Instant

class AddictionAppWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        appWidgetIds.forEach { appWidgetId ->
            val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0,
                Intent(context, Main::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            val addiction = Main.addictions.first()
            val nextMilestone = addiction.getNextMilestone()

            val views: RemoteViews = RemoteViews(
                context.packageName,
                R.layout.app_widget
            ).apply {
                setOnClickPendingIntent(R.id.widget_root, pendingIntent)
                setTextViewText(R.id.widgetAddictionName, addiction.name)
                setTextViewText(R.id.widgetAddictionTime, context.convertRangeToString( addiction.history.keys.last(),
                    Instant.now().toEpochMilli(), false))
                if(nextMilestone != null){
                    setProgressBar(R.id.widgetMilestoneProgress, 100,
                        addiction.calculateMilestoneProgressionPercentage(nextMilestone).second, false)
                    setTextViewText(R.id.widgetMilestoneText,
                        StringBuilder().append(
                            context.getString(R.string.next_milestone)).append(": ")
                            .append(context.convertMilestoneToString(nextMilestone)))
                }
                else {
                    setProgressBar(R.id.widgetMilestoneProgress, 100,0, true)
                    setTextViewText(R.id.widgetMilestoneText, context.getString(R.string.no_next_milestone))
                }
            }
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}