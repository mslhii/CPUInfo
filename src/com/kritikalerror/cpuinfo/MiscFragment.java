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
		
		final Button pauseButton = (Button) rootView.findViewById(R.id.pause);
		pauseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!mPauseFlag)
				{
					Toast.makeText(mContext, "Pausing...", Toast.LENGTH_SHORT).show();
					mPauseFlag = true;
					pauseButton.setText("Start");
				}
				else
				{
					Toast.makeText(mContext, "Starting...", Toast.LENGTH_SHORT).show();
					mPauseFlag = false;
					mCollectLogThread = new Thread(new CollectLogRunnable());
					mCollectLogThread.start();
					pauseButton.setText("Pause");
				}
			}

		});

		mFragmentText = (TextView) rootView.findViewById(R.id.tops);
		mFragmentText.setMovementMethod(new ScrollingMovementMethod());
		mFragmentText.setHorizontallyScrolling(true);
		mFragmentText.setTypeface(Typeface.MONOSPACE);
		if (mTopString.equals(""))
		{
			mCollectLogThread.start();
		}

		return rootView;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		mPauseFlag = true;
	}
	
	private class CollectLogRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (mTopString.equals(""))
			{
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
					
					Log.e("TOP", "Ran " + i + " iterations of top with " + log.capacity() + " capacity.");
					i++;
				} 
				catch (IOException e){
					Log.e("CPU INFO", "Getting top failed", e);
					threadMessage("Cannot run top");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Log.e("TOP", "Thread finished!");
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

/*
 * "Usage: %s [ -m max_procs ] [ -n iterations ] [ -d delay ] [ -s sort_column ] [ -t ] [ -h ]\n"
567                    "    -m num  Maximum number of processes to display.\n"
568                    "    -n num  Updates to show before exiting.\n"
569                    "    -d num  Seconds to wait between updates.\n"
570                    "    -s col  Column to sort by (cpu,vss,rss,thr).\n"
571                    "    -t      Show threads instead of processes.\n"
572                    "    -h      Display this help screen.\n",
*/
