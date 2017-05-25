package com.fantomsoftware.groupbudget.views_act;


import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.tasks.Task_InitApp;
import com.fantomsoftware.groupbudget.utils.Utils;


public class Act_Splash extends AppCompatActivity{
   //---------------------------------------------------------------------------
   private TextView tv_state = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected void onCreate( Bundle savedInstanceState ){

      SetStatusBarColor();

      super.onCreate( savedInstanceState );
      setContentView( R.layout.act_splash );

      Utils.UpdateActivity( this );
      Data.instance.act_splash = this;

      tv_state = (TextView) findViewById( R.id.tv_state );

      String state = getString( R.string.app_init_initialization ) + "\n" + "0%";
      tv_state.setText( state );

   }//onCreate
   //---------------------------------------------------------------------------
   @Override
   protected void onResume(){
      super.onResume();
      Utils.UpdateActivity( this );
   }//onResume
   //---------------------------------------------------------------------------
   @Override
   protected void onDestroy(){
      super.onDestroy();
      Data.instance.act_splash = null;
      Data.instance.on_splash_showed = null;
   }//onDestroy
   //---------------------------------------------------------------------------
   @Override
   public void onAttachedToWindow(){
      super.onAttachedToWindow();
      StartInitTask();
   }//onAttachedToWindow
   //---------------------------------------------------------------------------
   @Override
   public void onBackPressed(){
      super.onBackPressed();
      if( Utils.is_need_to_exit ){
         Utils.TerminateApplication();
      }//if
   }//onBackPressed
   //---------------------------------------------------------------------------
   @Override
   public void onRequestPermissionsResult(
      int request, @NonNull String permissions[], @NonNull int[] grant_permissions ){

      switch( request ){

         case ConstsAct.REQUEST_STORAGE:
            Data.instance.is_wait_permission = false;
            break;//case ConstsAct.REQUEST_STORAGE:

         case ConstsAct.REQUEST_ACCOUNTS:
            Data.instance.is_wait_permission = false;
            break;//case ConstsAct.REQUEST_STORAGE:

      }//switch

      /*
      // If request is cancelled, the result arrays are empty.
            if( grant_permissions.length == 2 &&
               grant_permissions[0] == PackageManager.PERMISSION_GRANTED &&
               grant_permissions[1] == PackageManager.PERMISSION_GRANTED ){

               // permission was granted, yay! Do the
               // contacts-related task you need to do.

            }else{
               // permission denied, boo! Disable the
               // functionality that depends on this permission.
            }
      */

   }//onRequestPermissionsResult
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetStatusBarColor(){

      if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
         Window window = getWindow();
         window.addFlags( WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
         window.setStatusBarColor( Color.BLACK );
      }//LOLLIPOP

   }//SetStatusBarColor
   //---------------------------------------------------------------------------
   public void SetState( String state ){
      if( tv_state != null ){
         tv_state.setText( state );
      }//if
   }//SetState
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void StartInitTask(){

      OnEvent on_result = new OnEvent(){
         @Override
         public void OnEvent(){
            ShowMainActivity();
         }//OnEvent
      };//event

      Data.instance.task = new Task_InitApp();
      Data.instance.task.SetOnResult( on_result );
      Data.instance.task.execute();

   }//StartInitTask
   //---------------------------------------------------------------------------
   private void ShowMainActivity(){

      Intent intent = new Intent( this, Act.class );
      startActivity( intent );

      this.finish();

      overridePendingTransition( R.anim.animate_fadein, R.anim.animate_fadeout );
   }//ShowMainActivity
   //---------------------------------------------------------------------------

}//Act_Splash
