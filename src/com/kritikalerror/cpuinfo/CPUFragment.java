package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.example.cpuinfo.R;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class CPUFragment extends Fragment {
	
	public static final String TAG = null;
    public TextView tv;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_cpu, container, false);
		
		// Display CPU Info in popup dialog
		tv = (TextView) rootView.findViewById(R.id.tv);
		tv.setMovementMethod(new ScrollingMovementMethod());
    	tv.setText("CPU Info: \n" + getInfo());	
		
		return rootView;
	}
	
	private String getInfo() 
    {
        StringBuffer sb = new StringBuffer();
        sb.append("CPU ABI: ").append(Build.CPU_ABI).append("\n");
        if (new File("/proc/cpuinfo").exists()) 
        {
            try 
            {
                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/cpuinfo")));
                String line;
                while ((line = br.readLine()) != null) 
                {
                    sb.append(line + "\n");
                }
                if (br != null) 
                {
                    br.close();
                }
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            } 
        }
        return sb.toString();
    }
}
