package com.fantomsoftware.groupbudget.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.Budget;


public class DBBudget{
   //---------------------------------------------------------------------------
   private DBHelper db_helper;
   private String tablename = "budgets";
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public DBBudget( DBHelper db_helper ){
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
         + "name text,"
         + "comment text,"
         + "id_curr_def integer,"
         + "date_created long"
         + ");";
      
      if( !db_helper.ExecuteSQL( query )  ){
         return;
      }//if

   }//CreateTable
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   public int Add( Budget item ){
      
      ContentValues cv = new ContentValues();
      
      cv.put( "name", item.name );
      cv.put( "comment", item.comment );
      cv.put( "id_curr_def", item.id_curr_def );
      cv.put( "date_created", item.date_created );

      int id = (int) db_helper.AddRow( tablename, cv );
      
      if( id < 1 ){
         return -1;
      }
      
      return id;
   }//Add
   //---------------------------------------------------------------------------
   public int Update( Budget item ){
      
      String strSQL =
         "UPDATE " + tablename
            + " SET name = '" + item.name + "'"
            + " , comment = '" + item.comment + "'"
            + " , id_curr_def = " + item.id_curr_def
            + " , date_created = " + item.date_created
            + " WHERE id = " + item.id;
      
      boolean is_result_ok = db_helper.ExecuteSQL( strSQL );
      
      if( !is_result_ok ){
         return -1;
      }
      
      return item.id;
   }//Update
   //---------------------------------------------------------------------------
   public SparseArray<Budget> Get(){
      
      String query = "SELECT * FROM " + tablename +
         " ORDER BY date_created DESC";
      
      Cursor cursor = db_helper.ExecuteRawQuery( query );
      
      return ToSparseArray( cursor );
   }//Get
   //---------------------------------------------------------------------------
   public Budget Get( int id ){
      
      String strSQL = "SELECT * FROM " + tablename + " WHERE id = " + id;
      
      Cursor cursor = db_helper.ExecuteRawQuery( strSQL );
      
      SparseArray<Budget> array = ToSparseArray( cursor );
      
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
   public void Clear(){
      db_helper.DeleteTable( tablename );
   }//Clear
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   private SparseArray<Budget> ToSparseArray( Cursor cursor ){
      
      SparseArray<Budget> array = new SparseArray<>();
      
      if( cursor == null ){
         return array;
      }
      
      if( cursor.moveToFirst() ){
         int id = cursor.getColumnIndex( "id" );
         int name = cursor.getColumnIndex( "name" );
         int comment = cursor.getColumnIndex( "comment" );
         int id_curr_def = cursor.getColumnIndex( "id_curr_def" );
         int date_created = cursor.getColumnIndex( "date_created" );

         int index = 0;
         
         do{
            Budget item = new Budget();
            
            item.id = cursor.getInt( id );
            item.name = cursor.getString( name );
            item.comment = cursor.getString( comment );
            item.id_curr_def = cursor.getInt( id_curr_def );
            item.date_created = cursor.getLong( date_created );

            array.put( index++, item );
            
         }while( cursor.moveToNext() );
      }//if( cursor.moveToFirst() )
      
      cursor.close();
      
      return array;
   }//ToSparseArray
   //---------------------------------------------------------------------------
}//DBBudget
