package com.robinwilson.radioguide;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import robinwilson.bbc.BBCChannel;
import robinwilson.bbc.ScheduleItem;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class ViewSchedules extends ListActivity {
	ArrayList<ScheduleItem> programmes;
	
	static final int MENU_REFRESH=0;
	
	static final int REMIND_ID=0;
	static final int REMIND_TEST_ID=1; 
	
	static final int PROGRESS_DIALOG=0;
	
	DownloadThread downloadThread;
	static ProgressDialog progressDialog;
	
	public ViewSchedules() {
		// Create a blank array list so that we can clear it without crashing
		programmes = new ArrayList<ScheduleItem>();
		
	}
	
    protected Dialog onCreateDialog(int id) {
        switch(id) {
        case PROGRESS_DIALOG:
            progressDialog = new ProgressDialog(ViewSchedules.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Loading data. Please wait...");
            downloadThread = new DownloadThread(handler);
            downloadThread.start();
            return progressDialog;
        default:
            return null;
        }
    }
    
    // Define the Handler that receives messages from the thread and update the progress
    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            boolean finished = msg.getData().getBoolean("finished");
            if (finished) {
            	// Close the dialog box as we've finished downloading and remove it from memory
            	removeDialog(PROGRESS_DIALOG);
            	
                // Tell the list to display the items we've just downloaded
        	    ViewSchedules.this.setListAdapter(new ScheduleAdaptor());
            }
        }
    };
    
    /** Nested class that does the downloading of the BBC data */
    private class DownloadThread extends Thread {
        Handler mHandler;
       
        // Constructor: initialise it with a handler
        DownloadThread(Handler h) {
            mHandler = h;
        }
       
        // Called when the thread is told to run. This sets the state to running
        // calls the routine to get the data from the BBC, and then sends a message back
        // to the calling class to say that it's finished.
        public void run() {
        	// Actually get the data
            LoadData();
            
            Message msg = mHandler.obtainMessage();
            Bundle b = new Bundle();
            b.putBoolean("finished", true);
            msg.setData(b);
            mHandler.sendMessage(msg);
        }
    }


	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        registerForContextMenu(getListView());
        
        RunUpdateData();
    }
    
    private void RunUpdateData()
    {
    	// Show the loading dialog - which starts the thread to download the BBC data
        showDialog(PROGRESS_DIALOG);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	menu.add(0, REMIND_ID, 0, "Remind me");
    	menu.add(0, REMIND_TEST_ID, 0, "Test Reminders");
    }
 
    
    /* Creates the menu items */
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_REFRESH, 0, "Refresh");
        return true;
    }

    /* Handles item selections */
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case MENU_REFRESH:
            RunUpdateData();
            return true;
        }
        return false;
    }
    
    public boolean onContextItemSelected(MenuItem item) {
    	ScheduleItem selectedProg;
    	AdapterContextMenuInfo info;
    	
        switch(item.getItemId()) {
        case REMIND_ID:
            info = (AdapterContextMenuInfo) item.getMenuInfo();
            selectedProg = programmes.get((int)info.id);
            

            // Set a reminder for this programme
            ReminderManager.SetReminder(this, selectedProg);
            return true;
//        case REMIND_TEST_ID:
//        	info = (AdapterContextMenuInfo) item.getMenuInfo();
//            selectedProg = programmes.get((int)info.id);
//            
//            Date currentDate = new Date();
//            
//            Calendar cal = Calendar.getInstance();
//            
//            cal.add(Calendar.SECOND, 10);
//            
//            
//            selectedProg.setStart(cal.getTime());
//
//            // Set a reminder for this programme
//            ReminderManager.SetReminder(this, selectedProg);
//            return true;
        }
        return super.onContextItemSelected(item);
    }
    

private void LoadData() {	
		// Get the data using the BBCChannel class
	    BBCChannel r4 = new BBCChannel();
	    r4.LoadData();
	    
	    programmes.clear();
	    
	    // Set the data from the programmes into the member variable
	    programmes = r4.mProgrammes;
	}


class ScheduleAdaptor extends ArrayAdapter<ScheduleItem> {
	ScheduleAdaptor() {
		super(ViewSchedules.this, R.layout.schedule_row, programmes);
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater=getLayoutInflater();
		
		View row = inflater.inflate(R.layout.schedule_row, parent, false);
		
		TextView title = (TextView) row.findViewById(R.id.title);
		TextView synopsis = (TextView) row.findViewById(R.id.synopsis);
		TextView start = (TextView) row.findViewById(R.id.start);
		
		ScheduleItem currentProg = programmes.get(position);
		
		title.setText(currentProg.mTitle);
		synopsis.setText(currentProg.mSynopsis);
		start.setText(currentProg.getStartHHMMString());
		
		return(row);
	}
}
}