package com.fantomsoftware.groupbudget.tasks;


import android.os.AsyncTask;

import com.fantomsoftware.groupbudget.data.Data;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task return parameter
>
*/
public class Task_UpdateCurrencyUsage extends AsyncTask<String, Integer, Integer>{
   //---------------------------------------------------------------------------
   private int     id;
   private boolean is_used;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetData( int id, boolean is_used ){
      this.id = id;
      this.is_used = is_used;
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected Integer doInBackground( String... param ){
      Data.instance.db_currency.UpdateUsage( id, is_used );
      return null;
   }//doInBackground
   //---------------------------------------------------------------------------
   protected void onProgressUpdate( Integer... progress ){
   }//onProgressUpdate
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected void onPreExecute(){
      super.onPreExecute();
   }//onPreExecute
   //---------------------------------------------------------------------------
   @Override
   protected void onPostExecute( Integer result ){
   }//onPostExecute
   //---------------------------------------------------------------------------

}//Task_UpdateCurrencyUsage