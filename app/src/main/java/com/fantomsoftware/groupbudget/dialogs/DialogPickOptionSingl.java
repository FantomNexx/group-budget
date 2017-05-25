package com.fantomsoftware.groupbudget.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.interfaces.OnResultPosition;


public class DialogPickOptionSingl extends DialogFragment{
   //---------------------------------------------------------------------------
   private String  title;
   private Options options;

   private OnResultPosition on_result;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @NonNull
   @Override
   public Dialog onCreateDialog( Bundle savedInstanceState ){

      DialogInterface.OnClickListener cl_selection = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int which ){
            OnSelect( which );
         }
      };//cl_selection

      CharSequence[] list              = options.GetListStrings();
      int            selected_position = options.GetSelectedPosition();

      AlertDialog.Builder builder =
         new AlertDialog.Builder( getActivity(), R.style.MyAlertDialog );

      builder.setTitle( title );

      if( selected_position == -1 ){
         builder.setItems( list, cl_selection );
      }else{
         builder.setSingleChoiceItems( list, selected_position, cl_selection );
      }//if

      return builder.create();
   }//onCreateDialog
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetData( String title, Options options ){
      this.title = title;
      this.options = options;
   }//SetData
   //---------------------------------------------------------------------------
   public void SetOnResult( OnResultPosition on_result ){
      this.on_result = on_result;
   }//SetOnSelected
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnSelect( int position ){
      if( on_result != null ){
         on_result.OnResult( position );
      }//if
      this.dismiss();
   }//OnSelected
   //---------------------------------------------------------------------------

}//DialogPickOptionSingl