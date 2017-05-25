package com.fantomsoftware.groupbudget.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.interfaces.OnResultNumber;
import com.fantomsoftware.groupbudget.utils.Utils;


public class DialogEnterNumber extends DialogFragment{
   //---------------------------------------------------------------------------
   private String title;
   private float  number;

   private View     view_custom;
   private EditText et_number;

   private OnResultNumber on_result;
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
            OnCancel();
         }
      };//cl_cancel

      LayoutInflater li = LayoutInflater.from( getContext() );
      view_custom = li.inflate( R.layout.dialog_enter_number, null );


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
   public void SetOnResult( OnResultNumber on_result ){
      this.on_result = on_result;
   }//SetOnSelected
   //---------------------------------------------------------------------------
   public void SetData( String title, float number ){
      this.title = title;
      this.number = number;
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void InitControls(){
      et_number = (EditText) view_custom.findViewById( R.id.et_number );
      et_number.setText( Utils.FormatFloatToString( number ) );
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnOk(){

      String number_str = et_number.getText().toString();

      if( number_str.equals( "" ) ){
         return;
      }//if

      if( on_result != null ){
         on_result.OnResult( Float.parseFloat( number_str ) );
      }//if

      this.dismiss();
   }//OnOk
   //---------------------------------------------------------------------------
   private void OnCancel(){
      this.dismiss();
   }//OnCancel
   //---------------------------------------------------------------------------

}//DialogEnterNumber
