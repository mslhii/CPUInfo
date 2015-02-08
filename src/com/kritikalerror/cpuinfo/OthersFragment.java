package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.kritikalerror.cpuinfo.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class OthersFragment extends Fragment {
	
	public TextView mFragmentText;
	protected Context mContext;
	private String mTopString;
	private String mParamString;
	private LinearLayout mLayout;
	
	private Thread mCollectLogThread;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_misc, container, false);
		
		mContext = rootView.getContext();
		mTopString = "";
		mParamString = "";
		
		mLayout = (LinearLayout) rootView.findViewById(R.id.ll);
		final EditText procField = (EditText) rootView.findViewById(R.id.search);
		Button refreshButton = (Button) rootView.findViewById(R.id.refresh);
		refreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				mParamString = procField.getText().toString();
				if(mParamString.equals(""))
				{
					Toast.makeText(mContext, "Please enter a valid process name!", Toast.LENGTH_SHORT).show();
				}
				else
				{
					mCollectLogThread = new Thread(new CollectPSRunnable());
					mCollectLogThread.start();
				}
			}
			
		});
		
		mFragmentText = (TextView) rootView.findViewById(R.id.miscs);
		mFragmentText.setMovementMethod(new ScrollingMovementMethod());
		mFragmentText.setHorizontallyScrolling(true);
		mFragmentText.setTypeface(Typeface.MONOSPACE);
		
		setHasOptionsMenu(true);
		return rootView;
	} 
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	    // TODO Add your menu entries here
		menu.add(0, 1, 0, "Help");
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 1:
			mFragmentText.setText("Usage: ps [-t] [-x] [-P] [-p] [c] [pid] [name]\n" +
					"-t show threads, comes up with threads in the list\n" +
					"-x shows time, user time and system time in seconds\n" +
					"-P show scheduling policy, either bg or fg are common,\n" +
					"but also un and er for failures to get policy\n" +
					"-p show priorities, niceness level\n" +
					"-c show CPU (may not be available prior to Android 4.x) involved\n" +
					"[pid] filter by PID if numeric, or...\n" +
					"[name] ...filter by process name");
		}
		return super.onOptionsItemSelected(item);
	}
	
	private class CollectPSRunnable implements Runnable {

		@SuppressLint("DefaultLocale")
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mTopString.equals(""))
			{
				threadMessage("Running ps command...");
			}
			
			final StringBuilder log = new StringBuilder();
			int i = 0;
			try
			{
				ArrayList<String> commandLine = new ArrayList<String>();
				commandLine.add("ps");
				commandLine.add("-x");
				commandLine.add("-p");
				commandLine.add("-P");
				commandLine.add("-c");
				commandLine.add(mParamString);
				
				Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String line;
				while ((line = bufferedReader.readLine()) != null){ 
					line = line.replaceAll(" ", "\t");
					log.append(line);
					log.append("\n"); 
				}
				
				String result = log.toString();
				if((result.length() == 0) || (result == null))
				{
					threadMessage("PS cannot run!\n" +
							"You may not have root access\n" +
							"or top installed.");
				}
				else
				{
					threadMessage("\n" + result);
				}
				
				Thread.sleep(1000);
				
				log.setLength(0);
				
				Log.e("PS", "Ran " + i + " iterations of ps with " + log.capacity() + " capacity.");
				i++;
			} 
			catch (IOException e){
				Log.e("CPU INFO", "Getting ps failed", e);
				threadMessage("Cannot run ps");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Log.e("PS", "Thread finished!");
		}
		
		private void threadMessage(String message) {
            if (!message.equals(null) && !message.equals("")) {
                Message messageObj = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("message", message);
                messageObj.setData(bundle);
                handler.sendMessage(messageObj);
            }
        }
		
		private final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				mFragmentText.setText(message.getData().getString("message"));
			}
		};
	}
}
