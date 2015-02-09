package com.kritikalerror.cpuinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.kritikalerror.cpuinfo.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CPUFragment extends Fragment {
	
	public static final String TAG = null;
    public TextView tv;
    
    protected Context mContext;
    private AdView mAdView;
    private LinearLayout mLayout;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_cpu, container, false);
		mContext = rootView.getContext();
		
		mLayout = (LinearLayout) rootView.findViewById(R.id.llc);
		loadAds((RelativeLayout) rootView.findViewById(R.id.cpu));
		
		// Display CPU Info in popup dialog
		tv = (TextView) rootView.findViewById(R.id.tv);
		tv.setMovementMethod(new ScrollingMovementMethod());
    	tv.setText("\n\n\n\n" + "CPU Info: \n" + getInfo());	
		
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
	
	private void loadAds(RelativeLayout layout)
	{
		// Create and setup the AdMob view
		mAdView = new AdView(mContext);

		mAdView.setAdSize(AdSize.SMART_BANNER);
		mAdView.setAdUnitId("ca-app-pub-6309606968767978/4023310042");
		AdRequest.Builder adRequestBuilder = new AdRequest.Builder();
				
		// Add the AdMob view
		RelativeLayout.LayoutParams adParams = 
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, 
						RelativeLayout.LayoutParams.WRAP_CONTENT);

		layout.addView(mAdView, adParams);

		mAdView.loadAd(adRequestBuilder.build());
	}
}
