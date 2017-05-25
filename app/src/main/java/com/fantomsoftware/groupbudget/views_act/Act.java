package com.fantomsoftware.groupbudget.views_act;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.fantomsoftware.groupbudget.views_frag.Frag_ListBudgets;
import com.fantomsoftware.groupbudget.views_frag.Frag_ListCurrencies;
import com.fantomsoftware.groupbudget.views_frag.Frag_ListUsers;

public class Act extends AppCompatActivity{
   //---------------------------------------------------------------------------
   private Toolbar        toolbar;
   private DrawerLayout   drawer_layout;
   private NavigationView navigation_view;
   //private CmpDrawerAuth  cmp_auth;

   public static final String sp_key_current_fragment_id = "sp_key_current_fragment_id";
   private             int    current_fragment_id        = -1;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected void onCreate( Bundle savedInstanceState ){
      super.onCreate( savedInstanceState );
      setContentView( R.layout.act );
      Utils.UpdateActivity( this );
      Init();
   }//onCreate
   //---------------------------------------------------------------------------
   @Override
   protected void onResume(){
      super.onResume();
      Utils.UpdateActivity( this );
   }//onResume
   //---------------------------------------------------------------------------
   @Override
   protected void onDestroy(){
      super.onDestroy();
      CancelAllNotification();
   }//onDestroy
   //---------------------------------------------------------------------------
   @Override
   protected void onStop(){
      super.onStop();

      /*
      if( cmp_auth != null ){
         cmp_auth.SignOut();
      }//if
      */
   }//onStop
   //---------------------------------------------------------------------------
   @Override
   public void onActivityResult( int request, int result, Intent data ){
      super.onActivityResult( request, result, data );

      /*
      //USER FOR GOOGLE AUTHORIZATION
      if( request == ConstsAct.REQUEST_SIGN_IN ){
         if( cmp_auth != null ){
            cmp_auth.OnRequestResult( request, result, data );
         }//if
      }//ConstsAct.REQUEST_SIGN_IN
      */

   }//onActivityResult
   //---------------------------------------------------------------------------
   @Override
   public void onBackPressed(){
      super.onBackPressed();

      if( Utils.is_need_to_exit ){
         Utils.TerminateApplication();
         return;
      }//if

      this.finish();

   }//onBackPressed
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Init(){

      navigation_view = (NavigationView) findViewById( R.id.navigation_view );

      InitToolbar();
      InitNavigationView();
      InitDrawer();
      //InitAuthorization();
      InitFragment();
   }//Init
   //---------------------------------------------------------------------------
   private void InitToolbar(){
      toolbar = (Toolbar) findViewById( R.id.toolbar );
      toolbar.setTitle( R.string.app_name );
      setSupportActionBar( toolbar );
   }//InitToolbar
   //---------------------------------------------------------------------------
   private void InitNavigationView(){

      NavigationView.OnNavigationItemSelectedListener on_item_selected =
         new NavigationView.OnNavigationItemSelectedListener(){

            @Override
            public boolean onNavigationItemSelected( MenuItem menu_item ){
               menu_item.setChecked( true );
               drawer_layout.closeDrawers();
               return NavigateToFragment( menu_item.getItemId() );
            }//onNavigationItemSelected

         };//onNavigationItemSelected


      navigation_view.setNavigationItemSelectedListener( on_item_selected );
   }//InitNavigationView
   //---------------------------------------------------------------------------
   private void InitDrawer(){

      drawer_layout = (DrawerLayout) findViewById( R.id.navigation_drawer );

      ActionBarDrawerToggle action_bar_toggle =
         new ActionBarDrawerToggle(
            this,
            drawer_layout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close ){
            //------------------------------------------------------------------
            @Override
            public void onDrawerClosed( View drawerView ){
               super.onDrawerClosed( drawerView );
            }//onDrawerClosed
            //------------------------------------------------------------------
            @Override
            public void onDrawerOpened( View drawerView ){
               super.onDrawerOpened( drawerView );
            }//onDrawerOpened
            //------------------------------------------------------------------

         };//action_bar_toggle

      drawer_layout.setDrawerListener( action_bar_toggle );

      //calling sync state is necessary to show icon
      action_bar_toggle.syncState();
   }//InitDrawer
   //---------------------------------------------------------------------------
   private void InitFragment(){
      current_fragment_id = GetCurrentFragmentId();
      NavigateToFragment( current_fragment_id );
   }//InitFragment
   //---------------------------------------------------------------------------
   private void InitAuthorization(){

      View header_view = navigation_view.getHeaderView( 0 );

      /*
      cmp_auth = new CmpDrawerAuth();
      cmp_auth.SetHeaderView( header_view );
      cmp_auth.Init();
      */
   }//InitAuthorization
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private int GetCurrentFragmentId(){

      current_fragment_id = Utils.GetSharedPreference( sp_key_current_fragment_id );

      switch( current_fragment_id ){
         case R.id.nav_item_budgets:
         case R.id.nav_item_currencies:
         case R.id.nav_item_users:
            break;

         default:
            current_fragment_id = R.id.nav_item_budgets;
            break;
      }//switch

      SetCurrentFragmentId( current_fragment_id );

      return current_fragment_id;
   }//GetCurrentFragmentId
   //---------------------------------------------------------------------------
   private void SetCurrentFragmentId( int id_fragment ){
      Utils.SetSharedPreference( sp_key_current_fragment_id, id_fragment );
   }//SetCurrentFragmentId
   //---------------------------------------------------------------------------
   public boolean NavigateToFragment( int id_fragment ){

      Fragment fragment_content;
      String   tag;

      switch( id_fragment ){
         case R.id.nav_item_budgets:
            toolbar.setTitle( R.string.toolbar_title_budgets );
            tag = Frag_ListBudgets.TAG;
            fragment_content = new Frag_ListBudgets();
            break;

         case R.id.nav_item_users:
            toolbar.setTitle( R.string.toolbar_title_users );
            tag = Frag_ListUsers.TAG;
            fragment_content = new Frag_ListUsers();
            break;

         case R.id.nav_item_currencies:
            toolbar.setTitle( R.string.toolbar_title_currencies );
            tag = Frag_ListCurrencies.TAG;
            fragment_content = new Frag_ListCurrencies();
            break;

         default:
            return false;
      }//switch

      SetCurrentFragmentId( id_fragment );
      SelectNavigationItem( id_fragment );


      android.support.v4.app.FragmentTransaction fragment_transaction;
      fragment_transaction = getSupportFragmentManager().beginTransaction();
      fragment_transaction.replace( R.id.fragment_content, fragment_content, tag );
      fragment_transaction.commitAllowingStateLoss();//fragment will lost its state
      getSupportFragmentManager().executePendingTransactions();

      return true;
   }//NavigateToFragment
   //---------------------------------------------------------------------------
   private void SelectNavigationItem( int id_fragment ){

      MenuItem menu_item;

      Menu menu = navigation_view.getMenu();

      switch( id_fragment ){
         case R.id.nav_item_budgets:
            menu_item = menu.findItem( R.id.nav_item_budgets );
            break;

         case R.id.nav_item_users:
            menu_item = menu.findItem( R.id.nav_item_users );
            break;

         case R.id.nav_item_currencies:
            menu_item = menu.findItem( R.id.nav_item_currencies );
            break;

         default:
            return;
      }//switch

      menu_item.setChecked( true );
   }//SelectNavigationItem
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void CancelAllNotification(){

      NotificationManager notification_manager =
         (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

      notification_manager.cancelAll();

   }//CancelAllNotification
   //---------------------------------------------------------------------------

}//Act
