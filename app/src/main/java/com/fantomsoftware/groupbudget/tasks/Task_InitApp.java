package com.fantomsoftware.groupbudget.tasks;

import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.utils.Permissions;
import com.fantomsoftware.groupbudget.utils.Utils;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task return parameter
>
*/

public class Task_InitApp extends AsyncTask<Void, Integer, Boolean>{
   //---------------------------------------------------------------------------
   private OnEvent on_app_inited = null;
   private String  intit_prefix  = "";
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetOnResult( OnEvent on_result ){
      this.on_app_inited = on_result;
   }//SetOnSelected
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected Boolean doInBackground( Void... param ){

      publishProgress( 5 );
      Pause( 100 );

      Utils.InitReferences();
      intit_prefix = Utils.GetResString( R.string.app_init_initialization );
      publishProgress( 10 );
      Pause( 100 );

      Utils.SetAppShortcutIcon();
      publishProgress( 15 );
      Pause( 100 );

      publishProgress( 25 );
      Pause( 100 );
      if( !PermissionStorage() ){
         return false;
      }//if app has no permissions for storage access
      publishProgress( 40 );
      Pause( 100 );


      //not critical permission
      if( PermissionAccounts() ){
         //todo ask for account
      }//if app has no permissions for accounts access
      publishProgress( 60 );
      Pause( 100 );

      if( !Data.is_inited ){
         if( !Data.instance.Init() ){
            return false;
         }//if failed to init data
      }//if not inited

      publishProgress( 100 );
      Pause( 100 );

      return true;
   }//doInBackground
   //---------------------------------------------------------------------------
   @Override
   protected void onProgressUpdate( Integer... progress ){

      if( Data.instance.act_splash != null ){
         Data.instance.act_splash.SetState( intit_prefix + "\n" + progress[0] + "%" );
      }//if

   }//onProgressUpdate
   //---------------------------------------------------------------------------
   @Override
   protected void onPostExecute( Boolean result ){

      if( result ){
         OnTaskSuccess();
      }else{
         OnTaskFailed();
      }//if

   }//onPostExecute
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnTaskSuccess(){

      String state = Utils.GetResString( R.string.app_init_completed ) + "\n" + "100%";
      Data.instance.act_splash.SetState( state );
      Pause( 500 );

      if( on_app_inited != null ){
         on_app_inited.OnEvent();
      }//if

   }//OnTaskSuccess
   //---------------------------------------------------------------------------
   private void OnTaskFailed(){

      Utils.is_need_to_exit = true;

      View.OnClickListener on_action_click = new View.OnClickListener(){
         @Override
         public void onClick( View view ){
            Utils.TerminateApplication();
         }//onClick
      };//on_action_click

      String state = Utils.GetResString( R.string.app_init_failed );
      Data.instance.act_splash.SetState( state );

      View   view        = Utils.activity.findViewById( android.R.id.content );
      String msg         = Utils.GetResString( R.string.app_init_failed_end );
      String action_text = Utils.GetResString( R.string.app_int_btn_close );

      Snackbar.make( view, msg, Snackbar.LENGTH_INDEFINITE ).
         setAction( action_text, on_action_click ).show();

   }//OnTaskSuccess
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Pause( int period ){

      try{
         Thread.sleep( period );
      }catch( InterruptedException e ){
         e.printStackTrace();
      }//try

   }//Pause
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private boolean PermissionStorage(){

      if( Permissions.PermissionCheck_Storage() ){
         return true;
      }//if app has storage permission

      Utils.is_need_to_exit = true;
      Data.instance.is_wait_permission = true;


      while( Data.instance.is_wait_permission ){

         try{
            Thread.sleep( 1000 );
         }catch( InterruptedException e ){
            e.printStackTrace();
         }//try
      }//while


      if( Permissions.IsPermissonGranted_Storage() ){
         return true;
      }else{
         return false;
      }//if app has storage permission

   }//PermissionStorage
   //---------------------------------------------------------------------------
   private boolean PermissionAccounts(){
      /*
      if( Permissions.PermissionCheck_Accounts() ){
         return true;
      }//if app has storage permission

      Utils.is_need_to_exit = true;
      Data.instance.is_wait_permission = true;


      while( Data.instance.is_wait_permission ){

         try{
            Thread.sleep( 1000 );
         }catch( InterruptedException e ){
            e.printStackTrace();
         }//try
      }//while


      if( Permissions.IsPermissonGranted_Accounts() ){
         return true;
      }else{
         return false;
      }//if app has storage permission
      */
      return true;
   }//PermissionAccounts
   //---------------------------------------------------------------------------

}//Task_InitApp