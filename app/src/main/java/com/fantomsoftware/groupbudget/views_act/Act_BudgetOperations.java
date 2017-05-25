package com.fantomsoftware.groupbudget.views_act;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.fantomsoftware.bottomsheet.ActionMenu;
import com.fantomsoftware.bottomsheet.BottomSheet;
import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.adapters.TabPagerAdapter;
import com.fantomsoftware.groupbudget.data.Budget;
import com.fantomsoftware.groupbudget.data.BudgetUser;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.interfaces.OnResultNumber;
import com.fantomsoftware.groupbudget.interfaces.OnResultStringTwo;
import com.fantomsoftware.groupbudget.interfaces.OnResultString;
import com.fantomsoftware.groupbudget.tasks.Task_MakeReport;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.fantomsoftware.groupbudget.views_frag.Frag_TabOpsBudget;
import com.fantomsoftware.groupbudget.views_frag.Frag_TabOpsUser;
import com.fantomsoftware.groupbudget.dialogs.DialogEnterString;

import java.io.File;


public class Act_BudgetOperations extends AppCompatActivity{
//---------------------------------------------------------------------------
private Budget budget = null;

private Frag_TabOpsBudget tap_budget;
private Frag_TabOpsUser   tap_user;

private int tab_selected     = 0;
private int is_showing_stats = -1;

private NotificationManager        notif_manager   = null;
private NotificationCompat.Builder notif_builder   = null;
private int                        notif_id_report = 1001;

public static final String sp_key_is_showing_stats   = "sp_key_is_showing_stats";
public static final String sp_key_is_showing_details = "sp_key_is_showing_details";
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
@Override
protected void onCreate( Bundle savedInstanceState ){
  super.onCreate( savedInstanceState );
  setContentView( R.layout.act_budget_operations );
  Utils.UpdateActivity( this );

  if( !IsParamsOk() ){
    Exit();
  }else{
    Init();
  }//if OK

}//onCreate
//---------------------------------------------------------------------------
@Override
protected void onNewIntent( Intent intent ){
  super.onNewIntent( intent );
  Utils.UpdateActivity( this );

  if( !IsParamsOk() ){
    Exit();
  }else{
    Init();
  }//if OK

}//onNewIntent
//---------------------------------------------------------------------------
@Override
protected void onResume(){
  super.onResume();
  Utils.UpdateActivity( this );
}//onResume
//---------------------------------------------------------------------------
@Override
public boolean onCreateOptionsMenu( Menu menu ){
  MenuInflater inflater = getMenuInflater();
  inflater.inflate( R.menu.menu_act_budget_operations, menu );

  SearchView.OnCloseListener on_close = new SearchView.OnCloseListener(){
    @Override
    public boolean onClose(){
      tap_budget.Search( "" );
      tap_user.Search( "" );
      return false;
    }//onClose
  };//on_close

  View.OnClickListener on_click_search = new View.OnClickListener(){
    @Override
    public void onClick( View v ){
      //do nothing
    }//onClick
  };//on_click_search

  SearchView.OnQueryTextListener on_input = new SearchView.OnQueryTextListener(){
    //---------------------------------------------------------------------
    @Override
    public boolean onQueryTextSubmit( String input ){
      //do nothing
      return false;
    }//onQueryTextSubmit
    //---------------------------------------------------------------------
    @Override
    public boolean onQueryTextChange( String input ){
      tap_budget.Search( input );
      tap_user.Search( input );
      return false;
    }//onQueryTextChange
    //---------------------------------------------------------------------
  };//on_input

  MenuItem   menu_item   = menu.findItem( R.id.action_search );
  SearchView search_view = (SearchView) menu_item.getActionView();

  search_view.setQueryHint( getString( R.string.search ) );
  search_view.setOnCloseListener( on_close );
  search_view.setOnSearchClickListener( on_click_search );
  search_view.setOnQueryTextListener( on_input );
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

    case R.id.action_report:
      SaveReport();
      break;

    case android.R.id.home:
      GoBack();
      break;

    default:
      return super.onOptionsItemSelected( item );
  }//switch

  return true;
}//onOptionsItemSelected
//---------------------------------------------------------------------------
@Override
public void onActivityResult( int request, int result, Intent data ){

  if( result != Activity.RESULT_OK ){
    return;
  }//if

  switch( request ){
    case ConstsAct.REQUEST_NEW:
    case ConstsAct.REQUEST_EDIT:
    case ConstsAct.REQUEST_DELETE:
      tap_budget.Update();
      tap_user.Update();
      break;
  }//switch
}//onActivityResult
//---------------------------------------------------------------------------
@Override
public void onBackPressed(){
  GoBack();
}//onBackPressed
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
private boolean IsParamsOk(){

  Intent intent = getIntent();
  if( intent == null ){
    return false;
  }//if

  Bundle extras = intent.getExtras();
  if( extras == null ){
    return false;
  }//if

  if( !extras.containsKey( ConstsAct.KEY_ID ) ){
    return false;
  }//if

  Integer budget_id = extras.getInt( ConstsAct.KEY_ID );
  budget = Data.instance.db_budget.Get( budget_id );

  if( budget == null ){
    return false;
  }//if

  //set currently displaying budget, to show it automatically next time
  //and not wasting time on click the same budget again from the list
  Utils.SetSharedPreference( sp_key_is_showing_details, budget_id );

  if( extras.containsKey( ConstsAct.KEY_ACTION ) ){
    ProcessAction( intent );
  }//if

  return true;
}//IsParamsOk
//---------------------------------------------------------------------------
private void ProcessAction( Intent intent ){

  if( intent == null ){
    return;
  }//if

  Bundle extras = intent.getExtras();
  if( extras == null ){
    return;
  }//if

  String action = extras.getString( ConstsAct.KEY_ACTION );
  if( action == null || action.equals( "" ) ){
    return;
  }//if


  if( extras.containsKey( ConstsAct.KEY_ID_NOTIFICATION ) ){
    int id_notification = extras.getInt( ConstsAct.KEY_ID_NOTIFICATION );

    NotificationManager notification_manager =
        (NotificationManager) getSystemService( Context.NOTIFICATION_SERVICE );

    notification_manager.cancel( id_notification );
  }//if


  switch( action ){
    case ConstsAct.KEY_ACTION_SHARE_REPORT:
      String report_name = extras.getString( ConstsAct.KEY_RERPORT_NAME );
      String report_file_name = extras.getString( ConstsAct.KEY_RERPORT_FILE_NAME );
      ProcessActionShare( report_name, report_file_name );
      break;
  }//switch

}//ProcessAction
//---------------------------------------------------------------------------
private void ProcessActionShare( String report_name, String report_file_name ){

  if( report_name == null || report_name.equals( "" ) ){
    return;
  }//if

  if( report_file_name == null || report_file_name.equals( "" ) ){
    return;
  }//if

  String subject = Utils.context.getString( R.string.btn_share_report );

  Intent intent = new Intent( android.content.Intent.ACTION_SEND );
  intent.setType( "*/*" );
  intent.putExtra( android.content.Intent.EXTRA_SUBJECT, subject );
  intent.putExtra( android.content.Intent.EXTRA_TEXT, report_name );
  intent.putExtra( Intent.EXTRA_STREAM, Uri.fromFile( new File( report_file_name ) ) );

  startActivity( Intent.createChooser( intent, subject ) );
}//ProcessActionShare
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
private void Init(){

  is_showing_stats = Utils.GetSharedPreference( sp_key_is_showing_stats );
  UpdateStatsVisibility( is_showing_stats );

  //UpdateLocal - used to get usernames quickly from local vaiable
  Data.instance.db_user.UpdateLocal();

  InitToolbar();
  InitTabs();
  InitFAB_Main();
  InitFAB_Options();
}//Init
//---------------------------------------------------------------------------
private void InitToolbar(){

  Toolbar toolbar = (Toolbar) findViewById( R.id.toolbar );
  toolbar.setTitle( budget.name );

  if( !budget.comment.equals( "" ) ){
    toolbar.setSubtitle( budget.comment );
  }//if

  View.OnClickListener cl = new View.OnClickListener(){
    @Override
    public void onClick( View v ){
      GoBack();
    }//onClick
  };//cl

  toolbar.setNavigationOnClickListener( cl );

  setSupportActionBar( toolbar );


  android.support.v7.app.ActionBar ab = getSupportActionBar();
  if( ab != null ){
    ab.setDisplayHomeAsUpEnabled( true );
    ab.setHomeButtonEnabled( true );
  }//if actionbar != null
}//Init
//---------------------------------------------------------------------------
private void InitTabs(){

  tap_budget = new Frag_TabOpsBudget();
  tap_user = new Frag_TabOpsUser();

  tap_budget.SetData( budget.id );
  tap_user.SetData( budget.id, GetFirstUserId() );

  tap_budget.UpdateStatsVisibility( is_showing_stats );
  tap_user.UpdateStatsVisibility( is_showing_stats );


  TabPagerAdapter pager_adapter = new TabPagerAdapter( getSupportFragmentManager() );
  pager_adapter.addFragment( tap_budget, getString( R.string.tab_budget ) );
  pager_adapter.addFragment( tap_user, getString( R.string.tab_user ) );


  ViewPager.OnPageChangeListener chl_view_pager =
      new ViewPager.OnPageChangeListener(){
        //----------------------------------------------------------------------------
        @Override
        public void onPageScrolled(
            int position, float positionOffset, int positionOffsetPixels ){
        }//onPageScrolled
        //----------------------------------------------------------------------------
        @Override
        public void onPageSelected( int position ){
          tab_selected = position;
        }//onPageSelected
        //----------------------------------------------------------------------------
        @Override
        public void onPageScrollStateChanged( int state ){
        }//onPageScrollStateChanged
        //----------------------------------------------------------------------------
      };//chl_view_pager

  ViewPager view_pager = (ViewPager) findViewById( R.id.view_pager );
  view_pager.setAdapter( pager_adapter );
  view_pager.addOnPageChangeListener( chl_view_pager );

  TabLayout tab_layout = (TabLayout) findViewById( R.id.tab_layout );
  tab_layout.setupWithViewPager( view_pager );
}//InitTabs
//---------------------------------------------------------------------------
private void InitFAB_Main(){

  View.OnClickListener cl = new View.OnClickListener(){
    @Override
    public void onClick( View v ){
      Intent intent = new Intent( Utils.context, Act_BudgetOperation.class );
      intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_NEW );
      intent.putExtra( ConstsAct.KEY_ID_BUDGET, budget.id );
      startActivityForResult( intent, ConstsAct.REQUEST_NEW );
    }//onClick
  };

  FloatingActionButton fab =
      (FloatingActionButton) findViewById( R.id.fab_main );

  fab.setOnClickListener( cl );
}//InitFAB_Main
//---------------------------------------------------------------------------
private void InitFAB_Options(){

  View.OnClickListener cl = new View.OnClickListener(){
    @Override
    public void onClick( View v ){
      ShowBottomMenu();
    }//onClick
  };

  FloatingActionButton fab =
      (FloatingActionButton) findViewById( R.id.fab_options );

  fab.setOnClickListener( cl );
}//InitFAB_Options
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
private int GetFirstUserId(){

  SparseArray<BudgetUser> budget_users =
      Data.instance.db_budget_users.GetByBudget( budget.id );

  if( budget_users.size() != 0 ){
    BudgetUser user = budget_users.valueAt( 0 );
    return user.id_user;
  }//if

  return -1;
}//GetFirstUserId
//---------------------------------------------------------------------------
private void ShowBottomMenu(){

  DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener(){
    @Override
    public void onClick( DialogInterface dialog, int which ){

      switch( which ){
        case R.id.bs_show_stats:
          UpdateStatsVisibility( View.VISIBLE );
          tap_budget.UpdateStatsVisibility( is_showing_stats );
          tap_user.UpdateStatsVisibility( is_showing_stats );
          break;

        case R.id.bs_hide_stats:
          UpdateStatsVisibility( View.GONE );
          tap_budget.UpdateStatsVisibility( is_showing_stats );
          tap_user.UpdateStatsVisibility( is_showing_stats );
          break;

        case R.id.bs_scroll_top:
          tap_budget.ScrollTop();
          tap_user.ScrollTop();
          break;

        case R.id.bs_scroll_bottom:
          tap_budget.ScrollBottom();
          tap_user.ScrollBottom();
          break;
      }//switch
    }//onClick
  };//cl


  final BottomSheet.Builder sheet_builder =
      new BottomSheet.Builder( this, R.style.BottomSheet );

  sheet_builder.title( getString( R.string.bottom_sheet_title ) );
  sheet_builder.sheet( R.menu.bottom_sheet_budget_details );


  ActionMenu menu = sheet_builder.GetMenu();

  if( is_showing_stats == View.VISIBLE ){
    menu.removeItem( R.id.bs_show_stats );
  }else{
    menu.removeItem( R.id.bs_hide_stats );
  }//else

  int position = 0;
  int size     = 0;

  switch( tab_selected ){
    case 0:
      position = tap_budget.GetListPosition();
      size = tap_budget.GetListSize();
      break;

    case 1:
      position = tap_user.GetListPosition();
      size = tap_user.GetListSize();
      break;
  }//switch

  if( position == 0 ){
    menu.removeItem( R.id.bs_scroll_top );
  }else if( position >= (size - 10) ){
    menu.removeItem( R.id.bs_scroll_bottom );
  }//else

  sheet_builder.listener( cl );


  final BottomSheet bottom_sheet_options = sheet_builder.build();
  bottom_sheet_options.show();
}//ShowBottomMenu
//---------------------------------------------------------------------------
private void UpdateStatsVisibility( int is_showing_stats ){

  switch( is_showing_stats ){

    case View.VISIBLE:
    case View.GONE:
      break;

    default:
      is_showing_stats = View.VISIBLE;
      break;
  }//switch

  this.is_showing_stats = is_showing_stats;

  Utils.SetSharedPreference( sp_key_is_showing_stats, is_showing_stats );
}//UpdateStatsVisibility
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
private void SaveReport(){

  String title = getString( R.string.dialog_report_csv_title );
  String hint  = getString( R.string.dialog_report ) + " " + budget.name + ".csv";

  OnResultString on_result = new OnResultString(){
    @Override
    public void OnResult( String report_name ){
      MakeReport( report_name );
    }//OnResult
  };//on_result

  DialogEnterString dialog = new DialogEnterString();
  dialog.SetData( title, hint );
  dialog.SetOnResult( on_result );
  dialog.show( getSupportFragmentManager(), "TAG" );
}//SaveReport
//---------------------------------------------------------------------------
private void MakeReport( String report_name ){

  OnEvent on_pre_execute = new OnEvent(){
    @Override
    public void OnEvent(){

      String title   = getString( R.string.notification_report_making );
      String content = budget.name;
      if( !budget.comment.equals( "" ) ){
        content += ", " + budget.comment;
      }//if

      notif_manager = (NotificationManager)
          getSystemService( Context.NOTIFICATION_SERVICE );

      notif_builder = new NotificationCompat.Builder( getBaseContext() );
      notif_builder.setContentTitle( title );
      notif_builder.setContentText( content );
      notif_builder.setSmallIcon( R.drawable.ic_notification_small );
    }//OnResult
  };//on_pre_execute


  OnResultStringTwo on_result = new OnResultStringTwo(){
    @Override
    public void OnResult( String report_name, String report_file_name ){
      ShowNotification_ShareReport( report_name, report_file_name );
    }//OnResult
  };//on_result


  OnResultNumber on_progress = new OnResultNumber(){
    @Override
    public void OnResult( float progress ){

      // Sets the progress indicator to a max value, the
      // current completion percentage, and "determinate" state
      notif_builder.setProgress( 100, (int) progress, false );
      // Displays the progress bar for the first time.
      notif_manager.notify( notif_id_report, notif_builder.build() );
    }//OnResult
  };//on_progress


  Task_MakeReport task = new Task_MakeReport();
  task.SetData( budget.id, report_name );
  task.SetOnPreExecute( on_pre_execute );
  task.SetOnResult( on_result );
  task.SetOnProgress( on_progress );
  task.execute();
}//MakeReport
//---------------------------------------------------------------------------
private void ShowNotification_ShareReport( String report_name, String report_file_name ){

  // Creates an explicit intent for an Activity in your app
  Intent intent_result = new Intent( this, Act_BudgetOperations.class );

  TaskStackBuilder stackBuilder = TaskStackBuilder.create( this );
  stackBuilder.addParentStack( Act.class );
  stackBuilder.addNextIntent( intent_result );

  PendingIntent pending_intent_result =
      stackBuilder.getPendingIntent( 0, PendingIntent.FLAG_CANCEL_CURRENT );


  String action_name_unique = Utils.GenerateViewId();

  Intent intent_action = new Intent( this, Act_BudgetOperations.class );

  intent_action.setAction( action_name_unique );

  intent_action.putExtra( ConstsAct.KEY_ACTION, ConstsAct.KEY_ACTION_SHARE_REPORT );
  intent_action.putExtra( ConstsAct.KEY_RERPORT_NAME, report_name );
  intent_action.putExtra( ConstsAct.KEY_RERPORT_FILE_NAME, report_file_name );
  intent_action.putExtra( ConstsAct.KEY_ID, budget.id );
  intent_action.putExtra( ConstsAct.KEY_ID_NOTIFICATION, notif_id_report );

  //intent_action.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK );
  intent_action.setFlags( Intent.FLAG_ACTIVITY_CLEAR_TOP );

  PendingIntent pending_intent_action =
      PendingIntent.getActivity( this, 0, intent_action, PendingIntent.FLAG_CANCEL_CURRENT );


  String title   = getString( R.string.notification_report_complete );
  String content = budget.name;
  if( !budget.comment.equals( "" ) ){
    content += ", " + budget.comment;
  }//if

  // When the loop is finished, updates the notification
  notif_builder.setContentTitle( title );
  notif_builder.setContentText( content );
  notif_builder.setContentIntent( pending_intent_result );
  notif_builder.setProgress( 0, 0, false );// Removes the progress bar
  notif_builder.setDefaults( Notification.DEFAULT_ALL );
  notif_builder.addAction(
      R.drawable.ic_share_black_24dp,
      getString( R.string.btn_share ),
      pending_intent_action
  );//add action

  notif_manager.notify( notif_id_report, notif_builder.build() );
}//ShowNotification_ShareReport
//---------------------------------------------------------------------------

public void Update(){
  tap_budget.Update();
  tap_user.Update();
}

//---------------------------------------------------------------------------
private void GoBack(){
  //We leaved current budget so there is no need to come back
  //to it automatically next time
  Utils.SetSharedPreference( sp_key_is_showing_details, -1 );
  super.onBackPressed();
  Exit();
}//GoBack
//---------------------------------------------------------------------------
private void Exit(){
  Intent return_intent = new Intent();
  setResult( RESULT_OK, return_intent );
  finish();
}//SaveAndExit
//---------------------------------------------------------------------------

}//Act_BudgetOperations
