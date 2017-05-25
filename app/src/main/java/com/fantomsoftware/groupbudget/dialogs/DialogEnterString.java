package com.fantomsoftware.groupbudget.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.interfaces.OnResultString;
import com.fantomsoftware.groupbudget.utils.Utils;


public class DialogEnterString extends DialogFragment{
   //---------------------------------------------------------------------------
   private String title;
   private String hint;
   private String result;

   private View view_custom;

   private TextInputLayout etl;
   private EditText        et;

   private OnResultString on_result = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @NonNull
   @Override
   public Dialog onCreateDialog( Bundle savedInstanceState ){

      DialogInterface.OnClickListener cl_ok = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int id ){
            OnOk();
         }
      };//cl_ok
      DialogInterface.OnClickListener cl_cancel = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int id ){
            OnClose();
         }
      };//cl_cancel

      LayoutInflater li = LayoutInflater.from( getContext() );
      view_custom = li.inflate( R.layout.dialog_enter_string, null );


      AlertDialog.Builder builder =
         new AlertDialog.Builder( getActivity(), R.style.MyAlertDialog );

      builder.setTitle( title );
      builder.setView( view_custom );
      builder.setPositiveButton( R.string.btn_ok, cl_ok );
      builder.setNegativeButton( R.string.btn_cancel, cl_cancel );

      Dialog dialog = builder.create();

      InitControls();

      return dialog;
   }//onCreateDialog
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetOnResult( OnResultString on_result ){
      this.on_result = on_result;
   }//SetOnSelected
   //---------------------------------------------------------------------------
   public void SetData( String title, String hint ){
      this.title = title;
      this.hint = hint;
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void InitControls(){
      etl = (TextInputLayout) view_custom.findViewById( R.id.etl );
      et = (EditText) view_custom.findViewById( R.id.et );
      et.setText( hint );
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private boolean IsOK(){

      result = et.getText().toString();

      if( result.equals( "" ) ){
         etl.setErrorEnabled( true );
         etl.setError( Utils.context.getString( R.string.error_field_cannot_be_empty ) );
         return false;
      }else{
         etl.setErrorEnabled( false );
      }//if

      return true;
   }//IsOK
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnOk(){

      if( !IsOK() ){
         return;
      }//if

      if( on_result != null ){
         on_result.OnResult( result );
      }//if

      this.dismiss();
   }//OnOk
   //---------------------------------------------------------------------------
   private void OnClose(){
      this.dismiss();
   }//OnClose
   //---------------------------------------------------------------------------

}//DialogEnterString