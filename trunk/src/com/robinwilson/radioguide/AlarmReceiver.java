package com.robinwilson.radioguide;

import java.net.URI;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent givenIntent) {
		NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		// Get the data from the bundle
		Bundle b = givenIntent.getExtras();
		String title = b.getString("title");
		String channel_id = b.getString("channel");
		String start = b.getString("start");
		
		
		int icon = R.drawable.icon;
		CharSequence tickerText = "TV Guide Notification";
		long when = System.currentTimeMillis();
		
		Notification notification = new Notification(icon, tickerText, when);
		
		context = context.getApplicationContext();
		CharSequence contentTitle = "UK Radio Guide";
		
		CharSequence contentText = title + " on " + channel_id + " at " + start;
		
		Intent notificationIntent = new Intent(context, ViewSchedules.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		
		notification.ledARGB = 0xff00ff00;
		notification.ledOnMS = 300;
		notification.ledOffMS = 1000;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
		
		
		
		//notification.sound = Uri.parse("android.resource://com.robinwilson.radioguide/" +R.raw.chimes);
		
		// Set the default notification sound
		notification.defaults |= Notification.DEFAULT_SOUND;
		
		notification.vibrate = new long[] { 0, 300, 200, 300, 400, 300 };
		
		// Actually send the notification
		nm.notify(0, notification);
		
		System.out.println("wooo");
		
	}

}
