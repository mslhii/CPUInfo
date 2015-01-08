package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.example.cpuinfo.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MiscFragment extends Fragment {
	
	public TextView tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_processes, container, false);
		
		tv = (TextView) rootView.findViewById(R.id.tops);
    	//tv.setText("Process List: \n" + getLog());	
		
		return rootView;
	}
	
	/*
	private StringBuilder getLog() {
		final StringBuilder log = new StringBuilder();
        try{
            ArrayList<String> commandLine = new ArrayList<String>();
            commandLine.add("top");
            //commandLine.add("-d");
            
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
	*/
}
