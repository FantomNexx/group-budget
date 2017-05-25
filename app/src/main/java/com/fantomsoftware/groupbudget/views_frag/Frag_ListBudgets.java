package com.fantomsoftware.groupbudget.views_frag;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantomsoftware.bottomsheet.BottomSheet;
import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.adapters.RecycleA_Budgets;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.data.Budget;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.fantomsoftware.groupbudget.views_act.Act;
import com.fantomsoftware.groupbudget.views_act.Act_Budget;
import com.fantomsoftware.groupbudget.views_act.Act_BudgetOperations;



public class Frag_ListBudgets extends Fragment{
   //---------------------------------------------------------------------------
   public static final String TAG = "Frag_ListBudgets";

   private View                root_view;
   private RecycleA_Budgets    adapter;
   private SparseArray<Budget> data;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public View onCreateView( LayoutInflater inf, ViewGroup cntr, Bundle bundle ){
      root_view = inf.inflate( R.layout.frag_list_budgets, cntr, false );
      return root_view;
   }//onCreateView
   //---------------------------------------------------------------------------
   @Override
   public void onResume(){
      super.onResume();
      Utils.UpdateActivity( ( (Act) getActivity() ) );
      Init();
   }//onResume
   //---------------------------------------------------------------------------
   @Override
   public void onActivityResult( int request, int result, Intent intent ){

      if( result != Activity.RESULT_OK ){
         return;
      }//if

      switch( request ){
         case ConstsAct.REQUEST_NEW:
            OnItemAdded( intent );
            break;

         case ConstsAct.REQUEST_EDIT:
            OnItemEdited( intent );
            break;
      }//switch
   }//onActivityResult
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Init(){

      int opened_budget_id = Utils.GetSharedPreference(
         Act_BudgetOperations.sp_key_is_showing_details );

      if( opened_budget_id != -1 ){
         ShowBudgetDetails( opened_budget_id );
         return;
      }//if

      InitList();
      InitFloatingActionButton();
   }//Init
   //---------------------------------------------------------------------------
   private void InitList(){
      UpdateData();
   }//InitList
   //---------------------------------------------------------------------------
   private void InitFloatingActionButton(){

      View.OnClickListener cl_fab = new View.OnClickListener(){
         @Override
         public void onClick( View v ){

            Data.instance.db_user.UpdateLocal();
            if( Data.instance.db_user == null ||
               Data.instance.db_user.users.size() == 0 ){
               AskToAddUsers();
               return;
            }//if no users

            Intent intent = new Intent( Utils.context, Act_Budget.class );
            intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_NEW );
            startActivityForResult( intent, ConstsAct.REQUEST_NEW );
         }//onClick
      };//cl_fab

      FloatingActionButton fab = (FloatingActionButton)
         root_view.findViewById( R.id.fab_main );

      fab.setOnClickListener( cl_fab );
   }//InitFloatingActionButton
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void UpdateData(){

      OnResultId on_item_click = new OnResultId(){
         @Override
         public void OnResult( int id ){
            OnListItemClick( id );
         }//OnResult
      };//on_item_click

      OnResultId on_item_long_click = new OnResultId(){
         @Override
         public void OnResult( int id ){
            OnListItemLongClick( id );
         }//OnResult
      };//on_item_click


      data = Data.instance.db_budget.Get();

      adapter = new RecycleA_Budgets( data );
      adapter.SetOnListItemClick( on_item_click );
      adapter.SetOnListItemLongClick( on_item_long_click );

      RecyclerView.LayoutManager layout_manager =
         new LinearLayoutManager( Utils.context );

      RecyclerView rv_list =
         (RecyclerView) root_view.findViewById( R.id.rv_list );

      rv_list.setLayoutManager( layout_manager );
      rv_list.setAdapter( adapter );
   }//UpdateData
   //---------------------------------------------------------------------------
   private void ShowBudgetDetails( int id ){
      Intent intent = new Intent( Utils.context, Act_BudgetOperations.class );
      intent.putExtra( ConstsAct.KEY_ID, id );
      startActivityForResult( intent, ConstsAct.REQUEST_EDIT );
   }//ShowBudgetDetails
   //---------------------------------------------------------------------------
   private void AskToAddUsers(){

      View.OnClickListener cl_add = new View.OnClickListener(){
         @Override
         public void onClick( View view ){

            try{
               ( (Act) Utils.activity ).NavigateToFragment( R.id.nav_item_users );
            }catch( Exception e ){
               Log.d( Utils.log_key, "AskToAddUsers: failed to switc to Users fragment" );
            }//try

         }//onClick
      };//cl_add

      View   view = Utils.activity.findViewById( android.R.id.content );
      String msg  = Utils.GetResString( R.string.error_users_required );
      String btn  = Utils.GetResString( R.string.error_users_btn_add );

      Snackbar.make( view, msg, Snackbar.LENGTH_LONG ).
         setAction( btn, cl_add ).show();

   }//AskToAddUsers
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnListItemClick( int id_item ){
      ShowBudgetDetails( id_item );
   }//OnListItemClick
   //---------------------------------------------------------------------------
   private void OnListItemLongClick(final int id_item ){

      DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int which ){
            switch( which ){
               case R.id.bs_edit:
                  EditItem( id_item );
                  break;

               case R.id.bs_delete:
                  DeleteItem(id_item);
                  break;
            }//switch
         }//onClick
      };//OnClickListener

      final BottomSheet.Builder sheet_builder =
         new BottomSheet.Builder( getActivity(), R.style.BottomSheet );

      sheet_builder.title( getString( R.string.bottom_sheet_title ) );
      sheet_builder.sheet( R.menu.bottom_sheet_delete_option );
      sheet_builder.listener( cl );

      final BottomSheet bottom_sheet_options = sheet_builder.build();
      bottom_sheet_options.show();

   }//OnListItemLongClick
   //---------------------------------------------------------------------------
   private void OnItemAdded( Intent intent ){

      Bundle extras = intent.getExtras();
      if( !extras.containsKey( ConstsAct.KEY_ID ) ){
         return;
      }//if

      int    id   = extras.getInt( ConstsAct.KEY_ID );
      Budget item = Data.instance.db_budget.Get( id );

      data.put( id, item );
      adapter.Update();

   }//OnItemAdded
   //---------------------------------------------------------------------------
   private void OnItemEdited( Intent intent ){
      Bundle extras = intent.getExtras();
      if( !extras.containsKey( ConstsAct.KEY_ID ) ){
         return;
      }//if

      int    id   = extras.getInt( ConstsAct.KEY_ID );
      Budget item = Data.instance.db_budget.Get( id );

      boolean is_found = false;
      for( int i = 0; i < data.size(); i++ ){
         if( data.valueAt( i ).id == item.id ){
            data.setValueAt( i, item );
            is_found = true;
            break;
         }//if
      }//for

      if( !is_found ){
         data.put( data.size(), item );
      }//if not found

      adapter.Update();
   }//OnItemEdited
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void DeleteItem( int id_item ){

      String msg = getString( R.string.snack_msg_delete_failed );

      if( Data.instance.db_budget.Remove( id_item ) != -1 ){

         data.remove( id_item );
         adapter.Update();

         msg = getString( R.string.snack_msg_delete_success );
      }//if success

      Utils.SnackL( root_view, msg );

   }//DeleteItem
   //---------------------------------------------------------------------------
   private void EditItem( int id_item ){
      Intent intent = new Intent( Utils.context, Act_Budget.class );
      intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_EDIT );
      intent.putExtra( ConstsAct.KEY_ID, id_item );
      startActivityForResult( intent, ConstsAct.REQUEST_EDIT );
   }//DeleteItem
   //---------------------------------------------------------------------------

}//Frag_ListUsers

