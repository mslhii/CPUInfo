package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.example.cpuinfo.R;
import com.google.android.gms.ads.AdView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MiscFragment extends Fragment {

	public TextView mFragmentText;
	protected Context mContext;
	private String mTopString;
	private Thread mCollectLogThread;
	private boolean mPauseFlag = false;
	
	private String mMaxProcesses;
	private String mRefreshFreq;
	private boolean mShowThreads;
	private String mSortColumns;
	
	private AdView mAdView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_processes, container, false);
		mContext = rootView.getContext();
		mTopString = "";
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
					prepareParams();
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
			prepareParams();
			mCollectLogThread.start();
		}
		
		loadAds((RelativeLayout) rootView.findViewById(R.id.topz));

		return rootView;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		mPauseFlag = true;
	}
	
	private class CollectLogRunnable implements Runnable {

		@SuppressLint("DefaultLocale")
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
					
					if(!mMaxProcesses.equals("Disabled") && mMaxProcesses != null)
					{
						commandLine.add("-m");
						commandLine.add(mMaxProcesses);
						Log.e("TOP", "Processing!");
					}
					if(mShowThreads)
					{
						commandLine.add("-t");
						Log.e("TOP", "Threading!");
					}
					if(!mSortColumns.equals("Default") && mSortColumns != null)
					{
						commandLine.add("-s");
						commandLine.add(mSortColumns.toLowerCase());
						Log.e("TOP", "Sorting!");
					}
					
					Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
					BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
	
					String line;
					while ((line = bufferedReader.readLine()) != null){ 
						line = line.replaceAll(" ", "\t");
						log.append(line);
						log.append("\n"); 
					}
					
					threadMessage(Integer.toString(i) + "\n" + log.toString());
					
					if(!mRefreshFreq.equals("1") && mRefreshFreq != null)
					{
						int refreshTime = Integer.valueOf(mRefreshFreq);
						Thread.sleep(refreshTime * 1000);
						Log.e("TOP", "Counting!");
					}
					else
					{
						Thread.sleep(1000);
					}
					
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
	
	private void prepareParams()
	{
		// Get settings params
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
		mMaxProcesses = sharedPrefs.getString("maxProcesses", "Disabled");
		mRefreshFreq = sharedPrefs.getString("refreshFrequency", "1");
		mShowThreads = sharedPrefs.getBoolean("threads", false);
		mSortColumns = sharedPrefs.getString("columns", "None");
		
		String testString = "true";
    	if(!mShowThreads)
    	{
    		testString = "false";
    	}
		
    	Log.e("MAIN", "Settings is: " + mMaxProcesses + " " +
    			mRefreshFreq + " " + testString + " " + mSortColumns);
	}
	
	private void loadAds(RelativeLayout layout)
	{
		// Create and setup the AdMob view
		mAdView = new AdView(mContext);

		mAdView.setAdSize(AdSize.SMART_BANNER);
		mAdView.setAdUnitId("ca-app-pub-6309606968767978/4023310042");
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
		
		// Get the height for offset calculations
		AdSize adSize = mAdView.getAdSize();
		
		// Add the AdMob view
		RelativeLayout.LayoutParams adParams = 
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
						RelativeLayout.LayoutParams.WRAP_CONTENT);

		layout.addView(mAdView, adParams);

		mAdView.loadAd(adRequestBuilder.build());
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
