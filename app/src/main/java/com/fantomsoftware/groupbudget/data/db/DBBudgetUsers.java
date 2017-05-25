package com.fantomsoftware.groupbudget.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.BudgetUser;

import java.util.List;


public class DBBudgetUsers{
   //---------------------------------------------------------------------------
   private DBHelper db_helper;
   private String tablename = "budgetusers";
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   public DBBudgetUsers( DBHelper db_helper ){
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
         + "id_user integer,"
         + "id_budget integer"
         + ");";
      
      if( !db_helper.ExecuteSQL( query )  ){
         return;
      }//if

   }//CreateTable
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   public int Add( BudgetUser item ){
      
      ContentValues cv = new ContentValues();
      
      cv.put( "id_user", item.id_user );
      cv.put( "id_budget", item.id_budget );
      
      int id = (int) db_helper.AddRow( tablename, cv );
      
      if( id < 1 ){
         return -1;
      }
      
      return id;
   }//Add
   //---------------------------------------------------------------------------
   public boolean AddMultiple( List<BudgetUser> items ){

      db_helper.Connect();

      String sql = "INSERT INTO " + tablename +
         " (id_user, id_budget)" +
         " VALUES (?, ?)";

      SQLiteDatabase db = db_helper.getDb();

      db.beginTransaction();

      SQLiteStatement stmt = db.compileStatement( sql );

      for( BudgetUser item : items ){
         stmt.bindLong( 1, item.id_user );
         stmt.bindLong( 2, item.id_budget );
         stmt.execute();
         stmt.clearBindings();
      }//for

      db.setTransactionSuccessful();
      db.endTransaction();

      db_helper.Close();

      return true;
   }//AddMultiple
   //---------------------------------------------------------------------------

   //---------------------------------------------------------------------------
   public int Update( BudgetUser item ){
      
      String strSQL =
         "UPDATE " + tablename
            + " SET id_user = " + item.id_user
            + " , id_budget = " + item.id_budget
            + " WHERE id = " + item.id;
      
      boolean is_result_ok = db_helper.ExecuteSQL( strSQL );
      
      if( !is_result_ok ){
         return -1;
      }
      
      return item.id;
   }//Update
   //---------------------------------------------------------------------------
   public SparseArray<BudgetUser> Get(){
      
      String query = "SELECT * FROM " + tablename;
      Cursor cursor = db_helper.ExecuteRawQuery( query );
      
      return ToSparseArray( cursor );
   }//Get
   // ---------------------------------------------------------------------------
   public SparseArray<BudgetUser> GetByBudget(int id_budget){

      String query = "SELECT * FROM " + tablename +
         " WHERE id_budget = " + id_budget;

      Cursor cursor = db_helper.ExecuteRawQuery( query );

      return ToSparseArray( cursor );
   }//Get
   //---------------------------------------------------------------------------
   /*
   public BudgetUser Get( int id ){
      
      String strSQL = "SELECT * FROM " + tablename +
         " WHERE id = " + id;
      
      Cursor cursor = db_helper.ExecuteRawQuery( strSQL );
      
      SparseArray<BudgetUser> array = ToSparseArray( cursor );
      
      if( array.size() == 0 ){
         return null;
      }
      
      return array.get( 0 );
   }//Get
   */
   //---------------------------------------------------------------------------
   public int RemoveByBudget( int id_budget ){
      return db_helper.DeleteRow( tablename, "id_budget", "" + id_budget );
   }//Remove
   // ---------------------------------------------------------------------------
   public int Remove( int id ){
      return db_helper.DeleteRow( tablename, "id", "" + id );
   }//Remove
   //---------------------------------------------------------------------------
   public void Clear(){
      db_helper.DeleteTable( tablename );
   }//Clear
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   private SparseArray<BudgetUser> ToSparseArray( Cursor cursor ){
      
      SparseArray<BudgetUser> array = new SparseArray<>();
      
      if( cursor == null ){
         return array;
      }
      
      if( cursor.moveToFirst() ){
         int id = cursor.getColumnIndex( "id" );
         int id_user = cursor.getColumnIndex( "id_user" );
         int id_budget = cursor.getColumnIndex( "id_budget" );
         
         int index = 0;
         
         do{
            BudgetUser item = new BudgetUser();
            
            item.id = cursor.getInt( id );
            item.id_user = cursor.getInt( id_user );
            item.id_budget = cursor.getInt( id_budget );
            
            array.put( index++, item );
            
         }while( cursor.moveToNext() );
      }//if( cursor.moveToFirst() )
      
      cursor.close();
      
      return array;
   }//ToSparseArray
   //---------------------------------------------------------------------------
}//DBBudgetUsers
