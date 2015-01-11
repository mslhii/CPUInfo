package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.example.cpuinfo.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MiscFragment extends Fragment {
	
	public TextView mFragmentText;
	private CollectLogTask mCollectLogTask;
	protected ProgressDialog mProgressDialog;
	protected Context mContext;
	private boolean mPauseFlag = true;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_processes, container, false);
		mContext = rootView.getContext();
		
		Button pauseButton = (Button) rootView.findViewById(R.id.pause);
		pauseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
	            if (mPauseFlag)
	            {
	            	mPauseFlag = false;
	            	Toast.makeText(mContext, "Unpaused!", Toast.LENGTH_LONG);
	            	mCollectLogTask = (CollectLogTask) new CollectLogTask().execute(new ArrayList<String>());
	            }
	            else
	            {
	            	mPauseFlag = true;
	            	Toast.makeText(mContext, "Paused!", Toast.LENGTH_LONG);
	            }
			}
			
		});
		
		mFragmentText = (TextView) rootView.findViewById(R.id.tops);
		mCollectLogTask = (CollectLogTask) new CollectLogTask().execute(new ArrayList<String>());
		//while(mPauseFlag)
		//{
		//	mCollectLogTask = (CollectLogTask) new CollectLogTask().execute(new ArrayList<String>());
		//}
    	//mFragmentText.setText("Process List: \n" + getLog());	
		
		return rootView;
	}
	
	private class CollectLogTask extends AsyncTask<ArrayList<String>, Void, StringBuilder>{
        @Override
        protected void onPreExecute(){
        	mProgressDialog = new ProgressDialog(mContext);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setMessage("Loading");
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){
                public void onCancel(DialogInterface dialog){
                	if (mCollectLogTask != null && mCollectLogTask.getStatus() == AsyncTask.Status.RUNNING) 
                    {
                		Toast.makeText(mContext, "Stopped collecting top", Toast.LENGTH_LONG);
                        mCollectLogTask.cancel(true);
                        mCollectLogTask = null;
                    }
                }
            });
            mProgressDialog.show();
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
                    log.append(line);
                    log.append(" | "); 
                }
            } 
            catch (IOException e){
                Log.e("CPU INFO", "Getting top failed", e);
            } 
            
            return log;
        }

        @Override
        protected void onPostExecute(StringBuilder log){
        	mFragmentText.setText("Process List: \n" + log.toString());
        	
        	if (null != mProgressDialog && mProgressDialog.isShowing())
            {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
    }
}
