package com.fantomsoftware.groupbudget.views_act;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.cmp.CmpInputText;
import com.fantomsoftware.groupbudget.cmp.CmpUserList;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.data.Budget;
import com.fantomsoftware.groupbudget.data.BudgetUser;
import com.fantomsoftware.groupbudget.data.Currency;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.data.User;
import com.fantomsoftware.groupbudget.dialogs.DialogPickOptionMult;
import com.fantomsoftware.groupbudget.dialogs.DialogPickOptionSingl;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.interfaces.OnResultOptions;
import com.fantomsoftware.groupbudget.interfaces.OnResultPosition;
import com.fantomsoftware.groupbudget.utils.Utils;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;


public class Act_Budget extends AppCompatActivity{
   //---------------------------------------------------------------------------
   private Budget item = null;
   private int    mode = ConstsAct.MODE_NEW;

   private CmpInputText cmp_name     = null;
   private CmpInputText cmp_currency = null;
   private CmpInputText cmp_comment  = null;
   private CmpUserList  cmp_users    = null;

   private DialogPickOptionSingl dialog_currency   = null;
   private DialogPickOptionMult  dialog_pick_users = null;

   private Options           option_users   = null;
   private SparseArray<User> data_users     = null;
   private SparseArray<User> data_users_all = null;
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   @Override
   protected void onCreate( Bundle savedInstanceState ){
      super.onCreate( savedInstanceState );
      setContentView( R.layout.act_budget );


      if( !IsParamsOk() ){
         return;
      }//if
      
      InitToolbar();
      InitControls();
      InitDataUserOptions();
      
      UpdateData();
   }//onCreate
   //---------------------------------------------------------------------------
   @Override
   public boolean onCreateOptionsMenu( Menu menu ){
      MenuInflater menuInflater = getMenuInflater();
      menuInflater.inflate( R.menu.menu_act_budget, menu );
      return true;
   }//onCreateOptionsMenu
   //---------------------------------------------------------------------------
   @Override
   public boolean onPrepareOptionsMenu( Menu menu ){
      super.onPrepareOptionsMenu( menu );
      return true;
   }//onPrepareOptionsMenu
   //---------------------------------------------------------------------------
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
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
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

      if( mode == ConstsAct.MODE_EDIT ){
         if( !extras.containsKey( ConstsAct.KEY_ID ) ){
            CloseActicity();
            return false;
         }//if

         int id = extras.getInt( ConstsAct.KEY_ID );

         item = Data.instance.db_budget.Get( id );

         if( item == null ){
            CloseActicity();
            return false;
         }//if

         return true;
      }//if( mode == ConstsAct.MODE_EDIT )

      if( mode == ConstsAct.MODE_NEW ){

         item = new Budget();
         item.id_curr_def = Currency.default_currnecy_id;
         item.date_created = Utils.DateToLong( new GregorianCalendar() );

         return true;
      }//if(mode == MODE_NEW )

      return false;
   }//IsParamsOk
   //---------------------------------------------------------------------------
   private boolean IsDataOk(){

      boolean is_data_ok = true;

      cmp_name.SetErrorOff();

      item.name = cmp_name.GetInputValue();
      item.comment = cmp_comment.GetInputValue();

      if( item.name.equals( "" ) ){
         cmp_name.SetErrorOn( getString( R.string.error_field_cannot_be_empty ) );
         is_data_ok = false;
      }//name


      if( !is_data_ok ){
         return false;
      }//if

      return true;
   }//IsDataOk
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void InitToolbar(){

      String title = "";

      switch( mode ){
         case ConstsAct.MODE_NEW:
            title = getString( R.string.toolbar_title_budget_add );
            break;

         case ConstsAct.MODE_EDIT:
            title = getString( R.string.toolbar_title_budget_edit );
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
   //---------------------------------------------------------------------------
   private void InitControls(){
      cmp_name = (CmpInputText) findViewById( R.id.cmp_name );
      cmp_currency = (CmpInputText) findViewById( R.id.cmp_currency );
      cmp_comment = (CmpInputText) findViewById( R.id.cmp_comment );
      cmp_users = (CmpUserList) findViewById( R.id.cmp_select_users );

      cmp_currency.SetEnabled( false );
      cmp_users.SetEnabled( false );


      OnEvent on_click_currency = new OnEvent(){
         @Override
         public void OnEvent(){
            OnClick_PickCurrency();
         }//OnEvent
      };//on_click_currency

      OnEvent on_click_users = new OnEvent(){
         @Override
         public void OnEvent(){
            OnClick_PickUsers();
         }//OnEvent
      };//on_click_users


      cmp_currency.SetOnItemClick( on_click_currency );
      cmp_users.SetOnBtnClick( on_click_users );
   }//InitControls
   //---------------------------------------------------------------------------
   private void InitDataUserOptions(){

      data_users_all = Data.instance.db_user.Get();
      option_users = new Options();

      User user;

      if( mode == ConstsAct.MODE_NEW ){

         for( int i = 0; i < data_users_all.size(); i++ ){
            user = data_users_all.valueAt( i );
            option_users.Add( user.id, user.name, true );
         }//for

      }else if( mode == ConstsAct.MODE_EDIT ){

         for( int i = 0; i < data_users_all.size(); i++ ){
            user = data_users_all.valueAt( i );
            option_users.Add( user.id, user.name, false );
         }//for


         SparseArray<BudgetUser> attached_users =
            Data.instance.db_budget_users.GetByBudget( item.id );

         for( int i = 0; i < attached_users.size(); i++ ){
            int id_user = attached_users.valueAt( i ).id_user;
            option_users.SetOptionStateById( id_user, true );
         }//for

      }//else if ConstsAct.MODE_EDIT

      UpdateDataUsers();
   }//InitDataUserOptions
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void UpdateData(){
      cmp_name.SetInputValue( item.name );
      cmp_comment.SetInputValue( item.comment );
      UpdateCurrencyCode();
      UpdateListUsers();
   }//UpdateData
   //---------------------------------------------------------------------------
   private void UpdateDataUsers(){

      data_users = new SparseArray<>();

      User           user;
      Options.Option option;

      for( int i = 0; i < option_users.list_options.size(); i++ ){
         option = option_users.list_options.valueAt( i );
         if( option.is_selected ){
            user = data_users_all.get( option.id );
            data_users.put( user.id, user );
         }//if
      }//for
   }//UpdateDataUsers
   //---------------------------------------------------------------------------
   private void UpdateCurrencyCode(){
      String curr_code = Data.instance.db_currency.GetCodeById( item.id_curr_def );
      if( !curr_code.equals( "" ) ){
         cmp_currency.SetInputValue( curr_code );
      }//id curr_code
   }//UpdateCurrencyCode
   //---------------------------------------------------------------------------
   private void UpdateListUsers(){
      cmp_users.SetData( data_users );
   }//UpdateListUsers
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnClick_PickCurrency(){

      String  title   = getString( R.string.dialog_pick_currency_title );
      Options options = Data.instance.db_currency.getCurrencyOptions();
      options.SetSelectedOption( item.id_curr_def );

      OnResultPosition on_result = new OnResultPosition(){
         @Override
         public void OnResult( int position ){

            dialog_currency.dismiss();
            dialog_currency = null;

            Options options = Data.instance.db_currency.getCurrencyOptions();
            item.id_curr_def = options.GetIdByPosition( position );

            UpdateCurrencyCode();
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
   //---------------------------------------------------------------------------
   private void OnClick_PickUsers(){

      String title = getString( R.string.dialog_pick_users_title );

      OnResultOptions on_result = new OnResultOptions(){
         @Override
         public void OnResult( Options options ){

            dialog_pick_users.dismiss();
            dialog_pick_users = null;

            option_users = options;
            UpdateDataUsers();
            UpdateListUsers();
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
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private List<BudgetUser> GetListBudgetUsers(){
      List<BudgetUser> list = new ArrayList<>();

      User user;

      for( int i = 0; i < data_users.size(); i++ ){
         user = data_users.valueAt( i );
         list.add( new BudgetUser( item.id, user.id ) );
      }//for

      return list;
   }//GetListBudgetUsers
   //---------------------------------------------------------------------------



   //---------------------------------------------------------------------------
   private void CloseActicity(){
      Intent return_intent = new Intent();
      setResult( RESULT_CANCELED, return_intent );
      finish();
   }//CloseActicity
   //---------------------------------------------------------------------------
   private void SaveAndExit(){

      boolean is_data_ok = IsDataOk();
      if( !is_data_ok ){
         return;
      }//if

      switch( mode ){
         case ConstsAct.MODE_NEW:
            item.id = Data.instance.db_budget.Add( item );
            break;

         case ConstsAct.MODE_EDIT:
            item.id = Data.instance.db_budget.Update( item );
            break;

         default:
            CloseActicity();
            return;
      }//switch


      if( item.id == -1 ){
         return;
      }


      List<BudgetUser> list = GetListBudgetUsers();
      Data.instance.db_budget_users.RemoveByBudget( item.id );
      Data.instance.db_budget_users.AddMultiple( list );


      Intent return_intent = new Intent();
      return_intent.putExtra( ConstsAct.KEY_ID, item.id );
      setResult( RESULT_OK, return_intent );
      finish();
   }//SaveAndExit
   //---------------------------------------------------------------------------

}//Act_Budget
