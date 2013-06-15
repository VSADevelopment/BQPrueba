package com.vsa.bqprueba;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.epub.EpubReader;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.vsa.bqprueba.utils.Constants;
import com.vsa.bqprueba.utils.MyOnItemClickListener;

public class MainActivity extends Activity{
	SyncBooksTask getEpubNames;
	ArrayList<String> epubFileNames;
	DropboxAPI<AndroidAuthSession> mDBApi;
	String appFolder="";
	ProgressDialog pdialog=null;
	private boolean isAuthentified=false;
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Creamos el directorio de la aplicación
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
        	Log.d("Storage: ", "No storage detected.");
        } else {
        	appFolder=Environment.getExternalStorageDirectory()+File.separator+Constants.MY_APPLICATION_FOLDER;
        	File fileFolder = new File(appFolder);
        	Log.d("Storage: ", fileFolder.getPath());
        	if(fileFolder.mkdirs()){
        		Log.d("Storage: ", "Creado");
        	}else{
        		Log.d("Storage: ", "El directorio no se ha creado, o ya existe.");
        	}
        	
        }
        // And later in some initialization function:
        AppKeyPair appKeys = new AppKeyPair(Constants.getAppKey(), Constants.getAppSecret());
        AndroidAuthSession session = new AndroidAuthSession(appKeys, Constants.getAccessType());
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        mDBApi.getSession().startAuthentication(MainActivity.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    	MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
        return true;
    }
    protected void onResume() {
        super.onResume();
        if (mDBApi.getSession().authenticationSuccessful() && !isAuthentified) {
        	isAuthentified=true;
            Log.d("Prueba","Autentificado");

            try {
                // MANDATORY call to complete auth.
                // Sets the access token on the session
                mDBApi.getSession().finishAuthentication();
                AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();
                // Uso el objeto SyncBooksTask para descargar los ficheros epub y obtener los títulos
                // de forma asíncrona.
                getEpubNames = new SyncBooksTask(this);
                getEpubNames.execute();
				// Provide your own storeKeys to persist the access token pair
                // A typical way to store tokens is using SharedPreferences
                //storeKeys(tokens.key, tokens.secret);
            } catch (IllegalStateException e) {
               // Log.i("DbAuthLog", "Error authenticating", e);
            }
        }

        // ...
    }
    
    
    //DESCARGARMOS LOS LIBROS DE FORMA CONCURRENTE
    private class SyncBooksTask extends AsyncTask<Void,Void,ArrayList<String>>{
    	//private File appFolder=null;
    	Context mContext;
    	SyncBooksTask(Context c){
    		super();
    		mContext=c;
    	}
    	protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			//super.onPostExecute(result);
    		if(result.size()==0){
				AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
				builder.setMessage("Debes subir libros en formato epub al directorio Dropbox de la aplicación (/Aplicaciones/BQPrueba)")
				.setCancelable(false)
				.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						System.exit(0);
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				//Toast.makeText(MainActivity.this, "Debes tener libros en formato epub en el directorio de la aplicación en Dropbox.", Toast.LENGTH_LONG).show();
			}
			ArrayList<String> bookTitles=getBookTitles(result);
			ArrayList <HashMap<String,String>> Items=getItems(bookTitles,result);
			
			String[] from=new String[] {"title","filename"};
    		int[] to=new int[]{R.id.textViewTitle,R.id.textViewFileName};
			SimpleAdapter mySimpleAdapter=new SimpleAdapter(mContext, Items, R.layout.row, from, to);
    		ListView myListView=(ListView)findViewById(R.id.listViewBooks);
    		//ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(mContext,android.R.layout.simple_list_item_1,getBookTitles(result));
			myListView.setOnItemClickListener(new MyOnItemClickListener(mContext, appFolder, result));
			myListView.setAdapter(mySimpleAdapter);
			
			pdialog.dismiss();
			
			Log.d("Prueba",Integer.toString(result.size()));
		}
    	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			//super.onPreExecute();
			pdialog=new ProgressDialog(MainActivity.this);
			pdialog.setCancelable(false);
			pdialog.setMessage("Sincronizando archivos ...");
			pdialog.show();
		}
		@Override
		protected ArrayList<String> doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ArrayList<String> res=new ArrayList<String>();
			String fileName="";
			Entry entries = null;
			try {
				entries = mDBApi.metadata(File.separator, 100, null, true, null);
			} catch (DropboxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

            for (Entry e : entries.contents) {
                if (!e.isDeleted) {
                	fileName=e.fileName();
                    if(fileName.endsWith(".epub")){
                    	Log.i("Item Name",fileName);
                    	res.add(fileName);
                    }
                }
            }
            //Una vez obtenidos los nombres le los archivos EPUB, los descargamos
            downloadBooks(res);
			return res;
		}
		
		
		private ArrayList<HashMap<String, String>> getItems(
				ArrayList<String> bookTitles, ArrayList<String> fileNames) {
			// TODO Auto-generated method stub
				HashMap<String,String> elem;
				ArrayList<HashMap<String,String>> items=new ArrayList<HashMap<String,String>>();
				elem=new HashMap<String,String>();
				for(int x=0;x<bookTitles.size();x++){
					elem=new HashMap<String,String>();
					elem.put("title", bookTitles.get(x));
					elem.put("filename", fileNames.get(x));
					items.add(elem);
				}
			return items;
		}
		private void downloadBooks(ArrayList<String> fileNames){
			FileOutputStream outputStream = null;
			Log.d("Prueba", "DESCARGANDO");
			//Toast.makeText(MainActivity.this, "Descargando libros", Toast.LENGTH_LONG).show();
			for (int fileNum=0;fileNum<fileNames.size();fileNum++){
				File f=new File(appFolder+File.separator+fileNames.get(fileNum));
				if(!f.exists()){
					try {
						
					    outputStream = new FileOutputStream(f);
					    Log.d("downloadBooks: ",appFolder+File.separator+fileNames.get(fileNum));
					    DropboxFileInfo info = mDBApi.getFile(File.separator+fileNames.get(fileNum), null, outputStream, null);
					    Log.i("downloadBooks", "The file's rev is: " + info.getMetadata().rev);
					    // /path/to/new/file.txt now has stuff in it.
					} catch (DropboxException e) {
					    Log.e("downloadBooks", "Something went wrong while downloading.");
					} catch (FileNotFoundException e) {
					    Log.e("downloadBooks", "File not found."+File.separator+fileNames.get(fileNum));
					} finally {
					    if (outputStream != null) {
					        try {
					            outputStream.close();
					        } catch (IOException e) {}
					    }
					}
				}else{Log.d("downloadBooks","El fichero ya existe");}
			}
		
		}
		
		
    }
    private ArrayList<String> getBookTitles (ArrayList<String> fileNames){
    	ArrayList<String> bookTitles=new ArrayList<String>();
    	Book book=null;
    	for(int x=0;x<fileNames.size();x++){
    		
				File f=new File(appFolder+File.separator+fileNames.get(x));
				//if(f.exists()) Log.d(" Epub:","El fichero existe");
				try {
					InputStream epubInputStream=new FileInputStream(f);
					book = (new EpubReader()).readEpub(epubInputStream);
					bookTitles.add(book.getTitle());
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	}
    	return bookTitles;
    }
    
}
