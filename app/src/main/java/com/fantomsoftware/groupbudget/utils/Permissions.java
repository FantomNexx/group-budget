package com.fantomsoftware.groupbudget.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.consts.ConstsAct;

public class Permissions{

   //STORAGE////////////////////////////////////////////////////////////////////
   //---------------------------------------------------------------------------
   public static boolean PermissionCheck_Storage(){

      if( !IsPermissonGranted_Storage() ){
         RequestPermission_Storage();
         return false;
      }//if

      return true;
   }//PermissionCheck_Storage
   //---------------------------------------------------------------------------
   public static boolean IsPermissonGranted_Storage(){

      int permission_state_read = ActivityCompat.checkSelfPermission(
         Utils.context, Manifest.permission.READ_EXTERNAL_STORAGE );

      int permission_state_write = ActivityCompat.checkSelfPermission(
         Utils.context, Manifest.permission.WRITE_EXTERNAL_STORAGE );

      if( permission_state_read == PackageManager.PERMISSION_GRANTED &&
         permission_state_write == PackageManager.PERMISSION_GRANTED ){
         return true;
      }//if

      return false;
   }//IsPermissonGranted_Storage
   //---------------------------------------------------------------------------
   public static void RequestPermission_Storage(){

      View.OnClickListener cl_grant = new View.OnClickListener(){
         @Override
         public void onClick( View view ){
            PermissionRequestUser_Storage();
         }//onClick
      };//cl_grant


      // Permission has not been granted and must be requested.
      if( ActivityCompat.shouldShowRequestPermissionRationale(
         Utils.activity, Manifest.permission.WRITE_EXTERNAL_STORAGE ) ){

         View view = Utils.activity.findViewById( android.R.id.content );
         String msg = Utils.GetResString( R.string.permission_app_needs_storage );
         String action_text = Utils.GetResString( R.string.permission_btn_choose );

         Snackbar.make( view, msg, Snackbar.LENGTH_INDEFINITE ).
            setAction( action_text, cl_grant ).show();

      }else{
         //We are really need STORAGE permission, we need to ask user.
         PermissionRequestUser_Storage();
      }//if we have no permissions

   }//RequestPermission_Storage
   //---------------------------------------------------------------------------
   public static void PermissionRequestUser_Storage(){

      String[] permissions = {
         Manifest.permission.READ_EXTERNAL_STORAGE,
         Manifest.permission.WRITE_EXTERNAL_STORAGE,
      };//permissions

      ActivityCompat.requestPermissions(
         Utils.activity, permissions, ConstsAct.REQUEST_STORAGE );

   }//PermissionRequestUser_Storage
   //---------------------------------------------------------------------------


   //ACCOUNTS///////////////////////////////////////////////////////////////////
   //---------------------------------------------------------------------------
   public static boolean PermissionCheck_Accounts(){

      if( !IsPermissonGranted_Accounts() ){
         RequestPermission_Accounts();
         return false;
      }//if

      return true;
   }//PermissionCheck_Accounts
   //---------------------------------------------------------------------------
   public static boolean IsPermissonGranted_Accounts(){

      int permission_get_accounts = ActivityCompat.checkSelfPermission(
         Utils.context, Manifest.permission.GET_ACCOUNTS );

      if( permission_get_accounts == PackageManager.PERMISSION_GRANTED ){
         return true;
      }//if

      return false;
   }//IsPermissonGranted_Accounts
   //---------------------------------------------------------------------------
   public static void RequestPermission_Accounts(){

      View.OnClickListener cl_grant = new View.OnClickListener(){
         @Override
         public void onClick( View view ){
            PermissionRequestUser_Accounts();
         }//onClick
      };//cl_grant


      // Permission has not been granted and must be requested.
      if( ActivityCompat.shouldShowRequestPermissionRationale(
         Utils.activity, Manifest.permission.GET_ACCOUNTS ) ){

         View view = Utils.activity.findViewById( android.R.id.content );
         String msg = Utils.GetResString( R.string.permission_app_needs_accounts );
         String action_text = Utils.GetResString( R.string.permission_btn_choose );

         Snackbar.make( view, msg, Snackbar.LENGTH_INDEFINITE ).
            setAction( action_text, cl_grant ).show();

      }else{
         //We are really need STORAGE permission, we need to ask user.
         PermissionRequestUser_Accounts();
      }//if we have no permissions

   }//RequestPermission_Accounts
   //---------------------------------------------------------------------------
   public static void PermissionRequestUser_Accounts(){

      String[] permissions = {
         Manifest.permission.GET_ACCOUNTS
      };//permissions

      ActivityCompat.requestPermissions(
         Utils.activity, permissions, ConstsAct.REQUEST_ACCOUNTS );

   }//PermissionRequestUser_Accounts
   //---------------------------------------------------------------------------
}//Permissions
