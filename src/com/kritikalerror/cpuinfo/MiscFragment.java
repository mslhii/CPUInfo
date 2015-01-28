package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.example.cpuinfo.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MiscFragment extends Fragment {

	public TextView mFragmentText;
	protected Context mContext;
	private String mTopString;
	private String mHeaderString;
	private ArrayList<String[]> mTableString;
	private Thread mCollectLogThread;
	private boolean mPauseFlag = false;

	final private int NUM_COLUMNS = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_processes, container, false);
		mContext = rootView.getContext();
		mHeaderString = "";
		mTopString = "";
		mTableString = new ArrayList<String[]>();
		mCollectLogThread = new Thread(new CollectLogRunnable());

		Button pauseButton = (Button) rootView.findViewById(R.id.pause);
		pauseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//new CollectLogTask().execute(new ArrayList<String>());
				if(!mPauseFlag)
				{
					Toast.makeText(mContext, "Pausing...", Toast.LENGTH_SHORT).show();
					mPauseFlag = true;
				}
				else
				{
					Toast.makeText(mContext, "Starting...", Toast.LENGTH_SHORT).show();
					mPauseFlag = false;
					mCollectLogThread = new Thread(new CollectLogRunnable());
					mCollectLogThread.start();
				}
			}

		});

		mFragmentText = (TextView) rootView.findViewById(R.id.tops);
		mFragmentText.setMovementMethod(new ScrollingMovementMethod());
		mFragmentText.setHorizontallyScrolling(true);
		mFragmentText.setTypeface(Typeface.MONOSPACE);
		if (mTopString.equals(""))
		{
			//new CollectLogTask().execute(new ArrayList<String>());
			mCollectLogThread.start();
		}

		return rootView;
	}
	
	private class CollectLogRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mTopString.equals(""))
			{
				//mFragmentText.setText("Running top command...");
				threadMessage("Running top command...");
			}
			
			final StringBuilder log = new StringBuilder();
			int i = 0;
			while(!mPauseFlag)
			{
				try
				{
					ArrayList<String> commandLine = new ArrayList<String>();
					commandLine.add("top");
					commandLine.add("-n");
					commandLine.add("1");
					
					Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	
					String line;
					while ((line = bufferedReader.readLine()) != null){ 
						line = line.replaceAll(" ", "\t");
						log.append(line);
						log.append("\n"); 
					}
					
					threadMessage(Integer.toString(i) + "\n" + log.toString());
					
					Thread.sleep(1000);
					
					log.setLength(0);
					
					Log.e("TESTA", "Ran " + i + " iterations of top with " + log.capacity() + " capacity.");
					i++;
				} 
				catch (IOException e){
					Log.e("CPU INFO", "Getting top failed", e);
					//mFragmentText.setText("Cannot run top");
					threadMessage("Cannot run top");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
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

	private class CollectLogTask extends AsyncTask<ArrayList<String>, Void, StringBuilder>{
		@Override
		protected void onPreExecute(){
			if (mTopString.equals(""))
			{
				mFragmentText.setText("Running top command...");
			}
		}

		@Override
		protected StringBuilder doInBackground(ArrayList<String>... params){
			final StringBuilder log = new StringBuilder();
			try{
				ArrayList<String> commandLine = new ArrayList<String>();
				commandLine.add("top");
				commandLine.add("-n");
				commandLine.add("1");
				
				Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

				String line;
				while ((line = bufferedReader.readLine()) != null){ 
					line = line.replaceAll(" ", "\t");
					log.append(line);
					log.append("\n"); 
				}
			} 
			catch (IOException e){
				Log.e("CPU INFO", "Getting top failed", e);
				mFragmentText.setText("Cannot run top");
			} 

			return log;
		}

		@Override
		protected void onPostExecute(StringBuilder log){
			mTopString = log.toString();

			mTableString = logSplitter(mTopString);
			
			int arListSize = mTableString.size();
			String[] firstColumns = new String[arListSize];
			for(int i = 0; i < arListSize; i++)
			{
				firstColumns[i] = mTableString.get(0)[0];
			}

			mFragmentText.setText(mTopString);
		}

		private ArrayList<String[]> logSplitter(String log)
		{
			ArrayList<String[]> finalArray = new ArrayList<String[]>();
			String[] splitLog = log.split(" ");
			boolean headerFlag = false;
			String[] row = new String[NUM_COLUMNS];
			int j = 0;

			for(int i = 0; i < splitLog.length; i++)
			{
				if(splitLog[i].equals("PID"))
				{
					headerFlag = true;
				}

				if(!headerFlag)
				{
					mHeaderString = mHeaderString + splitLog[i];
				}
				else
				{
					Log.e("TAGGET", Integer.toString(j));
					if(j == 10)
					{
						finalArray.add(row);
						row = new String[NUM_COLUMNS];
						j = 0;
					}
					row[j] = splitLog[i];

					j++;
				}
			}

			return finalArray;
		}
	}
}
