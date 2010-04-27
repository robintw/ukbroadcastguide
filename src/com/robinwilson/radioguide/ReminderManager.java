package com.robinwilson.radioguide;

import java.util.Calendar;
import java.util.TimeZone;

import robinwilson.bbc.ScheduleItem;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ReminderManager {
	public static void SetReminder(Context currentContext, ScheduleItem programme) {
		AlarmManager am = (AlarmManager) currentContext.getSystemService(Context.ALARM_SERVICE);
		
		Intent intent = new Intent(currentContext, AlarmReceiver.class);
		intent.putExtra("title", programme.mTitle);
		intent.putExtra("synopsis", programme.mSynopsis);
		intent.putExtra("start", programme.getStartHHMMString());
		intent.putExtra("duration", programme.mDuration);
		intent.putExtra("channel", programme.mChannelID);
		
		PendingIntent pendingIntent = PendingIntent.getBroadcast(currentContext, 0, intent, Intent.FLAG_GRANT_READ_URI_PERMISSION);
		
		//Date currentTime = new Date();
		
		//Long difference = programme.getStart().getTime() - currentTime.getTime();
		
		Long preTimeInMS = (long) 5 * 60 * 1000;
		
		
		Calendar cal = Calendar.getInstance();
		
		Long alarmTime = programme.getStart().getTime() + (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET));
		
		
		System.out.println(alarmTime);
		
		// PROPER code using actual time
		am.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
		
		
		
		
		// FAKE code using now plus 10 seconds
		//am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
		
		Toast t = Toast.makeText(currentContext, "Alarm set for " + programme.getStartHHMMString(), Toast.LENGTH_LONG);
        t.show();
	}
}
