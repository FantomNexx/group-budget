package com.fantomsoftware.groupbudget.tasks;

import android.os.AsyncTask;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.Operation;
import com.fantomsoftware.groupbudget.data.OperationBudget;
import com.fantomsoftware.groupbudget.data.OperationUser;
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
public class Task_UpdateStatsUser extends AsyncTask<Void, Integer, Integer>{
   //---------------------------------------------------------------------------
   private int id_budget = -1;
   private int id_stat   = -1;
   private int id_user   = -1;

   private OnResultStats on_stats_ready = null;

   private OperationUser                operation_original = null;
   private OperationBudget              operation_calced   = null;
   private SparseArray<OperationUser>   data_operations    = null;
   private SparseArray<OperationBudget> data_result        = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetData( int id_budget, int id_stat, int id_user ){
      this.id_budget = id_budget;
      this.id_stat = id_stat;
      this.id_user = id_user;
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

      data_operations = Data.instance.db_user_op.Get( id_budget, id_user );

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
      on_stats_ready.OnResult( data_result );
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
               operation_calced.amount -=
                  ( operation_original.amount_pers + operation_original.amount_shared );
            }else{
               operation_calced.amount +=
                  ( operation_original.amount_pers + operation_original.amount_shared );
            }//if
            break;

         case ConstsStats.STATS_TOTAL_ADDED:
            if( operation_original.type == Operation.TYPE_IN ){
               operation_calced.amount +=
                  ( operation_original.amount_pers + operation_original.amount_shared );
            }//if
            break;

         case ConstsStats.STATS_TOTAL_SPENT:
            if( operation_original.type == Operation.TYPE_OUT ){
               operation_calced.amount -=
                  ( operation_original.amount_pers + operation_original.amount_shared );
            }//if
            break;
      }//switch

   }//Process
   //---------------------------------------------------------------------------

}//Task_UpdateStatsBudget
