package com.fantomsoftware.groupbudget.data;
import android.app.Application;
import android.os.Environment;

import com.fantomsoftware.groupbudget.cmp.CustomExceptionHandler;
import com.fantomsoftware.groupbudget.consts.ConstsStats;
import com.fantomsoftware.groupbudget.data.db.DBBudget;
import com.fantomsoftware.groupbudget.data.db.DBBudgetOp;
import com.fantomsoftware.groupbudget.data.db.DBBudgetUsers;
import com.fantomsoftware.groupbudget.data.db.DBCurrency;
import com.fantomsoftware.groupbudget.data.db.DBHelper;
import com.fantomsoftware.groupbudget.data.db.DBUser;
import com.fantomsoftware.groupbudget.data.db.DBUserOp;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.tasks.Task_InitApp;
import com.fantomsoftware.groupbudget.utils.Permissions;
import com.fantomsoftware.groupbudget.utils.Storage;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.fantomsoftware.groupbudget.views_act.Act_Splash;

public class Data extends android.support.multidex.MultiDexApplication{
//--------------------------------------------------------------------

public static boolean is_inited = false;
public static Data    instance  = null;

public Storage storage;

public DBHelper      db_helper;
public DBCurrency    db_currency;
public DBUser        db_user;
public DBBudget      db_budget;
public DBBudgetUsers db_budget_users;
public DBUserOp      db_user_op;
public DBBudgetOp    db_budget_op;


public Task_InitApp task               = null;
public Act_Splash   act_splash         = null;
public OnEvent      on_splash_showed   = null;//called when splash is showed
public boolean      is_wait_permission = false;
//--------------------------------------------------------------------


   /*
   @Override
   public void onConfigurationChanged( Configuration newConfig ){
      Log.d( "eApplication", "[onConfigurationChanged]" );

      Configuration delta = new Configuration();
      newConfig.diff( delta );

      super.onConfigurationChanged( newConfig );
   }
   @Override
   public void onLowMemory(){
      Log.d( "eApplication", "[onLowMemory]" );
      super.onLowMemory();
   }
   @Override
   public void onTrimMemory( int level ){
      Log.d( "eApplication", "[onTrimMemory]" + level );
      super.onTrimMemory( level );
   }
   */


//--------------------------------------------------------------------
@Override
public void onCreate(){

  super.onCreate();

  is_inited = false;

  InitCrashReporter();

}//onCreate
//--------------------------------------------------------------------
public void InitCrashReporter(){

  if( !(Thread.getDefaultUncaughtExceptionHandler()
      instanceof CustomExceptionHandler) ){

    //String app_root_folder = Data.storage.GetAppRootFolder();
    String app_root_folder = Environment.getDataDirectory().getAbsolutePath();

    Thread.setDefaultUncaughtExceptionHandler(
        new CustomExceptionHandler( app_root_folder + "" ) );
  }//if

}//InitCrashReporter
//--------------------------------------------------------------------
public Data(){
  instance = this;
}//Data
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public boolean Init(){

  if( Utils.context == null ){
    return false;
  }//if app is not re initialized

  if( !Permissions.IsPermissonGranted_Storage() ){
    return false;
  }//if app has no permissions for STORAGE

  storage = new Storage();
  if( !storage.Init() ){
    return false;
  }//init storage


  db_helper = new DBHelper();
  db_currency = new DBCurrency( db_helper );
  db_budget_users = new DBBudgetUsers( db_helper );
  db_budget_op = new DBBudgetOp( db_helper );
  db_user_op = new DBUserOp( db_helper );
  db_budget = new DBBudget( db_helper );
  db_user = new DBUser( db_helper );


  Operation.InitOptions();
  ConstsStats.InitOptions();

  is_inited = true;

  return true;
}//Init
//--------------------------------------------------------------------

}//Data
