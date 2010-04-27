package robinwilson.bbc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ScheduleItem {
	public String mId;
	public String mTitle;
	public String mSynopsis;
	public String mChannelID;
	public Date mStart;
	public String mDuration;
	
	public String getStartHHMMString() {
		// Set the format that we want the output in
		DateFormat out_sdf = new SimpleDateFormat("HH:mm");
		
		// Get the current timezone to sort out Daylight Savings problems
		TimeZone curr = TimeZone.getDefault();
		int offset = (int) curr.getDSTSavings();
		
		// Get a calendar and set it to the date stored in this instance
		Calendar cal = Calendar.getInstance();
		cal.setTime(mStart);
		
		// Add the offset for Daylight Saving Time
		cal.add(Calendar.MILLISECOND, offset);
		
		// Get the result
		Date updatedDate = cal.getTime();
		
		
		// Return the formatted string
		return out_sdf.format(updatedDate);
	}
	
	public Date getStart()
	{
		return mStart;
	}
	
	public void setStart(Date mStart) {
//		// Get the current timezone to sort out Daylight Savings problems
//		TimeZone curr = TimeZone.getDefault();
//		int offset = (int) curr.getDSTSavings();
//		
//		// Get a calendar and set it to the date stored in this instance
//		Calendar cal = Calendar.getInstance();
//		cal.setTime(mStart);
//		
//		// Add the offset for Daylight Saving Time
//		cal.add(Calendar.MILLISECOND, offset);
		
		this.mStart = mStart;
	}	
}
