package com.fantomsoftware.groupbudget.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.interfaces.OnResultNumber;
import com.fantomsoftware.groupbudget.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class DialogCalcSum extends DialogFragment{
//--------------------------------------------------------------------
private String title;
private float  number;

private View     view_custom;
private TextView tv_res;
private TextView tv_eq;

private final int MODE_FN = 0;
private final int MODE_SN = 1;
private final int MODE_OP = 2;
private       int mode    = MODE_FN;

private boolean has_dot = false;

private String first_number  = "";
private String second_number = "";
private String operation     = "";

private OnResultNumber on_result;
private OnResultNumber on_tips;

private DecimalFormat number_format = null;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
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
  DialogInterface.OnClickListener cl_tips = new DialogInterface.OnClickListener(){
    @Override
    public void onClick( DialogInterface dialog, int id ){
      OnTips();
    }//onClick
  };//cl_tips

  LayoutInflater li = LayoutInflater.from( getContext() );
  view_custom = li.inflate( R.layout.dialog_calc_sum, null );

  AlertDialog.Builder builder =
      new AlertDialog.Builder( getActivity(), R.style.MyAlertDialog );

  builder.setTitle( title );
  builder.setView( view_custom );
  builder.setPositiveButton( R.string.btn_ok, cl_ok );
  builder.setNegativeButton( R.string.btn_cancel, cl_cancel );
  builder.setNeutralButton( R.string.btn_tips, cl_tips );

  Dialog dialog = builder.create();

  Init();

  return dialog;
}//onCreateDialog
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetOnResult( OnResultNumber on_result ){
  this.on_result = on_result;
}//SetOnSelected
//--------------------------------------------------------------------
public void SetOnTips( OnResultNumber on_tips ){
  this.on_tips = on_tips;
}//SetOnTips
//--------------------------------------------------------------------
public void SetData( String title, float number ){
  this.title = title;
  this.number = number;
}//SetData
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void Init(){
  number_format = Utils.GetCalcNumberFloatFormatter();

  tv_res = (TextView) view_custom.findViewById( R.id.tv_res );
  tv_eq = (TextView) view_custom.findViewById( R.id.tv_eq );

  first_number = number_format.format( number );
  first_number = first_number.replaceAll( ",", "." );

  tv_res.setText( first_number );

  //tv_res.setText( number_format.format( number ) );
  //first_number = "";//to let user type new number instead adding to the initial


  InitControls();
}//Init
//--------------------------------------------------------------------
private void InitControls(){
  InitBtnsKeyboard();
}//InitControls
//--------------------------------------------------------------------
private void InitBtnsKeyboard(){

  View.OnClickListener cl_keyboard = new View.OnClickListener(){
    @Override
    public void onClick( View view ){
      OnClick_KeyboardBtn( view );
    }//onClick
  };//cl_keyboard

  View.OnClickListener cl_backspace = new View.OnClickListener(){
    @Override
    public void onClick( View view ){
      OnClick_BackspaceBtn();
    }//onClick
  };//cl_backspace

  View.OnLongClickListener cll_backspace = new View.OnLongClickListener(){
    @Override
    public boolean onLongClick( View v ){
      OnLongClick_BackspaceBtn();
      return false;
    }//onLongClick
  };//cll_backspace

  //setting up touch and click events for key board
  LinearLayout ll_keyboard =
      (LinearLayout) view_custom.findViewById( R.id.ll_keyboard );


  ArrayList<View> children = GetChildren( ll_keyboard );
  for( View child : children ){
    if( child instanceof TextView ){
      child.setOnClickListener( cl_keyboard );
    }//if
  }//for

  //setting up touch down and up, click events for key board
  LinearLayout ll_backspace =
      (LinearLayout) view_custom.findViewById( R.id.ll_backspace );
  ll_backspace.setOnClickListener( cl_backspace );
  ll_backspace.setOnLongClickListener( cll_backspace );
}//InitBtnsKeyboard
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private boolean IsOperation( String value ){
  switch( value.charAt( 0 ) ){
    case '+':
    case '–':
    case '×':
    case '/':
    case '=':
      return true;
  }//switch

  return false;
}//IsOperation
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void PerformOperation(){
  float fn = Utils.StringToFloat( first_number );
  float sn = Utils.StringToFloat( second_number );

  switch( operation.charAt( 0 ) ){

    case '+':
      first_number = number_format.format( fn + sn );
      break;

    case '–':
      first_number = number_format.format( fn - sn );
      break;

    case '×':
      first_number = number_format.format( fn * sn );
      break;

    case '/':
      first_number = number_format.format( fn / sn );
      break;
  }//switch

  first_number = first_number.replaceAll( ",", "." );

}//PerformOperation
//--------------------------------------------------------------------
private void UpdateResult(){

  String result = first_number;

  if( !operation.equals( "" ) ){
    result += " " + operation;
  }//if

  if( !second_number.equals( "" ) ){
    result += " " + second_number;
  }//if

  tv_res.setText( result );
}//UpdateResult
//--------------------------------------------------------------------
private float GetResult(){

  OnClick_KeyboardBtn( tv_eq );

  String value = tv_res.getText().toString();

  if( value.length() == 0 ){
    return 0;
  }else{
    return Math.abs( Utils.StringToFloat( value ) );
  }//else

}//GetResult
//--------------------------------------------------------------------
private ArrayList<View> GetChildren( View view ){

  if( !(view instanceof ViewGroup) ){
    ArrayList<View> views = new ArrayList<>();
    views.add( view );
    return views;
  }//if

  ArrayList<View> result = new ArrayList<>();
  ViewGroup       vg     = (ViewGroup) view;

  for( int i = 0; i < vg.getChildCount(); i++ ){

    View child = vg.getChildAt( i );

    ArrayList<View> views = new ArrayList<View>();
    views.add( view );
    views.addAll( GetChildren( child ) );

    result.addAll( views );
  }//for

  return result;
}//GetChildren
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnClick_KeyboardBtn( View view ){

  TextView tv    = (TextView) view;
  String   value = tv.getText().toString();

  if( value.equals( "=" ) && (mode == MODE_FN || mode == MODE_OP) ){
    return;
  }//if

  if( value.equals( "." ) && has_dot ){
    return;
  }//if

  switch( mode ){

    case MODE_FN:
      int length_fts = first_number.length();

      if( IsOperation( value ) ){

        if( length_fts == 0 ){
          return;
        }

        char c = first_number.charAt( length_fts - 1 );
        if( c == '.' ){
          first_number += "0";
        }

        mode = MODE_OP;
        has_dot = false;
        operation = value;

      }else{//if not operation

        if( length_fts > 15 ){
          return;
        }//if long

        if( first_number.equals( "0.00" ) ){
          first_number = value;
          break;
        }//if


        if( value.equals( "." ) ){
          has_dot = true;
          if( length_fts == 0 ){
            first_number += "0";
          }//if
        }//if .

        if( length_fts == 1 ){
          char c = first_number.charAt( length_fts - 1 );
          if( c == '0' && value.equals( "0" ) ){
            has_dot = true;
            first_number = "0.0";
          }else{
            first_number += value;
          }
        }else{
          first_number += value;
        }//if
      }//if not operation

      break;

    case MODE_SN:

      int length_sec = second_number.length();

      if( IsOperation( value ) ){

        if( length_sec == 0 ){
          return;
        }

        char c = second_number.charAt( length_sec - 1 );
        if( c == '.' ){
          second_number += "0";
        }//if

        PerformOperation();
        second_number = "";
        if( value.equals( "=" ) ){
          operation = "";
          mode = MODE_FN;
          break;
        }else{
          operation = value;
        }//if

        mode = MODE_SN;

      }else{//if not operation

        if( length_sec > 15 ){
          return;
        }//if long


        if( value.equals( "." ) ){
          has_dot = true;
          if( length_sec == 0 ){
            second_number += "0";
          }//if
        }//if

        if( length_sec == 1 ){
          char c = second_number.charAt( length_sec - 1 );
          if( c == '0' && value.equals( "0" ) ){
            has_dot = true;
            second_number = "0.0";
          }else{
            second_number += value;
          }
        }else{
          second_number += value;
        }
      }//if not operation
      break;

    case MODE_OP:
      has_dot = false;
      if( IsOperation( value ) ){
        operation = value;
      }else if( value.equals( "." ) ){
        mode = MODE_SN;
        second_number += "0" + value;
      }else{
        mode = MODE_SN;
        second_number += value;
      }//else

      break;
  }//switch

  UpdateResult();
}//OnClick_KeyboardBtn
//--------------------------------------------------------------------
private void OnLongClick_BackspaceBtn(){
  mode = MODE_FN;
  first_number = "0";
  second_number = "";
  operation = "";
}
//--------------------------------------------------------------------
private void OnClick_BackspaceBtn(){

  int length = first_number.length();

  if( length == 0 ){
    mode = MODE_FN;
    second_number = "";
    operation = "";
  }//if

  if( length == 1 ){
    mode = MODE_FN;
    second_number = "";
    operation = "";
  }//if

  if( mode == MODE_OP && operation.equals( "" ) ){
    mode = MODE_FN;
  }//if

  char c;

  if( length > 0 ){
    switch( mode ){

      case MODE_FN:
        int length_fst = first_number.length();

        c = first_number.charAt( length_fst - 1 );

        switch( c ){
          case '.':
            has_dot = false;
            break;
        }

        first_number = first_number.substring( 0, length_fst - 1 );
        break;

      case MODE_SN:
        int length_sec = second_number.length();

        if( length_sec == 0 ){
          operation = "";
          mode = MODE_FN;
          break;
        }

        c = second_number.charAt( length_sec - 1 );

        switch( c ){
          case '.':
            has_dot = false;
            break;

          case '+':
          case '–':
          case '×':
          case '/':
            mode = MODE_FN;
            break;
        }

        second_number = second_number.substring( 0, length_sec - 1 );
        break;

      case MODE_OP:
        operation = "";
        mode = MODE_FN;
        break;
    }//switch
  }//if length > 0

  UpdateResult();
}//OnClick_BackspaceBtn
//--------------------------------------------------------------------
private void OnOk(){
  if( on_result != null ){
    on_result.OnResult( GetResult() );
  }//if
  this.dismiss();
}//OnOk
//--------------------------------------------------------------------
private void OnCancel(){
  this.dismiss();
}//OnCancel
//--------------------------------------------------------------------
private void OnTips(){
  if( on_tips != null ){
    on_tips.OnResult( GetResult() );
  }//if
  this.dismiss();
}//OnTips
//--------------------------------------------------------------------

}//DialogCalcSum
