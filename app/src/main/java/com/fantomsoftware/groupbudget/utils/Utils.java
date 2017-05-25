package com.fantomsoftware.groupbudget.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.views_act.Act;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Utils{
   //---------------------------------------------------------------------------
   public static AppCompatActivity activity         = null;
   public static Context           context          = null;
   public static Resources         resources        = null;
   public static FragmentManager   fragment_manager = null;

   public static String  package_name    = null;
   public static String  log_key         = null;
   public static boolean is_need_to_exit = false;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static void UpdateActivity( AppCompatActivity actitivty ){

      activity = actitivty;
      context = actitivty.getBaseContext();

      if( !Data.is_inited ){
         Utils.InitReferences();
         Data.instance.Init();
      }//if data is not inited

      Utils.SetAppTaskDescirptionStyle();

   }//UpdateActivity
   //---------------------------------------------------------------------------
   public static void InitReferences(){

      context = activity.getApplicationContext();
      fragment_manager = activity.getSupportFragmentManager();
      package_name = activity.getPackageName();
      resources = activity.getResources();
      log_key = package_name;

   }//InitReferences
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static void SnackS( View parent_view, String msg ){
      Snackbar.make( parent_view, msg, Snackbar.LENGTH_SHORT ).show();
   }
   //---------------------------------------------------------------------------
   public static void SnackL( View parent_view, String msg ){
      Snackbar.make( parent_view, msg, Snackbar.LENGTH_LONG ).show();
   }
   //---------------------------------------------------------------------------
   public static void ToastS( String msg ){
      Toast.makeText( context, msg, Toast.LENGTH_SHORT ).show();
   }
   //---------------------------------------------------------------------------
   public static void ToastL( String msg ){
      Toast.makeText( context, msg, Toast.LENGTH_LONG ).show();
   }
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static void SetSharedPreference( String key, int value ){
      String package_name = context.getPackageName();

      SharedPreferences sp = context.getSharedPreferences(
         package_name, Context.MODE_PRIVATE );

      sp.edit().putInt( key, value ).apply();
   }//SetSharedPreference
   // ---------------------------------------------------------------------------
   public static void SetSharedPreference( String key, boolean value ){
      String package_name = context.getPackageName();

      SharedPreferences sp = context.getSharedPreferences(
         package_name, Context.MODE_PRIVATE );

      sp.edit().putBoolean( key, value ).apply();
   }//SetSharedPreference
   //---------------------------------------------------------------------------
   public static int GetSharedPreference( String key ){
      SharedPreferences sp = context.getSharedPreferences(
         context.getPackageName(), Context.MODE_PRIVATE );

      return sp.getInt( key, -1 );
   }//SetSharedPreference
   //---------------------------------------------------------------------------
   public static boolean GetSharedPreferenceBoolean( String key ){
      SharedPreferences sp = context.getSharedPreferences(
         context.getPackageName(), Context.MODE_PRIVATE );

      return sp.getBoolean( key, false );
   }//SetSharedPreference
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static SimpleDateFormat GetDateFormatter_Whole(){
      SimpleDateFormat formater = new SimpleDateFormat( "dd MMMM yyyy HH:mm" );
      return formater;
   }//GetDateFormatter_Whole
   //---------------------------------------------------------------------------
   public static SimpleDateFormat GetDateFormatter(){
      SimpleDateFormat formater = new SimpleDateFormat( "dd MMMM yyyy" );
      return formater;
   }//GetDateFormatter_Whole
   //---------------------------------------------------------------------------
   public static SimpleDateFormat GetDateFormatter_WholeMonth(){
      SimpleDateFormat formater = new SimpleDateFormat( "MMMM" );
      return formater;
   }//GetDateFormatter_WholeMonth
   //---------------------------------------------------------------------------
   public static GregorianCalendar LongToDate( long date_long ){

      if( date_long == -1 ){
         return null;
      }

      GregorianCalendar date = new GregorianCalendar();
      date.setTimeInMillis( date_long );
      return date;
   }//LongToDate
   //---------------------------------------------------------------------------
   public static long DateToLong( GregorianCalendar date ){
      if( date == null ){
         return -1;
      }

      return date.getTimeInMillis();
   }//DateToLong
   //---------------------------------------------------------------------------
   public static String FormatLongDateToStringFull( long date_long ){
      SimpleDateFormat  formatter = Utils.GetDateFormatter_Whole();
      GregorianCalendar gc        = LongToDate( date_long );

      if( gc == null ){
         return "";
      }

      return formatter.format( gc.getTime() );
   }//FormatFloatToString
   //---------------------------------------------------------------------------
   public static String FormatLongDateToString( long date_long ){
      SimpleDateFormat  formatter = Utils.GetDateFormatter();
      GregorianCalendar gc        = LongToDate( date_long );

      if( gc == null ){
         return "";
      }

      return formatter.format( gc.getTime() );
   }//FormatFloatToString
   //---------------------------------------------------------------------------
   public static int GetDaysDiff( GregorianCalendar gc_from, GregorianCalendar gc_to ){

      gc_from.set( Calendar.HOUR_OF_DAY, 0 );
      gc_from.set( Calendar.MINUTE, 0 );
      gc_from.set( Calendar.HOUR_OF_DAY, 0 );

      gc_to.set( Calendar.HOUR_OF_DAY, 0 );
      gc_to.set( Calendar.MINUTE, 0 );
      gc_to.set( Calendar.HOUR_OF_DAY, 0 );

      //SimpleDateFormat sdf = new SimpleDateFormat( "dd MMM yyyy HH:mm" );
      //String date_before = sdf.format( gc_from.getTime() );
      //String date_after = sdf.format( gc_to.getTime() );

      long from = gc_from.getTimeInMillis();
      long to   = gc_to.getTimeInMillis();

      if( from > to ){
         return -1;
      }else if( from == to ){
         return 0;
      }

      int days_left = 0;

      while( from < to ){
         gc_from.add( Calendar.DAY_OF_MONTH, 1 );
         from = gc_from.getTimeInMillis();
         days_left++;
      }//while

      return days_left;
   }//GetDaysDiff
   //---------------------------------------------------------------------------
   public static int GetDaysDiffFromNow( long date_long_to ){
      GregorianCalendar gc_to  = Utils.LongToDate( date_long_to );
      GregorianCalendar gc_now = new GregorianCalendar();
      return GetDaysDiff( gc_now, gc_to );
   }//GetDaysDiffFromNow
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static int GetToolbarHeight( Context context ){

      final TypedArray styledAttributes = context.getTheme().
         obtainStyledAttributes( new int[]{ R.attr.actionBarSize } );

      int toolbarHeight = (int) styledAttributes.getDimension( 0, 0 );
      styledAttributes.recycle();

      return toolbarHeight;
   }//GetToolbarHeight
   //---------------------------------------------------------------------------
   public static Drawable GetDrawable( int id ){

      if( context == null ){
         return null;
      }//if

      if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){
         return resources.getDrawable( id, context.getTheme() );
      }else{
         return resources.getDrawable( id );
      }//else

   }//GetDrawable
   //---------------------------------------------------------------------------
   public static String GetResString( int id ){

      if( context == null ){
         return "";
      }//if

      return context.getString( id );

   }//GetResString
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static DecimalFormat GetNumberFormatter(){
      return new DecimalFormat( "0.00" );
   }//GetNumberFormatter
   //---------------------------------------------------------------------------
   public static DecimalFormat GetNumberFloatFormatter(){
      return new DecimalFormat( "##0.00" );
   }//GetNumberFormatter
   // ---------------------------------------------------------------------------
   public static DecimalFormat GetCalcNumberFloatFormatter(){

      DecimalFormatSymbols format_symbols = new DecimalFormatSymbols( Locale.ENGLISH );
      format_symbols.setDecimalSeparator( '.' );
      //format_symbols.setGroupingSeparator( '');

      return new DecimalFormat( "##0.00", format_symbols );
   }//GetNumberFormatter
   //---------------------------------------------------------------------------
   public static String FormatFloatToString( float value ){
      DecimalFormat formatter = GetCalcNumberFloatFormatter();
      return formatter.format( value );
   }//FormatFloatToString
   //---------------------------------------------------------------------------
   public static String FormatSumToString( float value ){
      return GetNumberFloatFormatter().format( value );
   }//FormatFloatToString
   //---------------------------------------------------------------------------
   public static Float StringToFloat( String s ){

      Float value = 0f;

      try{
         value = Float.valueOf( s );

      }catch( NumberFormatException ex ){

         DecimalFormat df = new DecimalFormat();

         Number n;

         try{
            n = df.parse( s );
         }catch( ParseException e ){
            n = 0;
         }//try

         if( n != null ){
            value = n.floatValue();
         }//if

      }//try parse

      return value;
   }//StringToFloat
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static String GenerateViewId(){

      if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ){
         return "" + View.generateViewId();
      }else{
         return "action_name_unique" + System.currentTimeMillis();
      }//else

   }//GenerateViewId
   //---------------------------------------------------------------------------
   public static void SetAppTaskDescirptionStyle(){

      if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ){

         Bitmap icon  = BitmapFactory.decodeResource( Utils.resources, R.mipmap.app_icon );
         String title = Utils.GetResString( R.string.app_name );
         int    color = Utils.resources.getColor( R.color.white );

         ActivityManager.TaskDescription task_description =
            new ActivityManager.TaskDescription( title, icon, color );

         Utils.activity.setTaskDescription( task_description );
      }//if

   }//SetAppTaskDescirptionStyle
   //---------------------------------------------------------------------------
   public static void SetAppShortcutIcon(){

      if( context == null ){
         return;
      }//if

      SharedPreferences appPreferences =
         PreferenceManager.getDefaultSharedPreferences( context );

      boolean is_app_installed = appPreferences.getBoolean( "isAppInstalled", false );

      if( is_app_installed ){
         return;
      }//if installed


      Intent shortcut_intent = new Intent( context, Act.class );
      shortcut_intent.setAction( Intent.ACTION_MAIN );

      Intent.ShortcutIconResource app_shortcut =
         Intent.ShortcutIconResource.fromContext( context, R.mipmap.app_icon );

      Intent intent = new Intent();
      intent.setAction( "com.android.launcher.action.INSTALL_SHORTCUT" );
      intent.putExtra( Intent.EXTRA_SHORTCUT_INTENT, shortcut_intent );
      intent.putExtra( Intent.EXTRA_SHORTCUT_NAME, R.string.app_name );
      intent.putExtra( Intent.EXTRA_SHORTCUT_ICON_RESOURCE, app_shortcut );


      context.sendBroadcast( intent );

      SharedPreferences.Editor editor = appPreferences.edit();
      editor.putBoolean( "isAppInstalled", true );
      editor.apply();

   }//SetAppShortcutIcon
   //---------------------------------------------------------------------------
   public static void TerminateApplication(){
      activity.finish();
      android.os.Process.killProcess( android.os.Process.myPid() );
   }//TerminateApplication
   //---------------------------------------------------------------------------

}//Utils
