package com.fantomsoftware.groupbudget.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.fantomsoftware.groupbudget.utils.Storage;
import com.fantomsoftware.groupbudget.utils.Utils;



public class DBHelper{
   //---------------------------------------------------------------------------
   private SQLiteDatabase db = null;
   public SQLiteDatabase getDb(){
      return db;
   }//getDb
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public DBHelper(){

      if( !Storage.IsFileExists( Storage.GetPath_Database() ) ){
         Create();
      }//if

      Open();
   }//DBHelper
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Create(){
      new Helper( Utils.context );
   }//Create
   //---------------------------------------------------------------------------
   private void Open(){
      try{
         db = SQLiteDatabase.openDatabase(
            Storage.GetPath_Database(),
            null,
            SQLiteDatabase.NO_LOCALIZED_COLLATORS );
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
      }
   }//Open
   //---------------------------------------------------------------------------
   public void Close(){
      if( db == null ){
         return;
      }

      try{
         db.close();
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
      }

   }//Close
   //---------------------------------------------------------------------------
   public void Connect(){
      if( db == null || !db.isOpen() ){
         Open();
      }
   }//Connect
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public boolean ExecuteSQL( String sql ){
      Connect();
      try{
         db.execSQL( sql );
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return false;
      }
      db.close();
      return true;
   }//ExecuteSQL
   //---------------------------------------------------------------------------
   public Cursor ExecuteRawQuery( String sql ){
      Connect();
      Cursor cursor;
      try{
         cursor = db.rawQuery( sql, null );
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return null;
      }
      return cursor;
   }//ExecuteRawQuery
   //---------------------------------------------------------------------------
   public Cursor GetWholeTable( String tablename ){
      Connect();
      Cursor cursor;
      try{
         cursor = db.query( tablename, null, null, null, null, null, null );
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return null;
      }
      return cursor;
   }//Connect
   //---------------------------------------------------------------------------
   public long AddRow( String tablename, ContentValues cv ){
      Connect();
      long result;
      try{
         result = db.insert( tablename, null, cv );
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return -1;
      }
      db.close();
      return result;
   }//AddRow
   //---------------------------------------------------------------------------
   public boolean DeleteTable( String tablename ){
      Connect();
      try{
         db.execSQL( "DROP TABLE IF EXISTS " + tablename );
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return false;
      }
      Close();
      return true;
   }//DeleteTable
   //---------------------------------------------------------------------------
   public int DeleteRow( String tablename, String key, String param ){
      Connect();
      int result;
      try{
         result = db.delete( tablename, key + " = '" + param + "'", null );
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return -1;
      }
      db.close();
      return result;
   }//DeleteRow
   //---------------------------------------------------------------------------
   public int UpdateRow( String tablename, ContentValues args, String key, String param ){
      Connect();
      int result;
      try{
         result = db.update( tablename, args, key + "=" + param, null );
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return -1;
      }
      db.close();
      return result;
   }//UpdateRow
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public boolean IsTableExists( String tablename ){
      Connect();

      String query = "select DISTINCT tbl_name from " +
                     "sqlite_master where tbl_name = '" + tablename + "'";

      Cursor cursor;

      boolean result = false;

      try{
         cursor = ExecuteRawQuery( query );

         if( cursor != null && cursor.getCount() > 0 ){
            result = true;
         }
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return false;
      }

      if( cursor != null ){
         cursor.close();
      }

      Close();
      return result;
   }//IsTableExists
   //---------------------------------------------------------------------------
   public boolean IsTableEmpty( String tablename ){
      Connect();
      boolean result = true;

      Cursor cursor = GetWholeTable( tablename );

      if( cursor.moveToFirst() ){
         result = false;
      }

      if( cursor != null ){
         cursor.close();
      }
      Close();

      return result;
   }//IsTableEmpty
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private class Helper
      extends SQLiteOpenHelper{
      //------------------------------------------------------------------------
      @Override
      public void onCreate( SQLiteDatabase db ){
      }//onCreate
      //------------------------------------------------------------------------
      public Helper( Context context ){
         super( context, Storage.GetPath_Database(), null, 1 );
         SQLiteDatabase.openOrCreateDatabase( Storage.GetPath_Database(), null );
      }//Helper
      //------------------------------------------------------------------------
      @Override
      public void onUpgrade( SQLiteDatabase db, int v_old, int v_new ){
      }//onUpgrade
   }//class Helper
   //---------------------------------------------------------------------------

}//DBHelper
