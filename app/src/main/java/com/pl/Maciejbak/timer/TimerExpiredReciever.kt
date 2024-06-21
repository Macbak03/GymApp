package com.pl.Maciejbak.timer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.pl.Maciejbak.activity.TimerActivity
import com.pl.Maciejbak.timer.util.NotificationUtil
import com.pl.Maciejbak.timer.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
        PrefUtil.setAlarmSetTime(0, context)
    }

}