package com.vsa.bqprueba.utils;

import java.io.File;
import java.util.ArrayList;

import com.vsa.bqprueba.CoverActivity;
import com.vsa.bqprueba.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class MyOnItemClickListener implements OnItemClickListener{
	Context mContext;
	String appFolder;
	ArrayList<String> epubFileNames;
	public MyOnItemClickListener(Context c,String appFolder,ArrayList<String> epubFileNames){
		mContext=c;
		this.appFolder=appFolder;
		this.epubFileNames=epubFileNames;
		
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(mContext,CoverActivity.class);
		intent.putExtra(Constants.FILE_PATH_EXTRA, appFolder+File.separator+epubFileNames.get(pos));
		mContext.startActivity(intent);
		
	}

}
