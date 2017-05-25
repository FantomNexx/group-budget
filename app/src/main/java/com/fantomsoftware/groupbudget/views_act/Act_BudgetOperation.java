package com.fantomsoftware.groupbudget.views_act;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.cmp.CmpInputText;
import com.fantomsoftware.groupbudget.cmp.CmpInputTextBtn;
import com.fantomsoftware.groupbudget.cmp.CmpUserListSums;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.data.Budget;
import com.fantomsoftware.groupbudget.data.BudgetUser;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.Operation;
import com.fantomsoftware.groupbudget.data.OperationBudget;
import com.fantomsoftware.groupbudget.data.OperationUser;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.dialogs.DialogCalcSum;
import com.fantomsoftware.groupbudget.dialogs.DialogCalcTips;
import com.fantomsoftware.groupbudget.dialogs.DialogPickDate;
import com.fantomsoftware.groupbudget.dialogs.DialogPickOptionMult;
import com.fantomsoftware.groupbudget.dialogs.DialogPickOptionSingl;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.interfaces.OnResultDate;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;
import com.fantomsoftware.groupbudget.interfaces.OnResultNumber;
import com.fantomsoftware.groupbudget.interfaces.OnResultOptions;
import com.fantomsoftware.groupbudget.interfaces.OnResultPosition;
import com.fantomsoftware.groupbudget.utils.Utils;

import java.util.GregorianCalendar;


public class Act_BudgetOperation extends AppCompatActivity{
//--------------------------------------------------------------------
private OperationBudget item = null;
private int             mode = ConstsAct.MODE_NEW;

private CmpInputText    cmp_type       = null;
private CmpInputText    cmp_name       = null;
private CmpInputTextBtn cmp_sum        = null;
private CmpUserListSums cmp_users_sums = null;
private CmpInputText    cmp_comment    = null;
private CmpInputText    cmp_date       = null;

private TextView tv_btn_sum_op_share_sum_evenly     = null;
private TextView tv_btn_sum_op_get_sum_from_total   = null;
private TextView tv_btn_sum_op_set_sum_diff_as_tips = null;

private View spacer = null;

private DialogPickOptionSingl dialog_currency   = null;
private DialogPickOptionSingl dialog_type       = null;
private DialogCalcSum         dialog_calc_sum   = null;
private DialogCalcTips        dialog_tip        = null;
private DialogCalcSum         dialog_calc_user  = null;
private DialogCalcTips        dialog_tip_user   = null;
private DialogPickOptionMult  dialog_pick_users = null;
private DialogPickDate        dialog_date       = null;


private SparseArray<OperationUser> users_operations = null;
private Options                    option_users     = null;

private final int SUM_SHARE_EVENLY   = 0;
private final int SUM_GET_FROM_USERS = 1;
private final int SUM_DIFF_AS_TIPS   = 2;

private boolean is_set_personal_sum = false;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
protected void onCreate( Bundle savedInstanceState ){

  super.onCreate( savedInstanceState );
  setContentView( R.layout.act_budget_operation );

  if( !IsParamsOk() ){
    return;
  }//if

  Init();

  UpdateData();

}//onCreate
//--------------------------------------------------------------------
@Override
public boolean onCreateOptionsMenu( Menu menu ){
  MenuInflater menuInflater = getMenuInflater();
  menuInflater.inflate( R.menu.menu_act_budget_operation, menu );
  return true;
}//onCreateOptionsMenu
//--------------------------------------------------------------------
@Override
public boolean onPrepareOptionsMenu( Menu menu ){
  super.onPrepareOptionsMenu( menu );
  return true;
}//onPrepareOptionsMenu
//--------------------------------------------------------------------
@Override
public boolean onOptionsItemSelected( MenuItem item ){

  switch( item.getItemId() ){
    case android.R.id.home:
      CloseActicity();
      break;

    case R.id.action_save:
      SaveAndExit();
      break;

    default:
      return super.onOptionsItemSelected( item );
  }//switch

  return true;
}//onOptionsItemSelected
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private boolean IsParamsOk(){

  Bundle extras = getIntent().getExtras();
  if( extras == null ){
    CloseActicity();
    return false;
  }//if

  if( !extras.containsKey( ConstsAct.KEY_MODE ) ){
    CloseActicity();
    return false;
  }//if


  mode = extras.getInt( ConstsAct.KEY_MODE );

  //NEW OPERATION
  if( mode == ConstsAct.MODE_EDIT ){

    if( !extras.containsKey( ConstsAct.KEY_ID_OPERATION ) ){
      CloseActicity();
      return false;
    }//if

    int id_operation = extras.getInt( ConstsAct.KEY_ID_OPERATION );

    item = Data.instance.db_budget_op.GetOperation( id_operation );

    if( item == null ){
      CloseActicity();
      return false;
    }//if

    is_set_personal_sum = true;

    return true;
  }//if( mode == ConstsAct.MODE_EDIT )


  //NEW OPERATION
  if( mode == ConstsAct.MODE_NEW ){

    if( !extras.containsKey( ConstsAct.KEY_ID_BUDGET ) ){
      CloseActicity();
      return false;
    }//if

    int    id_budget = extras.getInt( ConstsAct.KEY_ID_BUDGET );
    Budget budget    = Data.instance.db_budget.Get( id_budget );


    if( budget == null ){
      CloseActicity();
      return false;
    }//if

    item = new OperationBudget();
    item.id_budget = id_budget;
    item.id_curr = budget.id_curr_def;
    item.type = Operation.TYPE_OUT;
    item.date_created = Utils.DateToLong( new GregorianCalendar() );

    return true;
  }//if(mode == MODE_NEW )

  return false;
}//IsParamsOk
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void Init(){
  InitToolbar();
  InitControls();
  InitSumOpButtons();
  InitDataUserOptions();
}//Init
//--------------------------------------------------------------------
private void InitToolbar(){

  String title = "";

  switch( mode ){
    case ConstsAct.MODE_NEW:
      title = getString( R.string.toolbar_title_budget_op_add );
      break;

    case ConstsAct.MODE_EDIT:
      title = getString( R.string.toolbar_title_budget_op_edit );
      break;
  }//switch

  Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
  toolbar.setTitle( title );
  setSupportActionBar( toolbar );

  android.support.v7.app.ActionBar ab = getSupportActionBar();
  if( ab != null ){
    ab.setDisplayHomeAsUpEnabled( true );
    ab.setHomeButtonEnabled( true );
  }//if actionbar != null
}//Init
//--------------------------------------------------------------------
private void InitControls(){

  cmp_name = (CmpInputText) findViewById( R.id.cmp_name );
  cmp_comment = (CmpInputText) findViewById( R.id.cmp_comment );
  cmp_type = (CmpInputText) findViewById( R.id.cmp_type );
  cmp_date = (CmpInputText) findViewById( R.id.cmp_date );
  cmp_sum = (CmpInputTextBtn) findViewById( R.id.cmp_sum );
  cmp_users_sums = (CmpUserListSums) findViewById( R.id.cmp_users_sums );

  cmp_type.SetEnabled( false );
  cmp_sum.SetEnabled( false );
  cmp_date.SetEnabled( false );
  cmp_users_sums.SetEnabled( false );


  OnEvent on_click_sum = new OnEvent(){
    @Override
    public void OnEvent(){
      OnClick_PickSum();
    }//OnEvent
  };//on_click_sum

  OnEvent on_click_currency = new OnEvent(){
    @Override
    public void OnEvent(){
      OnClick_PickCurrency();
    }//OnEvent
  };//on_click_currency

  OnResultId on_click_sum_users = new OnResultId(){
    @Override
    public void OnResult( int id_user ){
      OnClick_PickUsersSum( id_user );
    }//OnResult
  };//on_click_sum_users

  OnEvent on_click_users = new OnEvent(){
    @Override
    public void OnEvent(){
      OnClick_PickUsers();
    }//OnEvent
  };//on_click_users

  OnEvent on_click_type = new OnEvent(){
    @Override
    public void OnEvent(){
      OnClick_PickType();
    }//OnEvent
  };//on_click_type

  OnEvent on_click_date = new OnEvent(){
    @Override
    public void OnEvent(){
      OnClick_PickDate();
    }//OnEvent
  };//on_click_date


  cmp_sum.SetOnItemClick( on_click_sum );
  cmp_sum.SetOnBtnClick( on_click_currency );

  cmp_users_sums.SetOnBtnClick( on_click_users );
  cmp_users_sums.SetOnItemClick( on_click_sum_users );

  cmp_type.SetOnItemClick( on_click_type );
  cmp_date.SetOnItemClick( on_click_date );

}//InitControls
//--------------------------------------------------------------------
private void InitSumOpButtons(){

  View.OnClickListener cl_btn_share_evenly = new View.OnClickListener(){
    @Override
    public void onClick( View v ){
      PerformSumOperation( SUM_SHARE_EVENLY );
      ConsiderSums();
    }//onClick
  };//cl_btn_share_evenly

  View.OnClickListener cl_btn_get_from_users = new View.OnClickListener(){
    @Override
    public void onClick( View v ){
      PerformSumOperation( SUM_GET_FROM_USERS );
      ConsiderSums();
    }//onClick
  };//cl_btn_get_from_users

  View.OnClickListener cl_btn_diff_to_tips = new View.OnClickListener(){
    @Override
    public void onClick( View v ){
      PerformSumOperation( SUM_DIFF_AS_TIPS );
      ConsiderSums();
    }//onClick
  };//cl_btn_diff_to_tips


  tv_btn_sum_op_share_sum_evenly = (TextView) findViewById( R.id.tv_btn_sum_op_share_sum_evenly );
  tv_btn_sum_op_get_sum_from_total = (TextView) findViewById( R.id.tv_btn_sum_op_get_sum_from_users );
  tv_btn_sum_op_set_sum_diff_as_tips = (TextView) findViewById( R.id.tv_btn_sum_op_set_sum_diff_as_tips );

  tv_btn_sum_op_share_sum_evenly.setVisibility( View.GONE );
  tv_btn_sum_op_get_sum_from_total.setVisibility( View.GONE );
  tv_btn_sum_op_set_sum_diff_as_tips.setVisibility( View.GONE );

  tv_btn_sum_op_share_sum_evenly.setOnClickListener( cl_btn_share_evenly );
  tv_btn_sum_op_get_sum_from_total.setOnClickListener( cl_btn_get_from_users );
  tv_btn_sum_op_set_sum_diff_as_tips.setOnClickListener( cl_btn_diff_to_tips );


  spacer = findViewById( R.id.spacer );
  spacer.setVisibility( View.GONE );

}//InitSumOpButtons
//--------------------------------------------------------------------
private void InitDataUserOptions(){

  SparseArray<BudgetUser> budget_users =
      Data.instance.db_budget_users.GetByBudget( item.id_budget );

  users_operations = new SparseArray<>();
  option_users = new Options();

  String        user_name;
  BudgetUser    budget_user;
  OperationUser operation_user;

  switch( mode ){

    case ConstsAct.MODE_NEW:

      for( int i = 0; i < budget_users.size(); i++ ){
        budget_user = budget_users.valueAt( i );
        user_name = Data.instance.db_user.GetName( budget_user.id_user );

        operation_user = new OperationUser();
        operation_user.id_user = budget_user.id_user;
        users_operations.put( operation_user.id_user, operation_user );

        option_users.Add( budget_user.id_user, user_name, true );
      }//for
      break;


    case ConstsAct.MODE_EDIT:

      for( int i = 0; i < budget_users.size(); i++ ){
        budget_user = budget_users.valueAt( i );
        user_name = Data.instance.db_user.GetName( budget_user.id_user );
        option_users.Add( budget_user.id_user, user_name, false );
      }//for

      users_operations = Data.instance.db_user_op.GetOperations( item.id );
      SparseArray<OperationUser> users_operations_temp = new SparseArray<>();

      for( int i = 0; i < users_operations.size(); i++ ){
        operation_user = users_operations.valueAt( i );
        OperationUser operation_user_tmp = new OperationUser( operation_user );
        users_operations_temp.put( operation_user_tmp.id_user, operation_user_tmp );
      }//for

      users_operations = users_operations_temp;

      for( int i = 0; i < users_operations.size(); i++ ){
        operation_user = users_operations.valueAt( i );
        option_users.SetOptionStateById( operation_user.id_user, true );
      }//for
      break;
  }//switch

  RecalcSumUsers();
}//InitDataUserOptions
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void UpdateData(){
  cmp_name.SetInputValue( item.name );
  cmp_comment.SetInputValue( item.comment );

  UpdateCurrencyCode();
  UpdateType();
  UpdateSum();
  UpdateSumUsers();
  UpdateDate();
}//UpdateData
//--------------------------------------------------------------------
private void UpdateCurrencyCode(){
  String curr_code = Data.instance.db_currency.GetCodeById( item.id_curr );
  if( !curr_code.equals( "" ) ){
    cmp_sum.SetButtonLabel( curr_code );
  }//id curr_code
}//UpdateCurrencyCode
//--------------------------------------------------------------------
private void UpdateType(){
  String type = Operation.ToString( item.type );
  cmp_type.SetInputValue( type );
}//UpdateType
//--------------------------------------------------------------------
private void UpdateSum(){
  cmp_sum.SetInputValue( Utils.FormatFloatToString( item.amount ) );
}//UpdateSum
//--------------------------------------------------------------------
private void UpdateSumUsers(){
  cmp_users_sums.SetData( users_operations );
}//ConsiderSums
//--------------------------------------------------------------------
private void UpdateDate(){

  String date_str = "";

  if( item.date_created == -1 ){
    cmp_date.SetInputValue( date_str );
    return;
  }//if

  date_str = Utils.FormatLongDateToStringFull( item.date_created );

  cmp_date.SetInputValue( date_str );

}//UpdatePaydate
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void ProcessSum( float sum ){
  item.amount = sum;
  UpdateSum();
  ConsiderSums();
}//ProcessSum
//--------------------------------------------------------------------
private void ProcessUserSum( float number, int id_user ){

  is_set_personal_sum = true;

  OperationUser operation_user = users_operations.get( id_user );
  operation_user.amount_pers = number;
  operation_user.amount_shared = 0;

  RecalcSumUsers();
  ConsiderSums();

}//ProcessUserSum
//--------------------------------------------------------------------
private void ConsiderSums(){

  tv_btn_sum_op_share_sum_evenly.setVisibility( View.GONE );
  tv_btn_sum_op_get_sum_from_total.setVisibility( View.GONE );
  tv_btn_sum_op_set_sum_diff_as_tips.setVisibility( View.GONE );

  spacer.setVisibility( View.GONE );

  float sum       = item.amount;
  float sum_users = GetSumUsers();

  if( !is_set_personal_sum ){
    PerformSumOperation( SUM_SHARE_EVENLY );
    return;
  }//if

  if( sum == sum_users ){
    cmp_sum.SetErrorOff();
    return;
  }//if

  cmp_sum.SetErrorOn( Utils.GetResString( R.string.error_field_sums_are_not_match ) );
  spacer.setVisibility( View.VISIBLE );

  tv_btn_sum_op_share_sum_evenly.setVisibility( View.VISIBLE );
  tv_btn_sum_op_get_sum_from_total.setVisibility( View.VISIBLE );

  if( sum > sum_users ){
    if( item.type != Operation.TYPE_IN ){
      tv_btn_sum_op_set_sum_diff_as_tips.setVisibility( View.VISIBLE );
    }//if
  }

}//ConsiderSums
//--------------------------------------------------------------------
private void RecalcSumUsers(){

  SparseArray<OperationUser> users_operations_temp = new SparseArray<>();

  Options.Option option;
  OperationUser  operation_user;
  OperationUser  operation_user_old;

  for( int i = 0; i < option_users.list_options.size(); i++ ){
    option = option_users.list_options.valueAt( i );

    if( option.is_selected ){
      operation_user = new OperationUser();
      operation_user.id_user = option.id;

      operation_user_old = users_operations.get( operation_user.id_user );
      if( operation_user_old != null ){
        operation_user.amount_pers = operation_user_old.amount_pers;
        operation_user.amount_shared = operation_user_old.amount_shared;
      }//if

      users_operations_temp.put( operation_user.id_user, operation_user );
    }//if
  }//for

  users_operations = users_operations_temp;

  UpdateSumUsers();

}//RecalcSumUsers
//--------------------------------------------------------------------
private float GetSumUsers(){

  OperationUser item       = null;
  float         total      = 0;
  int           user_count = users_operations.size();

  for( int i = 0; i < user_count; i++ ){
    item = users_operations.valueAt( i );
    total += (item.amount_pers + item.amount_shared);
  }//for

  return total;
}//GetUsersSum
//--------------------------------------------------------------------
private void PerformSumOperation( int operation_type ){

  OperationUser operation_user = null;

  float amount_part = 0;
  float amount_tips = 0;
  float total_sum   = 0;
  int   user_count  = users_operations.size();

  switch( operation_type ){

    case SUM_SHARE_EVENLY:
      amount_part = item.amount / user_count;

      for( int i = 0; i < user_count; i++ ){
        operation_user = users_operations.valueAt( i );
        operation_user.amount_pers = amount_part;
        operation_user.amount_shared = 0;
      }//for
      break;

    case SUM_GET_FROM_USERS:
      ProcessSum( GetSumUsers() );
      break;

    case SUM_DIFF_AS_TIPS:

      for( int i = 0; i < user_count; i++ ){
        operation_user = users_operations.valueAt( i );
        total_sum += operation_user.amount_pers;
        operation_user.amount_shared = 0;
      }//for

      amount_tips = (item.amount - total_sum) / user_count;//tips

      for( int i = 0; i < user_count; i++ ){
        operation_user = users_operations.valueAt( i );
        operation_user.amount_shared = amount_tips;
      }//for

      break;
  }//switch

  RecalcSumUsers();
  UpdateSum();
  UpdateSumUsers();

}//PerformSumOperation
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnClick_PickCurrency(){

  String  title   = getString( R.string.dialog_pick_currency_title );
  Options options = Data.instance.db_currency.getCurrencyOptions();
  options.SetSelectedOption( item.id_curr );

  OnResultPosition on_result = new OnResultPosition(){
    @Override
    public void OnResult( int position ){

      dialog_currency.dismiss();
      dialog_currency = null;

      Options options = Data.instance.db_currency.getCurrencyOptions();
      item.id_curr = options.GetIdByPosition( position );

      UpdateCurrencyCode();
      UpdateSum();
    }//OnResult
  };//on_result


  if( dialog_currency != null ){
    dialog_currency.dismiss();
    dialog_currency = null;
  }//if

  dialog_currency = new DialogPickOptionSingl();
  dialog_currency.SetData( title, options );
  dialog_currency.SetOnResult( on_result );
  dialog_currency.show( getSupportFragmentManager(), "TAG" );

}//OnClick_PickContact
//--------------------------------------------------------------------
private void OnClick_PickType(){

  String  title   = getString( R.string.dialog_pick_operation_type_title );
  Options options = Operation.options_type_new;

  if( mode == ConstsAct.MODE_EDIT ){
    options = Operation.options_type_edit;
  }//if

  options.SetSelectedOption( item.type );

  OnResultPosition on_result = new OnResultPosition(){
    @Override
    public void OnResult( int position ){

      dialog_type.dismiss();
      dialog_type = null;

      item.type = Operation.options_type_new.GetIdByPosition( position );
      UpdateType();

    }//OnResult
  };//on_result


  if( dialog_type != null ){
    dialog_type.dismiss();
    dialog_type = null;
  }//if

  dialog_type = new DialogPickOptionSingl();
  dialog_type.SetData( title, options );
  dialog_type.SetOnResult( on_result );
  dialog_type.show( getSupportFragmentManager(), "TAG" );

}//OnClick_PickType
//--------------------------------------------------------------------
private void OnClick_PickSum(){

  String title = getString( R.string.dialog_enter_sum_title );

  OnResultNumber on_result = new OnResultNumber(){
    @Override
    public void OnResult( float number ){

      dialog_calc_sum.dismiss();
      dialog_calc_sum = null;

      ProcessSum( number );
    }//OnResult
  };//on_result

  OnResultNumber on_tips = new OnResultNumber(){
    @Override
    public void OnResult( float number ){

      dialog_calc_sum.dismiss();
      dialog_calc_sum = null;

      ProcessSum( number );
      OnClick_PickTip();

    }//OnEvent
  };//on_tips


  if( dialog_calc_sum != null ){
    dialog_calc_sum.dismiss();
    dialog_calc_sum = null;
  }//if

  dialog_calc_sum = new DialogCalcSum();
  dialog_calc_sum.SetData( title, item.amount );
  dialog_calc_sum.SetOnResult( on_result );
  dialog_calc_sum.SetOnTips( on_tips );
  dialog_calc_sum.show( getSupportFragmentManager(), "TAG" );

}//OnClick_PickSum
//--------------------------------------------------------------------
private void OnClick_PickTip(){

  String title = getString( R.string.dialog_tips_title );

  OnResultNumber on_result = new OnResultNumber(){
    @Override
    public void OnResult( float number ){

      dialog_tip.dismiss();
      dialog_tip = null;

      ProcessSum( number );
    }//OnResult
  };//on_result

  OnResultNumber on_calc = new OnResultNumber(){
    @Override
    public void OnResult( float number ){

      dialog_tip.dismiss();
      dialog_tip = null;

      ProcessSum( number );
      OnClick_PickSum();

    }//OnEvent
  };//on_tips


  if( dialog_tip != null ){
    dialog_tip.dismiss();
    dialog_tip = null;
  }//if

  dialog_tip = new DialogCalcTips();
  dialog_tip.SetData( title, item.amount );
  dialog_tip.SetOnResult( on_result );
  dialog_tip.SetOnCalc( on_calc );
  dialog_tip.show( getSupportFragmentManager(), "TAG" );
}//OnClick_PickTip
//--------------------------------------------------------------------
private void OnClick_PickUsersSum( final int id_user ){

  String title = getString( R.string.dialog_enter_sum_title );

  OnResultNumber on_result = new OnResultNumber(){
    @Override
    public void OnResult( float number ){

      dialog_calc_user.dismiss();
      dialog_calc_user = null;

      ProcessUserSum( number, id_user );

    }//OnResult
  };//on_result

  OnResultNumber on_tips = new OnResultNumber(){
    @Override
    public void OnResult( float number ){

      dialog_calc_user.dismiss();
      dialog_calc_user = null;

      ProcessUserSum( number, id_user );
      OnClick_PickUserTip( id_user );

    }//OnEvent
  };//on_tips


  if( dialog_calc_user != null ){
    dialog_calc_user.dismiss();
    dialog_calc_user = null;
  }//if

  OperationUser operation_user = users_operations.get( id_user );

  dialog_calc_user = new DialogCalcSum();
  dialog_calc_user.SetData( title, operation_user.amount_pers );
  dialog_calc_user.SetOnResult( on_result );
  dialog_calc_user.SetOnTips( on_tips );
  dialog_calc_user.show( getSupportFragmentManager(), "TAG" );

}//OnClick_PickUsersSum
//--------------------------------------------------------------------
private void OnClick_PickUserTip( final int id_user ){

  String title = getString( R.string.dialog_tips_title );

  OnResultNumber on_result = new OnResultNumber(){
    @Override
    public void OnResult( float number ){

      dialog_tip_user.dismiss();
      dialog_tip_user = null;

      ProcessUserSum( number, id_user );

    }//OnResult
  };//on_result

  OnResultNumber on_calc = new OnResultNumber(){
    @Override
    public void OnResult( float number ){

      dialog_tip_user.dismiss();
      dialog_tip_user = null;

      ProcessUserSum( number, id_user );
      OnClick_PickUsersSum( id_user );

    }//OnEvent
  };//on_tips


  if( dialog_tip_user != null ){
    dialog_tip_user.dismiss();
    dialog_tip_user = null;
  }//if

  OperationUser operation_user = users_operations.get( id_user );

  dialog_tip_user = new DialogCalcTips();
  dialog_tip_user.SetData( title, operation_user.amount_pers );
  dialog_tip_user.SetOnResult( on_result );
  dialog_tip_user.SetOnCalc( on_calc );
  dialog_tip_user.show( getSupportFragmentManager(), "TAG" );

}//OnClick_PickTip
//--------------------------------------------------------------------
private void OnClick_PickUsers(){

  String title = getString( R.string.dialog_pick_users_title );

  OnResultOptions on_result = new OnResultOptions(){
    @Override
    public void OnResult( Options options ){

      dialog_pick_users.dismiss();
      dialog_pick_users = null;

      option_users = options;
      is_set_personal_sum = false;
      RecalcSumUsers();
      ConsiderSums();

    }//OnResult
  };//on_result


  if( dialog_pick_users != null ){
    dialog_pick_users.dismiss();
    dialog_pick_users = null;
  }//if

  dialog_pick_users = new DialogPickOptionMult();
  dialog_pick_users.SetData( title, option_users );
  dialog_pick_users.SetOnResult( on_result );
  dialog_pick_users.show( getSupportFragmentManager(), "TAG" );
}//OnClick_PickUsers
//--------------------------------------------------------------------
private void OnClick_PickDate(){

  String title = getString( R.string.dialog_pick_date_title );

  OnResultDate on_result = new OnResultDate(){
    @Override
    public void OnResult( long date_long ){

      dialog_date.dismiss();
      dialog_date = null;

      item.date_created = date_long;
      UpdateDate();

    }//OnResult
  };//OnResult


  if( dialog_date != null ){
    dialog_date.dismiss();
    dialog_date = null;
  }//if

  dialog_date = new DialogPickDate();
  dialog_date.SetData( title, item.date_created );
  dialog_date.SetOnResult( on_result );
  dialog_date.show( getSupportFragmentManager(), "TAG" );
}//OnClick_PickDate
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private boolean ProcessOperationData(){

  boolean is_data_ok = true;

  cmp_name.SetErrorOff();
  item.name = cmp_name.GetInputValue();

  if( item.name.equals( "" ) ){
    cmp_name.SetErrorOn( getString( R.string.error_field_cannot_be_empty ) );
    is_data_ok = false;
  }//name

  if( !is_data_ok ){
    return false;
  }//if

  item.comment = cmp_comment.GetInputValue();

  return true;
}//ProcessOperationData
//--------------------------------------------------------------------
private void ProcessOperationDataUsers( int type ){

  OperationUser operation;

  for( int i = 0; i < users_operations.size(); i++ ){

    operation = users_operations.valueAt( i );

    operation.name = item.name;
    operation.comment = item.comment;
    operation.type = type;
    operation.id_budget = item.id_budget;
    operation.id_op = item.id;
    operation.id_curr = item.id_curr;
    operation.date_created = item.date_created;
  }//for

}//ProcessOperationDataUsers
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void CloseActicity(){
  Intent return_intent = new Intent();
  setResult( RESULT_CANCELED, return_intent );
  finish();
}//CloseActicity
//--------------------------------------------------------------------
private void SaveAndExit(){

  if( !ProcessOperationData() ){
    return;
  }//if


  switch( mode ){

    case ConstsAct.MODE_NEW:

      if( item.type != Operation.TYPE_IN_OUT ){

        if( !AddOperation( item.type ) ){
          return;
        }//if

      }else{

        if( !AddOperation( Operation.TYPE_IN ) ){
          return;
        }//if

        if( !AddOperation( Operation.TYPE_OUT ) ){
          return;
        }//if
      }//if item.type == Operation.TYPE_IN_OUT

      break;

    case ConstsAct.MODE_EDIT:
      Data.instance.db_budget_op.Update( item );
      Data.instance.db_user_op.RemoveByBudgetOperation( item.id_budget, item.id );

      ProcessOperationDataUsers( item.type );
      Data.instance.db_user_op.AddMultiple( users_operations );
      break;
  }//switch

  Intent return_intent = new Intent();
  return_intent.putExtra( ConstsAct.KEY_ID, item.id );
  setResult( RESULT_OK, return_intent );
  finish();
}//SaveAndExit
//--------------------------------------------------------------------

//--------------------------------------------------------------------
private boolean AddOperation( int item_type ){

  item.type = item_type;
  item.id = Data.instance.db_budget_op.Add( item );

  if( item.id != -1 ){
    ProcessOperationDataUsers( item_type );
    Data.instance.db_user_op.AddMultiple( users_operations );
  }else{
    Utils.ToastS( Utils.GetResString( R.string.error_msg_failed_operation ) );
    return false;
  }//if failed

  return true;
}//AddOperationIn
//--------------------------------------------------------------------

}//Act_Budget
