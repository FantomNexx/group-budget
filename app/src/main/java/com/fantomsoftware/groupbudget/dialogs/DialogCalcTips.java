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
import android.widget.SeekBar;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.interfaces.OnResultNumber;
import com.fantomsoftware.groupbudget.utils.Utils;

import java.text.DecimalFormat;


public class DialogCalcTips extends DialogFragment{
   //---------------------------------------------------------------------------
   private String title = "";

   private float sum   = 0;
   private float tip   = 0;
   private float total = 0;

   private float tip_percent     = 0;
   private float tip_percent_def = 10;

   private int round_to = 0;

   private View     view_custom;
   private EditText et_sum;
   private EditText et_tip;
   private EditText et_total;
   private TextView tv_percent_view;

   private OnResultNumber on_result;
   private OnResultNumber on_calc;

   private DecimalFormat number_format = null;

   public static final String sp_key_tip_percent = "sp_key_tip_percent";
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @NonNull
   @Override
   public Dialog onCreateDialog( Bundle savedInstanceState ){

      DialogInterface.OnClickListener cl_ok = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int id ){
            OnOk();
         }//cl_ok
      };//cl_ok
      DialogInterface.OnClickListener cl_cancel = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int id ){
            OnCancel();
         }//onClick
      };//cl_cancel
      DialogInterface.OnClickListener cl_calc = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int id ){
            OnCalc();
         }//onClick
      };//cl_calc

      LayoutInflater li = LayoutInflater.from( getContext() );
      view_custom = li.inflate( R.layout.dialog_calc_tips, null );

      AlertDialog.Builder builder =
         new AlertDialog.Builder( getActivity(), R.style.MyAlertDialog );

      builder.setTitle( title );
      builder.setView( view_custom );
      builder.setPositiveButton( R.string.btn_ok, cl_ok );
      builder.setNegativeButton( R.string.btn_cancel, cl_cancel );
      builder.setNeutralButton( R.string.btn_calc, cl_calc );

      Dialog dialog = builder.create();

      Init();

      return dialog;
   }//onCreateDialog
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetOnResult( OnResultNumber on_result ){
      this.on_result = on_result;
   }//SetOnSelected
   //---------------------------------------------------------------------------
   public void SetOnCalc( OnResultNumber on_calc ){
      this.on_calc = on_calc;
   }//SetOnTips
   //---------------------------------------------------------------------------
   public void SetData( String title, float sum ){
      this.title = title;
      this.sum = sum;
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Init(){
      number_format = Utils.GetCalcNumberFloatFormatter();

      et_sum = (EditText) view_custom.findViewById( R.id.et_sum );
      et_tip = (EditText) view_custom.findViewById( R.id.et_tip );
      et_total = (EditText) view_custom.findViewById( R.id.et_total );

      et_sum.setText( number_format.format( sum ) );

      InitTipPercent();
      InitRoundTo();
      UpdateResult();
   }//Init
   //---------------------------------------------------------------------------
   private void InitTipPercent(){
      tip_percent = Utils.GetSharedPreference( DialogCalcTips.sp_key_tip_percent );

      if( tip_percent == -1 ){
         tip_percent = tip_percent_def;
         Utils.SetSharedPreference( DialogCalcTips.sp_key_tip_percent, (int) tip_percent );
      }//if

      tv_percent_view = (TextView) view_custom.findViewById( R.id.tv_percent_view );

      SeekBar sb = (SeekBar) view_custom.findViewById( R.id.sb_percent );
      sb.setProgress( (int) tip_percent );

      SeekBar.OnSeekBarChangeListener on_change = new SeekBar.OnSeekBarChangeListener(){
         //---------------------------------------------------------------------
         @Override
         public void onProgressChanged( SeekBar seekBar, int i, boolean b ){
            tip_percent = i;
            tv_percent_view.setText( "" + tip_percent + "%" );
            round_to = 0;
            UpdateResult();
         }//onProgressChanged
         //---------------------------------------------------------------------
         @Override
         public void onStartTrackingTouch( SeekBar seekBar ){
         }//onStartTrackingTouch
         //---------------------------------------------------------------------
         @Override
         public void onStopTrackingTouch( SeekBar seekBar ){
         }//onStopTrackingTouch
         //---------------------------------------------------------------------
      };//on_change
      sb.setOnSeekBarChangeListener( on_change );
   }//InitTipPercent
   //---------------------------------------------------------------------------
   private void InitRoundTo(){

      View.OnClickListener cl_round = new View.OnClickListener(){
         @Override
         public void onClick( View view ){

            TextView tv   = (TextView) view;
            String   text = tv.getText().toString();

            switch( text ){
               case "-":
                  round_to = 0;
                  break;

               default:
                  round_to = Integer.valueOf( text );
                  break;
            }//switch

            UpdateResult();
         }//onClick
      };//cl_round


      TextView tv_to_    = (TextView) view_custom.findViewById( R.id.tv_to_ );
      TextView tv_to_1   = (TextView) view_custom.findViewById( R.id.tv_to_1 );
      TextView tv_to_5   = (TextView) view_custom.findViewById( R.id.tv_to_5 );
      TextView tv_to_10  = (TextView) view_custom.findViewById( R.id.tv_to_10 );
      TextView tv_to_20  = (TextView) view_custom.findViewById( R.id.tv_to_20 );
      TextView tv_to_50  = (TextView) view_custom.findViewById( R.id.tv_to_50 );
      TextView tv_to_100 = (TextView) view_custom.findViewById( R.id.tv_to_100 );

      tv_to_.setOnClickListener( cl_round );
      tv_to_1.setOnClickListener( cl_round );
      tv_to_5.setOnClickListener( cl_round );
      tv_to_10.setOnClickListener( cl_round );
      tv_to_20.setOnClickListener( cl_round );
      tv_to_50.setOnClickListener( cl_round );
      tv_to_100.setOnClickListener( cl_round );

   }//InitControls
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void UpdateResult(){
      tip = sum * ( tip_percent / 100.0f );

      if( round_to != 0 ){
         float total_tmp = sum + tip;
         total = (float) Math.ceil( total_tmp / (float) round_to ) * round_to;
      }else{
         total = sum + tip;
      }//else

      et_tip.setText( number_format.format( tip ) );
      et_total.setText( number_format.format( total ) );
   }//UpdateResult
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnOk(){

      Utils.SetSharedPreference( DialogCalcTips.sp_key_tip_percent, (int) tip_percent );

      if( on_result != null ){
         on_result.OnResult( total );
      }//if
      this.dismiss();
   }//OnOk
   //---------------------------------------------------------------------------
   private void OnCancel(){
      this.dismiss();
   }//OnCancel
   //---------------------------------------------------------------------------
   private void OnCalc(){

      Utils.SetSharedPreference( DialogCalcTips.sp_key_tip_percent, (int) tip_percent );

      if( on_calc != null ){
         on_calc.OnResult( total );
      }//if
      this.dismiss();
   }//OnCalc
   //---------------------------------------------------------------------------

}//DialogCalcTips
