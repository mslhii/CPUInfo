package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.example.cpuinfo.R;

import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity 
{
	
    public static final String TAG = null;
    public Button button;
    public Button button3;
    public TextView tv;
    
    Button btnClosePopup; 
    Button popup;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// Display CPU Info in popup dialog
		tv = (TextView)findViewById(R.id.tv);
    	tv.setText("CPU Info: \n" + getInfo());	 
    }

    @Override
    protected void onResume() 
    {
        super.onResume();
    }

    @Override
    protected void onPause() 
    {
        super.onPause();
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
