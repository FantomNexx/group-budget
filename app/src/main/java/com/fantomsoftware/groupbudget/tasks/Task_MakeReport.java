package com.fantomsoftware.groupbudget.tasks;

import android.os.AsyncTask;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.BudgetUser;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.Operation;
import com.fantomsoftware.groupbudget.data.OperationBudget;
import com.fantomsoftware.groupbudget.data.OperationUser;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.interfaces.OnResultNumber;
import com.fantomsoftware.groupbudget.interfaces.OnResultStringTwo;
import com.fantomsoftware.groupbudget.consts.ConstsTask;
import com.fantomsoftware.groupbudget.utils.Storage;
import com.fantomsoftware.groupbudget.utils.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task return parameter
>
*/
public class Task_MakeReport extends AsyncTask<Void, Integer, Integer>{
//--------------------------------------------------------------------

private OnEvent           on_pre_execute = null;
private OnResultStringTwo on_result      = null;
private OnResultNumber    on_progress    = null;

private int    id_budget         = -1;
private String report_name       = "";
private String report_file_name  = "";
private int    progress_task     = 0;
private int    progress_task_old = 0;
private int    rows_count        = 0;
private int    rows_total        = 0;
private int    rows_count_global = 0;
private int    rows_total_global = 0;
private String csvHeader         = "";
private String csvValues         = "";

private SparseArray<OperationBudget>            data_budget_ops     = null;
private SparseArray<OperationBudget>            data_balance        = null;
private SparseArray<Integer>                    curr_ids            = null;
private SparseArray<BudgetUser>                 data_users          = null;
private SparseArray<SparseArray<OperationUser>> data_users_ops      = null;
private SparseArray<SparseArray<OperationUser>> data_users_balances = null;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetData( int id_budget, String report_name ){
  this.id_budget = id_budget;
  this.report_name = report_name;
}//SetData
//--------------------------------------------------------------------
public void SetOnPreExecute( OnEvent on_pre_execute ){
  this.on_pre_execute = on_pre_execute;
}//SetOnSelected
//--------------------------------------------------------------------
public void SetOnResult( OnResultStringTwo on_result ){
  this.on_result = on_result;
}//SetOnSelected
//--------------------------------------------------------------------
public void SetOnProgress( OnResultNumber on_progress ){
  this.on_progress = on_progress;
}//SetOnSelected
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected Integer doInBackground( Void... param ){

  if( !ProcessData() ){
    return ConstsTask.RESULT_PARAMETER_ERROR;
  }//if


  for( int i = 0; i < curr_ids.size(); i++ ){
    ProcessReportCurrency( curr_ids.valueAt( i ) );
    csvValues += "\n";
    csvValues += "\n";
  }//for

  if( !WriteToFile() ){
    return ConstsTask.RESULT_ERROR;
  }//if

  return ConstsTask.RESULT_OK;
}//doInBackground
//--------------------------------------------------------------------
@Override
protected void onPreExecute(){
  super.onPreExecute();


  if( on_pre_execute != null ){
    on_pre_execute.OnEvent();
  }//if

}//onPreExecute
//--------------------------------------------------------------------
@Override
protected void onProgressUpdate( Integer... progress ){
  if( on_progress != null ){
    if( progress_task != progress_task_old ){
      on_progress.OnResult( progress[0] );
      progress_task_old = progress_task;
    }//if
  }//if
}//onProgressUpdate
//--------------------------------------------------------------------
@Override
protected void onPostExecute( Integer result ){
  if( on_result != null && !isCancelled() ){
    on_result.OnResult( report_name, report_file_name );
  }//if
}//onPostExecute
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private boolean ProcessData(){
  data_budget_ops =
      Data.instance.db_budget_op.GetOperations( id_budget );

  data_users =
      Data.instance.db_budget_users.GetByBudget( id_budget );

  curr_ids = GetCurrencies( data_budget_ops );

  //initializing arrays for users (all operations and balances arrays)
  data_users_ops = new SparseArray<>();
  data_users_balances = new SparseArray<>();

  BudgetUser user;
  for( int i = 0; i < data_users.size(); i++ ){
    user = data_users.valueAt( i );

    SparseArray<OperationUser> data_user_ops =
        Data.instance.db_user_op.Get_IdexOperation( id_budget, user.id_user );

    SparseArray<OperationUser> data_users_balance =
        new SparseArray<>();

    data_users_ops.put( user.id_user, data_user_ops );
    data_users_balances.put( user.id_user, data_users_balance );
  }//for

  progress_task = 0;

  rows_count = 0;
  rows_total = data_budget_ops.size();

  rows_count_global = 0;
  rows_total_global = curr_ids.size() * rows_total;

  csvHeader = "";
  csvValues = "";

  List<String> list_headers = new ArrayList<>();
  list_headers.add( "Date" );
  list_headers.add( "Name" );
  list_headers.add( "Comment" );
  list_headers.add( "Amount" );
  list_headers.add( "Balance" );
  list_headers.add( "Currency" );

  String user_name;
  for( int i = 0; i < data_users.size(); i++ ){
    user = data_users.valueAt( i );
    user_name = Data.instance.db_user.GetName( user.id_user );

    list_headers.add( "Amount " + user_name );
    list_headers.add( "Balance " + user_name );
  }//for


  for( String header : list_headers ){
    csvHeader += header + ";";
  }//for
  csvHeader = csvHeader.substring( 0, csvHeader.length() - 1 );
  csvHeader += "\n";

  String app_root_folder = Data.instance.storage.GetPath_AppRootFolder();
  report_file_name = app_root_folder + "/" + report_name;

  return true;
}//ProcessData
//--------------------------------------------------------------------
public void ProcessReportCurrency( int id_curr ){

  rows_count = rows_total - 1;
  data_balance = new SparseArray<>();

  while( rows_count >= 0 ){

    progress_task = (int) (Math.ceil( (rows_count_global / (float) rows_total_global) * 100.0f ));

    //onProgressUpdate( progress_task );
    publishProgress( progress_task );

    OperationBudget op = data_budget_ops.valueAt( rows_count );

    if( op.id_curr != id_curr ){
      rows_count_global++;
      rows_count--;
      continue;
    }//if

    data_balance.put( rows_count, op );

    ProcessRow( op, id_curr );

    rows_count--;
    rows_count_global++;
  }//while

}//ProcessReportCurrency
//--------------------------------------------------------------------
private void ProcessRow( OperationBudget op, int id_curr ){

  csvValues += Utils.FormatLongDateToStringFull( op.date_created ) + ";";
  csvValues += op.name + ";";
  csvValues += op.comment + ";";

  if( op.type == Operation.TYPE_IN ){
    csvValues += "+" + Utils.FormatSumToString( op.amount ) + ";";
  }else{
    csvValues += "-" + Utils.FormatSumToString( op.amount ) + ";";
  }//if

  float balance = GetBalance( data_balance, id_curr );
  csvValues += Utils.FormatSumToString( balance ) + ";";

  csvValues += Data.instance.db_currency.GetCodeById( op.id_curr ) + ";";

  BudgetUser    user;
  OperationUser op_user;
  for( int i = 0; i < data_users.size(); i++ ){
    user = data_users.valueAt( i );

    SparseArray<OperationUser> data_user_ops =
        data_users_ops.get( user.id_user );
    SparseArray<OperationUser> data_users_balance =
        data_users_balances.get( user.id_user );

    op_user = data_user_ops.get( op.id );

    if( op_user == null ){
      balance = GetBalanceUser( data_users_balance, id_curr );
      csvValues += Utils.FormatSumToString( 0 ) + ";";
      csvValues += Utils.FormatSumToString( balance ) + ";";
    }else{
      data_users_balance.put( data_users_balance.size(), op_user );

      balance = GetBalanceUser( data_users_balance, id_curr );

      if( op_user.type == Operation.TYPE_IN ){
        csvValues += "+" + Utils.FormatSumToString( op_user.amount_pers + op_user.amount_shared ) + ";";
      }else{
        csvValues += "-" + Utils.FormatSumToString( op_user.amount_pers + op_user.amount_shared ) + ";";
      }//if

      csvValues += Utils.FormatSumToString( balance ) + ";";
    }//else
  }//for

  csvValues += "\n";

}//ProcessRow
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private float GetBalance( SparseArray<OperationBudget> data, int id_curr ){

  OperationBudget operation;
  float           balance = 0;

  for( int i = 0; i < data.size(); i++ ){
    operation = data.valueAt( i );

    if( operation.id_curr != id_curr ){
      continue;
    }//if

    if( operation.type == Operation.TYPE_IN ){
      balance += operation.amount;
    }else{
      balance -= operation.amount;
    }//if
  }//for

  return balance;
}//GetBalance
//--------------------------------------------------------------------
private float GetBalanceUser( SparseArray<OperationUser> data, int id_curr ){

  OperationUser operation;
  float         balance = 0;

  for( int i = 0; i < data.size(); i++ ){
    operation = data.valueAt( i );

    if( operation.id_curr != id_curr ){
      continue;
    }//if

    if( operation.type == Operation.TYPE_IN ){
      balance += (operation.amount_pers + operation.amount_shared);
    }else{
      balance -= (operation.amount_pers + operation.amount_shared);
    }//if
  }//for

  return balance;
}//GetBalanceUser
//--------------------------------------------------------------------
private SparseArray<Integer> GetCurrencies( SparseArray<OperationBudget> data ){

  SparseArray<Integer> currencies = new SparseArray<>();

  Integer         id_curr;
  OperationBudget operation;

  for( int i = 0; i < data.size(); i++ ){
    operation = data.valueAt( i );

    id_curr = currencies.get( operation.id_curr );

    if( id_curr == null ){
      currencies.put( operation.id_curr, operation.id_curr );
    }//if
  }//for

  return currencies;
}//GetBalanceData
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private boolean WriteToFile(){

  try{
    Storage.DeleteFile( report_file_name );

    File             file = new File( report_file_name );
    FileOutputStream fos  = new FileOutputStream( file );
    //OutputStreamWriter osw = new OutputStreamWriter( fos, Charset.forName( "UTF-16LE" ).newEncoder() );
    OutputStreamWriter osw = new OutputStreamWriter( fos, Charset.forName( "UTF-8" ).newEncoder() );
    //OutputStreamWriter osw = new OutputStreamWriter( fos, "UTF-8" );
    Writer writer = new BufferedWriter( osw );

    writer.write( csvHeader );
    writer.write( csvValues );
    writer.close();

  }catch( IOException e ){
    Utils.ToastS( "Export transactions to a file failed" );
    return false;
  }//try

  return true;
}//WriteToFile
//--------------------------------------------------------------------

}//Task_UpdateStatsBudget
