package com.fantomsoftware.groupbudget.tasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import com.fantomsoftware.groupbudget.interfaces.OnResultBitmap;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task return parameter
>
*/
public class Task_DownloadImage extends AsyncTask<String, Bitmap, Bitmap>{
   //---------------------------------------------------------------------------
   private OnResultBitmap on_result;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetOnResult( OnResultBitmap on_result ){
      this.on_result = on_result;
   }//SetOnSelected
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected Bitmap doInBackground( String... param ){
      return DownloadBitmap( param[0] );
   }//doInBackground
   //---------------------------------------------------------------------------
   @Override
   protected void onPreExecute(){
      //do nothing
   }//onPreExecute
   //---------------------------------------------------------------------------
   @Override
   protected void onPostExecute( Bitmap bitmap ){
      if( on_result != null ){
         on_result.OnResult( bitmap );
      }//if
   }//onPostExecute
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private Bitmap DownloadBitmap( String link ){

      InputStream input_stream = null;

      try{
         URL url = new URL( link );

         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         connection.setReadTimeout( 10000 /* milliseconds */ );
         connection.setConnectTimeout( 15000 /* milliseconds */ );
         connection.setRequestMethod( "GET" );
         connection.setDoInput( true );
         connection.connect();

         //int response = connection.getResponseCode();

         input_stream = connection.getInputStream();

         return BitmapFactory.decodeStream( input_stream );

      }catch( IOException e ){

         Log.e( "ImageDownloader", "Something went wrong while" +
            " retrieving bitmap from " + e.getMessage() );

      }finally{

         if( input_stream != null ){
            try{
               input_stream.close();
            }catch( IOException e ){
               Log.e( "ImageDownloader", "Failed to close stream" + e.getMessage() );

            }//try
         }//if
      }//finally

      return null;
   }//DownloadUrl
   //---------------------------------------------------------------------------

}//Task_DownloadImage