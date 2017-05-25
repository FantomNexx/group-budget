package com.fantomsoftware.groupbudget.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.interfaces.OnResultOptions;


public class DialogPickOptionMult extends DialogFragment{
   //---------------------------------------------------------------------------
   private String  title;
   private Options options;

   private OnResultOptions on_result;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @NonNull
   @Override
   public Dialog onCreateDialog( Bundle savedInstanceState ){

      DialogInterface.OnClickListener cl_ok =
         new DialogInterface.OnClickListener(){
            @Override
            public void onClick( DialogInterface dialog, int id ){
               OnOk();
            }
         };//cl_ok

      DialogInterface.OnClickListener cl_cancel =
         new DialogInterface.OnClickListener(){
            @Override
            public void onClick( DialogInterface dialog, int id ){
               OnCancel();
            }
         };//cl_cancel

      DialogInterface.OnMultiChoiceClickListener cl_selection =
         new DialogInterface.OnMultiChoiceClickListener(){
            @Override
            public void onClick( DialogInterface dialog, int which, boolean is_checked ){
               options.SetOptionStateByPos( which, is_checked );
            }//onClick
         };

      AlertDialog.Builder builder =
         new AlertDialog.Builder( getActivity(), R.style.MyAlertDialog );

      builder.setTitle( title );
      builder.setPositiveButton( R.string.btn_ok, cl_ok );
      builder.setNegativeButton( R.string.btn_cancel, cl_cancel );
      builder.setMultiChoiceItems(
         options.GetListStrings(),
         options.GetSelectedBooleans(),
         cl_selection );

      return builder.create();
   }//onCreateDialog
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetData( String title, Options options ){
      this.title = title;
      this.options = options;
   }//SetData
   //---------------------------------------------------------------------------
   public void SetOnResult( OnResultOptions on_result ){
      this.on_result = on_result;
   }//SetOnSelected
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnOk(){
      if( on_result != null ){
         on_result.OnResult( options );
      }//if
      this.dismiss();
   }//OnOk
   //---------------------------------------------------------------------------
   private void OnCancel(){
      this.dismiss();
   }//OnCancel
   //---------------------------------------------------------------------------

}//DialogPickOptionMult