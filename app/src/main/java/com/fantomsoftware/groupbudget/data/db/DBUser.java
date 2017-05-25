package com.fantomsoftware.groupbudget.data.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.User;
import com.fantomsoftware.groupbudget.utils.Utils;


public class DBUser{
   //---------------------------------------------------------------------------
   private DBHelper db_helper;
   private String tablename = "users";

   public SparseArray<User> users = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public DBUser( DBHelper db_helper ){
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
         + "photo_path text,"
         + "photo_uri text"
         + ");";

      if( !db_helper.ExecuteSQL( query )  ){
         return;
      }//if

   }//CreateTable
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public int Add( User item ){

      ContentValues cv = new ContentValues();

      cv.put( "name", item.name );
      cv.put( "photo_path", item.photo_path );
      cv.put( "photo_uri", item.photo_uri );

      int id = (int) db_helper.AddRow( tablename, cv );

      if( id < 1 ){
         return -1;
      }

      return id;
   }//Add
   //---------------------------------------------------------------------------
   public int Update( User item ){

      String strSQL =
         "UPDATE " + tablename
            + " SET name = '" + item.name + "'"
            + " , photo_path = '" + item.photo_path + "'"
            + " , photo_uri = '" + item.photo_uri + "'"
            + " WHERE id = " + item.id;

      boolean is_result_ok = db_helper.ExecuteSQL( strSQL );

      if( !is_result_ok ){
         return -1;
      }

      return item.id;
   }//Update
   //---------------------------------------------------------------------------
   public SparseArray<User> Get(){

      String query = "SELECT * FROM " + tablename +
         " ORDER BY name DESC";

      Cursor cursor = db_helper.ExecuteRawQuery( query );

      return ToSparseArray( cursor );
   }//Get
   //---------------------------------------------------------------------------
   public User Get( int id ){

      String strSQL = "SELECT * FROM " + tablename + " WHERE id = " + id;

      Cursor cursor = db_helper.ExecuteRawQuery( strSQL );

      SparseArray<User> array = ToSparseArray( cursor );

      if( array.size() == 0 ){
         return null;
      }

      return array.valueAt( 0 );
   }//Get
   //---------------------------------------------------------------------------
   public String GetName( int id_user ){

      User user = users.get( id_user );
      if( user != null ){
         return user.name;
      }//if user exists

      return "["+id_user+"] " + Utils.GetResString( R.string.user_deleted );
   }//GetName
   //---------------------------------------------------------------------------
   public void UpdateLocal(){
      users = Get();
   }//UpdateLocal
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
   private SparseArray<User> ToSparseArray( Cursor cursor ){

      SparseArray<User> array = new SparseArray<>();

      if( cursor == null ){
         return array;
      }

      if( cursor.moveToFirst() ){
         int id = cursor.getColumnIndex( "id" );
         int name = cursor.getColumnIndex( "name" );
         int photo_path = cursor.getColumnIndex( "photo_path" );
         int photo_uri = cursor.getColumnIndex( "photo_uri" );

         do{
            User item = new User();

            item.id = cursor.getInt( id );
            item.name = cursor.getString( name );
            item.photo_path = cursor.getString( photo_path );
            item.photo_uri = cursor.getString( photo_uri );

            array.put( item.id, item );

         }while( cursor.moveToNext() );
      }//if( cursor.moveToFirst() )

      cursor.close();

      return array;
   }//ToSparseArray
   //---------------------------------------------------------------------------

}//DBUser
