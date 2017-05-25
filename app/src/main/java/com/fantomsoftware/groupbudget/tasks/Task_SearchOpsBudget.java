package com.fantomsoftware.groupbudget.tasks;


import android.os.AsyncTask;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.OperationBudget;
import com.fantomsoftware.groupbudget.interfaces.OnResultSearchOpsBudget;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task return parameter
>
*/
public class Task_SearchOpsBudget extends AsyncTask<Void, Integer, Void>{
   //---------------------------------------------------------------------------
   private String search_str;

   private SparseArray<OperationBudget> ops          = null;
   private SparseArray<OperationBudget> ops_filtered = null;

   private OnResultSearchOpsBudget on_result = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetData( String search_str, SparseArray<OperationBudget> ops ){
      this.search_str = search_str;
      this.ops = ops;
   }//SetData
   //---------------------------------------------------------------------------
   public void SetOnResult( OnResultSearchOpsBudget on_result ){
      this.on_result = on_result;
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected Void doInBackground( Void... param ){

      ops_filtered = new SparseArray<>();

      if( search_str.equals( "" ) ){
         ops_filtered = ops;
         return null;
      }//if search empty


      boolean         is_match;
      OperationBudget item;

      for( int i = 0; i < ops.size(); i++ ){

         if( isCancelled() ){
            ops_filtered = null;
            return null;
         }//if canceled

         item = ops.valueAt( i );
         is_match = false;

         if( item.name.toLowerCase().startsWith( search_str ) ){
            is_match = true;
         }else if( item.comment.toLowerCase().startsWith( search_str ) ){
            is_match = true;
         }//if

         if( !is_match ){
            continue;
         }//if

         ops_filtered.put( item.id, item );
      }//for

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
   protected void onPostExecute( Void result ){
      if( on_result != null && !isCancelled() ){
         on_result.OnResult( ops_filtered );
      }//if
   }//onPostExecute
   //---------------------------------------------------------------------------

}//Task_SearchOpsBudget