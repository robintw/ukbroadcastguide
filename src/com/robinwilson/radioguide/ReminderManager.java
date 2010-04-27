package com.robinwilson.radioguide;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
		
		// Five minutes before in milliseconds
		Long preTimeInMS = (long) 5 * 60 * 1000;
		
		
		Calendar cal = Calendar.getInstance();
		
		Long alarmTime = programme.getStart().getTime() + (cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET));
		
		alarmTime = alarmTime - preTimeInMS;
		
		System.out.println(alarmTime);
		
		// PROPER code using actual time
		am.set(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
		
		
		
		
		// FAKE code using now plus 10 seconds
		//am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent);
		
		// Set the format that we want the output in
		DateFormat out_sdf = new SimpleDateFormat("HH:mm");
		
		Date alarmDate = new Date();
		
		alarmDate.setTime(alarmTime);
		
		// Return the formatted string
		String alarmTimeString = out_sdf.format(alarmDate);
		
		Toast t = Toast.makeText(currentContext, "Alarm set for " + alarmTimeString, Toast.LENGTH_LONG);
        t.show();
	}
}
