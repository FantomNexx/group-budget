package com.fantomsoftware.groupbudget.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.OperationUser;


public class DBUserOp{
   //---------------------------------------------------------------------------
   private DBHelper db_helper;
   private String tablename = "operationusers";
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   public DBUserOp( DBHelper db_helper ){
      this.db_helper = db_helper;
      //Clear();
      Init();
   }//LoanDebtDB
   //---------------------------------------------------------------------------
   private boolean Init(){
      if( !db_helper.IsTableExists( tablename ) ){
         CreateTable();
      }
      return true;
   }//Init
   //---------------------------------------------------------------------------
   private void CreateTable(){

      String query = "create table " + tablename + " ("
         + "id integer primary key autoincrement,"
         + "type integer,"
         + "name text,"
         + "comment text,"
         + "id_budget integer,"
         + "icon_name text,"
         + "id_op integer,"
         + "id_user integer,"
         + "id_curr integer,"
         + "amount_pers real,"
         + "amount_shared real,"
         + "date_created long,"
         + "latitude real,"
         + "longitude real"
         + ");";
      
      if( !db_helper.ExecuteSQL( query )  ){
         return;
      }//if

   }//CreateTable
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   public int Add( OperationUser item ){
      
      ContentValues cv = new ContentValues();

      cv.put( "type", item.type );
      cv.put( "name", item.name );
      cv.put( "comment", item.comment );
      cv.put( "id_budget", item.id_budget );
      cv.put( "id_icon", item.icon_name );
      cv.put( "id_op", item.id_op );
      cv.put( "id_user", item.id_user );
      cv.put( "id_curr", item.id_curr );
      cv.put( "amount_pers", item.amount_pers );
      cv.put( "amount_shared", item.amount_shared );
      cv.put( "date_created", item.date_created );
      cv.put( "latitude", item.latitude );
      cv.put( "longitude", item.longitude );

      int id = (int) db_helper.AddRow( tablename, cv );
      
      if( id < 1 ){
         return -1;
      }
      
      return id;
   }//Add
   //---------------------------------------------------------------------------
   public boolean AddMultiple( SparseArray<OperationUser> items ){

      db_helper.Connect();

      String sql = "INSERT INTO " + tablename +
         " (" +
         "type, " +
         "name , " +
         "comment , " +
         "id_budget , " +
         "icon_name , " +
         "id_op , " +
         "id_user , " +
         "id_curr , " +
         "amount_pers , " +
         "amount_shared , " +
         "date_created, " +
         "latitude , " +
         "longitude" +
         ")" +
         " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";

      SQLiteDatabase db = db_helper.getDb();

      db.beginTransaction();

      SQLiteStatement stmt = db.compileStatement( sql );

      OperationUser item;

      for( int i = 0; i < items.size(); i++ ){

         item = items.valueAt( i );

         stmt.bindLong( 1, item.type );
         stmt.bindString( 2, item.name );
         stmt.bindString( 3, item.comment );
         stmt.bindLong( 4, item.id_budget );
         stmt.bindString( 5, item.icon_name );
         stmt.bindLong( 6, item.id_op );
         stmt.bindLong( 7, item.id_user );
         stmt.bindLong( 8, item.id_curr );
         stmt.bindDouble( 9, item.amount_pers );
         stmt.bindDouble( 10, item.amount_shared );
         stmt.bindLong( 11, item.date_created );
         stmt.bindDouble( 12, item.latitude );
         stmt.bindDouble( 13, item.longitude );
         stmt.execute();
         stmt.clearBindings();
      }//for

      db.setTransactionSuccessful();
      db.endTransaction();

      db_helper.Close();

      return true;
   }//AddMultiple
   //---------------------------------------------------------------------------
   public int Update( OperationUser item ){

      /*
      String strSQL =
         "UPDATE " + tablename
            + " , id_budget = " + item.id_budget
            + " , id_user = " + item.id_user
            + " WHERE id = " + item.id;

      boolean is_result_ok = db_helper.ExecuteSQL( strSQL );

      if( !is_result_ok ){
         return -1;
      }
      */

      return -1;
   }//Update
   //---------------------------------------------------------------------------
   public SparseArray<OperationUser> Get( int id_budget, int id_user ){
      
      String query = "SELECT * FROM " + tablename +
         " WHERE id_budget = " + id_budget +
         " AND id_user = " + id_user +
         " ORDER BY date_created DESC";
      
      Cursor cursor = db_helper.ExecuteRawQuery( query );
      
      return ToSparseArray( cursor );
   }//Get
   //---------------------------------------------------------------------------
   public SparseArray<OperationUser> Get_IdexOperation( int id_budget, int id_user ){

      String query = "SELECT * FROM " + tablename +
         " WHERE id_budget = " + id_budget +
         " AND id_user = " + id_user +
         " ORDER BY date_created DESC";

      Cursor cursor = db_helper.ExecuteRawQuery( query );

      return ToSparseArray_IndexOp( cursor );
   }//Get
   //---------------------------------------------------------------------------
   public SparseArray<OperationUser> GetOperations( int id_operation ){
      
      String strSQL = "SELECT * FROM " + tablename +
         " WHERE id_op = " + id_operation +
         " ORDER BY date_created DESC";
      
      Cursor cursor = db_helper.ExecuteRawQuery( strSQL );
      
      SparseArray<OperationUser> array = ToSparseArray( cursor );
      
      if( array.size() == 0 ){
         return null;
      }
      
      return array;
   }//Get
   // ---------------------------------------------------------------------------
   public OperationUser Get( int id ){

      String strSQL = "SELECT * FROM " + tablename + " WHERE id = " + id;

      Cursor cursor = db_helper.ExecuteRawQuery( strSQL );

      SparseArray<OperationUser> array = ToSparseArray( cursor );

      if( array.size() == 0 ){
         return null;
      }

      return array.get( 0 );
   }//Get
   //---------------------------------------------------------------------------
   public int Remove( int id ){
      return db_helper.DeleteRow( tablename, "id", id + "" );
   }//Remove
   //---------------------------------------------------------------------------
   public int RemoveByBudgetOperation( int id_budget, int id_operation ){

      db_helper.Connect();

      SQLiteDatabase db = db_helper.getDb();
      int result = db.delete( tablename, "id_budget =" + id_budget +
         " AND " + "id_op =" + id_operation, null );

      db_helper.Close();

      return result;
   }//Remove
   //---------------------------------------------------------------------------
   public void Clear(){
      db_helper.DeleteTable( tablename );
   }//Clear
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   private SparseArray<OperationUser> ToSparseArray( Cursor cursor ){
      
      SparseArray<OperationUser> array = new SparseArray<>();
      
      if( cursor == null ){
         return array;
      }
      
      if( cursor.moveToFirst() ){
         int id = cursor.getColumnIndex( "id" );
         int id_op = cursor.getColumnIndex( "id_op" );
         int id_budget = cursor.getColumnIndex( "id_budget" );
         int id_user = cursor.getColumnIndex( "id_user" );
         int id_curr = cursor.getColumnIndex( "id_curr" );
         
         int type = cursor.getColumnIndex( "type" );

         int name = cursor.getColumnIndex( "name" );
         int comment = cursor.getColumnIndex( "comment" );
         int icon_name = cursor.getColumnIndex( "icon_name" );
         
         int amount_pers = cursor.getColumnIndex( "amount_pers" );
         int amount_shared = cursor.getColumnIndex( "amount_shared" );

         int date_created = cursor.getColumnIndex( "date_created" );
         int latitude = cursor.getColumnIndex( "latitude" );
         int longitude = cursor.getColumnIndex( "longitude" );
         
         int index = 0;
         
         do{
            OperationUser item = new OperationUser();
            
            item.id = cursor.getInt( id );
            item.id_op = cursor.getInt( id_op );
            item.id_budget = cursor.getInt( id_budget );
            item.id_user = cursor.getInt( id_user );
            item.id_curr = cursor.getInt( id_curr );
            
            item.type = cursor.getInt( type );
            
            item.name = cursor.getString( name );
            item.comment = cursor.getString( comment );
            item.icon_name = cursor.getString( icon_name );
            
            item.amount_pers = cursor.getFloat( amount_pers );
            item.amount_shared = cursor.getFloat( amount_shared );
            item.date_created = cursor.getLong( date_created );
            item.latitude = cursor.getFloat( latitude );
            item.longitude = cursor.getFloat( longitude );
            
            array.put( index++, item );
            
         }while( cursor.moveToNext() );
      }//if( cursor.moveToFirst() )
      
      cursor.close();
      
      return array;
   }//ToSparseArray
   //---------------------------------------------------------------------------
   private SparseArray<OperationUser> ToSparseArray_IndexOp( Cursor cursor ){

      SparseArray<OperationUser> array = new SparseArray<>();

      if( cursor == null ){
         return array;
      }

      if( cursor.moveToFirst() ){
         int id = cursor.getColumnIndex( "id" );
         int id_op = cursor.getColumnIndex( "id_op" );
         int id_budget = cursor.getColumnIndex( "id_budget" );
         int id_user = cursor.getColumnIndex( "id_user" );
         int id_curr = cursor.getColumnIndex( "id_curr" );

         int type = cursor.getColumnIndex( "type" );

         int name = cursor.getColumnIndex( "name" );
         int comment = cursor.getColumnIndex( "comment" );
         int icon_name = cursor.getColumnIndex( "icon_name" );

         int amount_pers = cursor.getColumnIndex( "amount_pers" );
         int amount_shared = cursor.getColumnIndex( "amount_shared" );

         int date_created = cursor.getColumnIndex( "date_created" );
         int latitude = cursor.getColumnIndex( "latitude" );
         int longitude = cursor.getColumnIndex( "longitude" );

         do{
            OperationUser item = new OperationUser();

            item.id = cursor.getInt( id );
            item.id_op = cursor.getInt( id_op );
            item.id_budget = cursor.getInt( id_budget );
            item.id_user = cursor.getInt( id_user );
            item.id_curr = cursor.getInt( id_curr );

            item.type = cursor.getInt( type );

            item.name = cursor.getString( name );
            item.comment = cursor.getString( comment );
            item.icon_name = cursor.getString( icon_name );

            item.amount_pers = cursor.getFloat( amount_pers );
            item.amount_shared = cursor.getFloat( amount_shared );
            item.date_created = cursor.getLong( date_created );
            item.latitude = cursor.getFloat( latitude );
            item.longitude = cursor.getFloat( longitude );

            array.put( item.id_op, item );

         }while( cursor.moveToNext() );
      }//if( cursor.moveToFirst() )

      cursor.close();

      return array;
   }//ToSparseArray
   //---------------------------------------------------------------------------
   
}//DBUserOp
