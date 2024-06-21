package com.lifthub.lifthubandroid.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lifthub.lifthubandroid.activity.TimerActivity
import com.lifthub.lifthubandroid.timer.util.NotificationUtil
import com.lifthub.lifthubandroid.timer.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }

}