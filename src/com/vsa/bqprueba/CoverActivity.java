package com.vsa.bqprueba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.vsa.bqprueba.utils.Constants;

public class CoverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_cover);
		//Toast.makeText(this, getIntent().getExtras().get(Constants.FILE_PATH_EXTRA).toString(), Toast.LENGTH_LONG).show();
		ImageView myCover=(ImageView)findViewById(R.id.imageViewCover);
		Bitmap myCoverBitmap=getImageBitmap (getIntent().getExtras().get(Constants.FILE_PATH_EXTRA).toString());
		if(myCoverBitmap==null){
			Toast.makeText(this,"Este libro no tiene portada.",Toast.LENGTH_LONG).show();
		}else{
			myCover.setImageBitmap(myCoverBitmap);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.cover, menu);
		return true;
	}
	
	private Bitmap getImageBitmap (String filePath){
		File f=new File(filePath);
		InputStream epubInputStream;
		Book book;
		Bitmap cover=null;
		try {
			epubInputStream = new FileInputStream(f);
			book = (new EpubReader()).readEpub(epubInputStream);
			if(book.getCoverImage()!=null)
				cover=BitmapFactory.decodeStream(book.getCoverImage().getInputStream());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return cover;
	}

}
