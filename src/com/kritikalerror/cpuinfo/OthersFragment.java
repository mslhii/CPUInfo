package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import com.kritikalerror.cpuinfo.R;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class OthersFragment extends Fragment {
	
	public TextView mFragmentText;
	protected Context mContext;
	private String mTopString;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_misc, container, false);
		/*
		mContext = rootView.getContext();
		mTopString = "";
		
		Button refreshButton = (Button) rootView.findViewById(R.id.refresh);
		refreshButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "Refreshing...", Toast.LENGTH_SHORT).show();
	            new CollectLogTask().execute(new ArrayList<String>());
			}
			
		});
		
		mFragmentText = (TextView) rootView.findViewById(R.id.miscs);
		mFragmentText.setMovementMethod(new ScrollingMovementMethod());
		if (mTopString.equals(""))
		{
			new CollectLogTask().execute(new ArrayList<String>());
		}
		*/
		
		mFragmentText = (TextView) rootView.findViewById(R.id.miscs);
		mFragmentText.setMovementMethod(new ScrollingMovementMethod());
		mFragmentText.setText("General CPU Usage: " + Float.toString(readUsage()));
		return rootView;
	}
	
	private float readUsage() {
	    try {
	        RandomAccessFile reader = new RandomAccessFile("/proc/stat", "r");
	        String load = reader.readLine();

	        String[] toks = load.split(" +");  // Split on one or more spaces

	        long idle1 = Long.parseLong(toks[4]);
	        long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
	              + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        try {
	            Thread.sleep(360);
	        } catch (Exception e) {}

	        reader.seek(0);
	        load = reader.readLine();
	        reader.close();

	        toks = load.split(" +");

	        long idle2 = Long.parseLong(toks[4]);
	        long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3]) + Long.parseLong(toks[5])
	            + Long.parseLong(toks[6]) + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

	        return (float)(cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1));

	    } catch (IOException ex) {
	        ex.printStackTrace();
	    }

	    return 0;
	} 
	/*
	public class CollectLogTask extends AsyncTask<ArrayList<String>, Void, StringBuilder>{
        @Override
        protected void onPreExecute(){
        	if (mTopString.equals(""))
    		{
        		mFragmentText.setText("Running procrank command...");
    		}
        }
        
        @Override
        protected StringBuilder doInBackground(ArrayList<String>... params){
        	final StringBuilder log = new StringBuilder();
            try{
                ArrayList<String> commandLine = new ArrayList<String>();
                commandLine.add("lshw");
                
                Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[0]));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                
                String line;
                while ((line = bufferedReader.readLine()) != null){ 
                    log.append(line);
                    log.append("\n"); 
                }
            } 
            catch (IOException e){
                Log.e("CPU INFO", "Getting procrank failed", e);
                mFragmentText.setText("Cannot run procrank");
                log.append("");
            } 
            
            return log;
        }

        @Override
        protected void onPostExecute(StringBuilder log){
        	mTopString = log.toString();
        	mFragmentText.setText(mTopString);
        }
    }
	*/
}
