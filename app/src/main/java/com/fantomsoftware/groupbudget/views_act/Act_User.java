package com.fantomsoftware.groupbudget.views_act;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.cmp.CmpInputTextBtn;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.User;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;


public class Act_User extends AppCompatActivity{
   //---------------------------------------------------------------------------
   private User            item     = null;
   private CmpInputTextBtn cmp_name = null;

   private int mode = ConstsAct.MODE_NEW;
   //---------------------------------------------------------------------------
   
   
   //---------------------------------------------------------------------------
   @Override
   protected void onCreate( Bundle savedInstanceState ){
      super.onCreate( savedInstanceState );
      setContentView( R.layout.act_user );

      if( !IsParamsOk() ){
         return;
      }//if
      
      InitToolbar();
      InitControls();
      
      UpdateValues();
   }//onCreate
   //---------------------------------------------------------------------------
   @Override
   public boolean onCreateOptionsMenu( Menu menu ){
      MenuInflater menuInflater = getMenuInflater();
      menuInflater.inflate( R.menu.menu_act_user, menu );
      return true;
   }//onCreateOptionsMenu
   //---------------------------------------------------------------------------
   @Override
   public boolean onPrepareOptionsMenu( Menu menu ){
      super.onPrepareOptionsMenu( menu );
      return true;
   }//onPrepareOptionsMenu
   //---------------------------------------------------------------------------
   @Override
   public boolean onOptionsItemSelected( MenuItem item ){
      switch( item.getItemId() ){
         case android.R.id.home:
            CloseActicity();
            break;

         case R.id.action_save:
            SaveAndExit();
            break;

         default:
            return super.onOptionsItemSelected( item );
      }//switch

      return true;
   }//onOptionsItemSelected
   //---------------------------------------------------------------------------
   @Override
   protected void onActivityResult( int request, int result, Intent intent ){

      if( result != RESULT_OK ){
         return;
      }//if

      switch( request ){
         case ConstsAct.REQUEST_PICK_CONTACT:
            UpdateName( intent );
            break;
      }//switch
   }//onActivityResult
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private boolean IsParamsOk(){

      Bundle extras = getIntent().getExtras();
      if( extras == null ){
         CloseActicity();
         return false;
      }//if

      if( !extras.containsKey( ConstsAct.KEY_MODE ) ){
         CloseActicity();
         return false;
      }//if

      mode = extras.getInt( ConstsAct.KEY_MODE );

      if( mode == ConstsAct.MODE_EDIT ){
         if( !extras.containsKey( ConstsAct.KEY_ID ) ){
            CloseActicity();
            return false;
         }//if

         int id = extras.getInt( ConstsAct.KEY_ID );

         item = Data.instance.db_user.users.get( id );

         if( item == null ){
            CloseActicity();
            return false;
         }//if

         return true;
      }//if( mode == MODE_EDIT )

      if( mode == ConstsAct.MODE_NEW ){

         item = new User();

         return true;
      }//if(mode == MODE_NEW )

      return false;
   }//IsParamsOk
   //---------------------------------------------------------------------------
   private boolean IsDataOk(){

      boolean is_data_ok = true;

      cmp_name.SetErrorOff();

      item.name = cmp_name.GetInputValue();

      if( item.name.equals( "" ) ){
         cmp_name.SetErrorOn( getString( R.string.error_field_cannot_be_empty ) );
         is_data_ok = false;
      }//name


      if( !is_data_ok ){
         return false;
      }//if

      return true;
   }//IsDataOk
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void InitToolbar(){

      if( item == null ){
         return;
      }

      String title = "";

      switch( mode ){
         case ConstsAct.MODE_NEW:
            title = getString( R.string.toolbar_title_user_add );
            break;

         case ConstsAct.MODE_EDIT:
            title = getString( R.string.toolbar_title_user_edit );
            break;
      }//switch

      Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
      toolbar.setTitle( title );
      setSupportActionBar( toolbar );

      android.support.v7.app.ActionBar ab = getSupportActionBar();
      if( ab != null ){
         ab.setDisplayHomeAsUpEnabled( true );
         ab.setHomeButtonEnabled( true );
      }//if actionbar != null
   }//Init
   //---------------------------------------------------------------------------
   private void InitControls(){

      OnEvent on_click_pick_user = new OnEvent(){
         @Override
         public void OnEvent(){
            OnClick_PickContact();
         }//OnEvent
      };//on_click_users

      cmp_name = (CmpInputTextBtn) findViewById( R.id.citb_name );
      cmp_name.SetOnBtnClick( on_click_pick_user );

   }//InitControls
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void UpdateValues(){
      cmp_name.SetInputValue( item.name );
   }//UpdateValues
   //---------------------------------------------------------------------------
   private void UpdateName( Intent data ){
      Uri contact_uri = data.getData();

      String key_display_name = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME;
      String key_photo_uri    = ContactsContract.CommonDataKinds.Phone.PHOTO_URI;

      String[] projection = { key_display_name, key_photo_uri };

      try{
         Cursor cursor = getContentResolver().query(
            contact_uri, projection, null, null, null );

         cursor.moveToFirst();

         int col_name = cursor.getColumnIndex( key_display_name );
         int col_photo = cursor.getColumnIndex( key_photo_uri );

         String name = cursor.getString( col_name );
         String photo = cursor.getString( col_photo );

         if( name == null ){
            item.name = "";
         }else{
            item.name = name;
         }//if

         if( photo == null ){
            item.photo_uri = "";
         }else{
            item.photo_uri = photo;
         }//if

         UpdateValues();

      }catch( Exception e ){
         //todo add Exception print
      }
   }//UpdateName
   //---------------------------------------------------------------------------
   private void UpdateContactImage( String s_photo_uri ){
      /*
      ImageView iv_user = (ImageView) findViewById( R.id.iv_user );
      iv_user.setImageDrawable( null );

      if( s_photo_uri == null ){
         iv_user.setBackground( context.getResources().getDrawable( R.drawable.icon96_user ) );
         ld.photo = "";
         return;
      }

      if( s_photo_uri.equals( "" ) == true ){
         iv_user.setBackground( context.getResources().getDrawable( R.drawable.icon96_user ) );
         ld.photo = "";
         return;
      }

      ld.photo = s_photo_uri;

      Uri uri = Uri.parse( s_photo_uri );
      UpdateContactImage( uri );
      */
   }//UpdateContactImage
   //---------------------------------------------------------------------------
   private void UpdateContactImage( Uri uri ){
      /*
      ImageView iv_user = (ImageView) findViewById( R.id.iv_user );

      int density = UtilsDensity.Get( getResources() );
      int side    = 40 * density;

      Bitmap bm      = GetBitmapByURI( uri );
      Bitmap resized = Bitmap.createScaledBitmap( bm, side, side, true );
      Bitmap conv_bm = getRoundedRectBitmap( resized, side );

      iv_user.setImageBitmap( conv_bm );
      */
   }//UpdateContactImage
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnClick_PickContact(){
      Intent intent = new Intent( Intent.ACTION_PICK, Uri.parse( "content://contacts" ) );
      intent.setType( ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE );
      startActivityForResult( intent, ConstsAct.REQUEST_PICK_CONTACT );
   }//OnClick_PickContact
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void CloseActicity(){
      Intent return_intent = new Intent();
      setResult( RESULT_CANCELED, return_intent );
      finish();
   }//CloseActicity
   //---------------------------------------------------------------------------
   private void SaveAndExit(){

      boolean is_data_ok = IsDataOk();
      if( !is_data_ok ){
         return;
      }//if

      int id_item;

      switch( mode ){
         case ConstsAct.MODE_NEW:
            id_item = Data.instance.db_user.Add( item );
            break;

         case ConstsAct.MODE_EDIT:
            id_item = Data.instance.db_user.Update( item );
            break;

         default:
            CloseActicity();
            return;
      }//switch


      if( id_item == -1 ){
         return;
      }//if

      item.id = id_item;

      Intent return_intent = new Intent();
      return_intent.putExtra( ConstsAct.KEY_ID, item.id );
      setResult( RESULT_OK, return_intent );
      finish();
   }//SaveAndExit
   //---------------------------------------------------------------------------

}//Act_User