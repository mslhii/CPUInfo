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
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

	final private int NUM_COLUMNS = 10;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_processes, container, false);
		mContext = rootView.getContext();
		mHeaderString = "";
		mTopString = "";
		mTableString = new ArrayList<String[]>();

		Button pauseButton = (Button) rootView.findViewById(R.id.pause);
		pauseButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(mContext, "Refreshing...", Toast.LENGTH_SHORT).show();
				new CollectLogTask().execute(new ArrayList<String>());
			}

		});

		mFragmentText = (TextView) rootView.findViewById(R.id.tops);
		mFragmentText.setMovementMethod(new ScrollingMovementMethod());
		if (mTopString.equals(""))
		{
			new CollectLogTask().execute(new ArrayList<String>());
			/*
			ScrollView sv = new ScrollView(mContext);
			int arListSize = mTableString.size();
			String[] firstColumns = new String[arListSize];
			for(int i = 0; i < arListSize; i++)
			{
				firstColumns[i] = mTableString.get(0)[0];
			}
			TableLayout tableLayout = createTableLayout(mTableString.get(0), firstColumns, NUM_COLUMNS, mTableString.size());
			*/
		}

		return rootView;
	}

	private TableLayout createTableLayout(String [] rv, String [] cv,int rowCount, int columnCount) {
		TableLayout.LayoutParams tableLayoutParams = new TableLayout.LayoutParams();
		TableLayout tableLayout = new TableLayout(mContext);
		tableLayout.setBackgroundColor(Color.BLACK);

		TableRow.LayoutParams tableRowParams = new TableRow.LayoutParams();
		tableRowParams.setMargins(1, 1, 1, 1);
		tableRowParams.weight = 1;

		for (int i = 0; i < rowCount; i++) {
			TableRow tableRow = new TableRow(mContext);
			tableRow.setBackgroundColor(Color.BLACK);

			for (int j= 0; j < columnCount; j++) {
				TextView textView = new TextView(mContext);
				textView.setBackgroundColor(Color.WHITE);
				textView.setGravity(Gravity.CENTER);

				String s1 = Integer.toString(i);
				String s2 = Integer.toString(j);
				String s3 = s1 + s2;
				int id = Integer.parseInt(s3);
				Log.d("TAG", "-___>"+id);
				if (i ==0 && j==0){
					textView.setText("0==0");
				} else if(i==0){
					Log.d("TAAG", "set Column Headers");
					textView.setText(cv[j-1]);
				}else if( j==0){
					Log.d("TAAG", "Set Row Headers");
					textView.setText(rv[i-1]);
				}else {
					textView.setText(""+id);
					if(id==23){
						textView.setText("ID=23");

					}
				}

				tableRow.addView(textView, tableRowParams);
			}

			tableLayout.addView(tableRow, tableLayoutParams);
		}

		return tableLayout;
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

			logSplitter(mTopString);

			mFragmentText.setText(mTopString);
		}

		private ArrayList<String[]> logSplitter(String log)
		{
			ArrayList<String[]> finalArray = new ArrayList<String[]>();
			String[] splitLog = log.split(" ");
			boolean headerFlag = false;
			String[] row = new String[NUM_COLUMNS];

			for(int i = 0; i < splitLog.length; i++)
			{
				Log.e("TEST", splitLog[i]);
				int j = 0;

				if(!splitLog[i].equals("PID"))
				{
					headerFlag = true;
				}

				if(!headerFlag)
				{
					mHeaderString = mHeaderString + splitLog[i];
				}
				else
				{
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
