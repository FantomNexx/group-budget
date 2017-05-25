package com.fantomsoftware.groupbudget.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.OperationBudget;


public class DBBudgetOp{
   //---------------------------------------------------------------------------
   private DBHelper db_helper;
   private String tablename = "operationsuser";
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   public DBBudgetOp( DBHelper db_helper ){
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
         + "id_op integer,"
         + "id_budget integer,"
         + "id_user integer,"
         + "id_curr integer,"

         + "type integer,"

         + "name text,"
         + "comment text,"
         + "icon_name text,"

         + "amount real,"
         + "date_created long,"
         + "latitude real,"
         + "longitude real"
         + ");";
      
      if( !db_helper.ExecuteSQL( query ) ){
         return;
      }//if

   }//CreateTable
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   public int Add( OperationBudget item ){
      
      ContentValues cv = new ContentValues();
      
      cv.put( "id_budget", item.id_budget );
      cv.put( "id_curr", item.id_curr );
      
      cv.put( "type", item.type );

      cv.put( "name", item.name );
      cv.put( "comment", item.comment );
      cv.put( "icon_name", item.icon_name );
      
      cv.put( "amount", item.amount );
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
   public int Update( OperationBudget item ){
      
      String strSQL =
         "UPDATE " + tablename + " SET"
            + " id_budget = " + item.id_budget
            + " , id_curr = " + item.id_curr

            + " , type = " + item.type

            + " , name = '" + item.name + "'"
            + " , comment = '" + item.comment + "'"
            + " , icon_name = '" + item.icon_name + "'"

            + " , amount = " + item.amount
            + " , date_created = " + item.date_created
            + " , latitude = " + item.latitude
            + " , longitude = " + item.longitude
            + " WHERE id = " + item.id;
      
      boolean is_result_ok = db_helper.ExecuteSQL( strSQL );
      
      if( !is_result_ok ){
         return -1;
      }
      
      return item.id;
   }//Update
   //---------------------------------------------------------------------------
   public SparseArray<OperationBudget> GetOperations( int id_budget ){
      
      String query = "SELECT * FROM " + tablename +
         " WHERE id_budget = " + id_budget +
         " ORDER BY date_created DESC";
      
      Cursor cursor = db_helper.ExecuteRawQuery( query );
      
      return ToSparseArray( cursor );
   }//Get
   //---------------------------------------------------------------------------
   public OperationBudget GetOperation( int id ){
      
      String strSQL = "SELECT * FROM " + tablename + " WHERE id = " + id;
      
      Cursor cursor = db_helper.ExecuteRawQuery( strSQL );
      
      SparseArray<OperationBudget> array = ToSparseArray( cursor );
      
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
   private SparseArray<OperationBudget> ToSparseArray( Cursor cursor ){
      
      SparseArray<OperationBudget> array = new SparseArray<>();
      
      if( cursor == null ){
         return array;
      }
      
      if( cursor.moveToFirst() ){
         int id = cursor.getColumnIndex( "id" );
         int id_budget = cursor.getColumnIndex( "id_budget" );
         int id_curr = cursor.getColumnIndex( "id_curr" );
         
         int type = cursor.getColumnIndex( "type" );

         int name = cursor.getColumnIndex( "name" );
         int comment = cursor.getColumnIndex( "comment" );
         int icon_name = cursor.getColumnIndex( "icon_name" );
         
         int amount = cursor.getColumnIndex( "amount" );
         int date_created = cursor.getColumnIndex( "date_created" );
         int latitude = cursor.getColumnIndex( "latitude" );
         int longitude = cursor.getColumnIndex( "longitude" );
         
         int index = 0;
         
         do{
            OperationBudget item = new OperationBudget();
            
            item.id = cursor.getInt( id );
            item.id_budget = cursor.getInt( id_budget );
            item.id_curr = cursor.getInt( id_curr );
            
            item.type = cursor.getInt( type );

            item.name = cursor.getString( name );
            item.comment = cursor.getString( comment );
            item.icon_name = cursor.getString( icon_name );
            
            item.amount = cursor.getFloat( amount );
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
}//DBBudgetOp
