package com.fantomsoftware.groupbudget.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.interfaces.OnResultDate;
import com.fantomsoftware.groupbudget.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class DialogPickDate extends DialogFragment{
//--------------------------------------------------------------------
private String            title;
private GregorianCalendar date;
private View              view_custom;

private String[] days;
private String[] months;
private String[] years;
private String[] hours;
private String[] minutes;

private NumberPicker np_day;
private NumberPicker np_month;
private NumberPicker np_year;
private NumberPicker np_hours;
private NumberPicker np_minutes;

private OnResultDate on_result;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
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
  view_custom = li.inflate( R.layout.dialog_pick_date, null );

  AlertDialog.Builder builder =
      new AlertDialog.Builder( getActivity(), R.style.MyAlertDialog );

  builder.setTitle( title );
  builder.setView( view_custom );
  builder.setPositiveButton( R.string.btn_ok, cl_ok );
  builder.setNegativeButton( R.string.btn_cancel, cl_cancel );

  Dialog dialog = builder.create();

  InitData();
  InitControls();

  return dialog;
}//onCreateDialog
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetOnResult( OnResultDate on_result ){
  this.on_result = on_result;
}//SetOnSelected
//--------------------------------------------------------------------
public void SetData( String title, long date_long ){
  this.title = title;

  if( date_long > 0 ){
    this.date = Utils.LongToDate( date_long );
  }else{
    date = new GregorianCalendar();
  }//if
}//SetData
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void InitData(){

  years = new String[100];
  int base = Calendar.getInstance().get( Calendar.YEAR ) - 50;
  for( int i = 0; i < 100; i++ ){
    years[i] = "" + base++;
  }//for

  SimpleDateFormat month_format = Utils.GetDateFormatter_WholeMonth();

  GregorianCalendar gc = new GregorianCalendar( 2000, 0, 0, 0, 0, 0 );

  String month;

  months = new String[12];
  for( int i = 0; i < 12; i++ ){
    gc.add( Calendar.MONTH, 1 );
    month = month_format.format( gc.getTime() );
    months[i] = month;
  }//for

  hours = new String[24];
  for( int i = 0; i < 24; i++ ){
    if( i < 10 ){
      hours[i] = "0" + i;
    }else{
      hours[i] = "" + i;
    }//if
  }//for

  minutes = new String[60];
  for( int i = 0; i < 60; i++ ){
    if( i < 10 ){
      minutes[i] = "0" + i;
    }else{
      minutes[i] = "" + i;
    }//if
  }//for
}//InitData
//--------------------------------------------------------------------
private void InitControls(){
  np_day = (NumberPicker) view_custom.findViewById( R.id.np_day );
  np_month = (NumberPicker) view_custom.findViewById( R.id.np_month );
  np_year = (NumberPicker) view_custom.findViewById( R.id.np_year );
  np_hours = (NumberPicker) view_custom.findViewById( R.id.np_hours );
  np_minutes = (NumberPicker) view_custom.findViewById( R.id.np_minutes );

  np_month.setMinValue( 0 );
  np_month.setMaxValue( months.length - 1 );
  np_month.setDisplayedValues( months );
  np_month.setValue( date.get( Calendar.MONTH ) );

  np_year.setMinValue( 0 );
  np_year.setMaxValue( years.length - 1 );
  np_year.setDisplayedValues( years );
  np_year.setValue( 50 );//middler of a years period - current year

  np_hours.setMinValue( 0 );
  np_hours.setMaxValue( hours.length - 1 );
  np_hours.setDisplayedValues( hours );
  np_hours.setValue( date.get( Calendar.HOUR_OF_DAY ) );

  np_minutes.setMinValue( 0 );
  np_minutes.setMaxValue( minutes.length - 1 );
  np_minutes.setDisplayedValues( minutes );
  np_minutes.setValue( date.get( Calendar.MINUTE ) );

  UpdateDatePicker();

  NumberPicker.OnValueChangeListener l_on_day_change =
      new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange( NumberPicker numberPicker, int i, int i2 ){
          date.set( Calendar.DAY_OF_MONTH, Integer.valueOf( days[i2] ) );
        }
      };

  NumberPicker.OnValueChangeListener l_on_month_change =
      new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange( NumberPicker numberPicker, int i, int i2 ){
          date.set( Calendar.MONTH, i2 );
          UpdateDatePicker();
        }
      };

  NumberPicker.OnValueChangeListener l_on_year_change =
      new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange( NumberPicker numberPicker, int i, int i2 ){
          date.set( Calendar.YEAR, Integer.valueOf( years[i2] ) );
          UpdateDatePicker();
        }
      };

  NumberPicker.OnValueChangeListener l_on_hour_change =
      new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange( NumberPicker numberPicker, int i, int i2 ){
          date.set( Calendar.HOUR_OF_DAY, Integer.valueOf( hours[i2] ) );
          UpdateDatePicker();
        }
      };

  NumberPicker.OnValueChangeListener l_on_munute_change =
      new NumberPicker.OnValueChangeListener(){
        @Override
        public void onValueChange( NumberPicker numberPicker, int i, int i2 ){
          date.set( Calendar.MINUTE, Integer.valueOf( minutes[i2] ) );
          UpdateDatePicker();
        }
      };

  np_day.setOnValueChangedListener( l_on_day_change );
  np_month.setOnValueChangedListener( l_on_month_change );
  np_year.setOnValueChangedListener( l_on_year_change );
  np_hours.setOnValueChangedListener( l_on_hour_change );
  np_minutes.setOnValueChangedListener( l_on_munute_change );
}//InitControls
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void UpdateDatePicker(){

  int days_count = date.getActualMaximum( Calendar.DATE );
  days = new String[days_count];

  for( int i = 0; i < days_count; i++ ){
    days[i] = "" + (i + 1);
  }

  int day = date.get( Calendar.DAY_OF_MONTH ) - 1;
  if( day >= days_count ){
    day = days_count - 1;
  }//if

  np_day.setMaxValue( 0 );
  np_day.setDisplayedValues( days );
  np_day.setMinValue( 0 );
  np_day.setMaxValue( days.length - 1 );
  np_day.setValue( day );
}//UpdateDatePicker
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnOk(){
  if( on_result != null ){
    on_result.OnResult( Utils.DateToLong( date ) );
  }//if
  this.dismiss();
}//OnOk
//--------------------------------------------------------------------
private void OnCancel(){
  this.dismiss();
}//OnCancel
//--------------------------------------------------------------------

}//DialogPickDate
