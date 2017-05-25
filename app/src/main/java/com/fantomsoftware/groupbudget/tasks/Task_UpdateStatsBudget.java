package com.fantomsoftware.groupbudget.tasks;

import android.os.AsyncTask;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.Operation;
import com.fantomsoftware.groupbudget.data.OperationBudget;
import com.fantomsoftware.groupbudget.interfaces.OnResultStats;
import com.fantomsoftware.groupbudget.consts.ConstsStats;
import com.fantomsoftware.groupbudget.consts.ConstsTask;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task return parameter
>
*/
public class Task_UpdateStatsBudget extends AsyncTask<Void, Integer, Integer>{
   //---------------------------------------------------------------------------
   private int id_budget = -1;
   private int id_stat   = -1;

   private OnResultStats on_stats_ready = null;

   private OperationBudget              operation_original = null;
   private OperationBudget              operation_calced   = null;
   private SparseArray<OperationBudget> data_operations    = null;
   private SparseArray<OperationBudget> data_result        = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetData( int id_budget, int id_stat ){
      this.id_budget = id_budget;
      this.id_stat = id_stat;
   }//SetData
   //---------------------------------------------------------------------------
   public void SetOnStatsReady( OnResultStats on_stats_ready ){
      this.on_stats_ready = on_stats_ready;
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected Integer doInBackground( Void... param ){

      data_result = null;

      if( id_budget == -1 || id_stat == -1 || on_stats_ready == null ){
         return ConstsTask.RESULT_PARAMETER_ERROR;
      }//if

      data_operations = Data.instance.db_budget_op.GetOperations( id_budget );

      if( data_operations == null ){
         return ConstsTask.RESULT_CANCELED;
      }//if

      switch( id_stat ){
         case ConstsStats.STATS_BALANCE:
         case ConstsStats.STATS_TOTAL_ADDED:
         case ConstsStats.STATS_TOTAL_SPENT:
            Process();
            break;
      }//switch

      return ConstsTask.RESULT_OK;
   }//doInBackground
   //---------------------------------------------------------------------------
   @Override
   protected void onPreExecute(){
      super.onPreExecute();
   }//onPreExecute
   //---------------------------------------------------------------------------
   protected void onProgressUpdate( Integer... progress ){
   }//onProgressUpdate
   //---------------------------------------------------------------------------
   @Override
   protected void onPostExecute( Integer result ){
      if(on_stats_ready != null){
         on_stats_ready.OnResult( data_result );
      }//if
   }//onPostExecute
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Process(){

      data_result = new SparseArray<>();

      for( int i = 0; i < data_operations.size(); i++ ){
         operation_original = data_operations.valueAt( i );

         //balances is calulated by currency
         operation_calced = data_result.get( operation_original.id_curr );

         if( operation_calced == null ){
            operation_calced = new OperationBudget();
            operation_calced.id_curr = operation_original.id_curr;
         }//if

         UpdateCalcedOperation();

         data_result.put( operation_calced.id_curr, operation_calced );
      }//for

   }//Process
   //---------------------------------------------------------------------------
   private void UpdateCalcedOperation(){

      switch( id_stat ){
         case ConstsStats.STATS_BALANCE:
            if( operation_original.type == Operation.TYPE_OUT ){
               operation_calced.amount -= operation_original.amount;
            }else{
               operation_calced.amount += operation_original.amount;
            }//if
            break;

         case ConstsStats.STATS_TOTAL_ADDED:
            if( operation_original.type == Operation.TYPE_IN ){
               operation_calced.amount += operation_original.amount;
            }//if
            break;

         case ConstsStats.STATS_TOTAL_SPENT:
            if( operation_original.type == Operation.TYPE_OUT ){
               operation_calced.amount -= operation_original.amount;
            }//if
            break;
      }//switch

   }//Process
   //---------------------------------------------------------------------------

}//Task_UpdateStatsBudget
