package com.fantomsoftware.groupbudget.views_frag;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantomsoftware.bottomsheet.BottomSheet;
import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.adapters.RecycleA_ListOpsUser;
import com.fantomsoftware.groupbudget.data.BudgetUser;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.OperationUser;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;
import com.fantomsoftware.groupbudget.interfaces.OnResultPosition;
import com.fantomsoftware.groupbudget.interfaces.OnResultSearchOpsUser;
import com.fantomsoftware.groupbudget.tasks.Task_SearchOpsUser;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.consts.ConstsStats;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.fantomsoftware.groupbudget.views_act.Act_BudgetOperation;
import com.fantomsoftware.groupbudget.cmp.CmpStatsBudget;
import com.fantomsoftware.groupbudget.cmp.CmpStatsUser;
import com.fantomsoftware.groupbudget.dialogs.DialogPickOptionSingl;


public class Frag_TabOpsUser extends Fragment{
   //---------------------------------------------------------------------------
   private View         root_view;
   private CmpStatsUser cmp_stats_user;
   private RecyclerView rv_list;

   private int id_budget;
   private int id_user;
   private int id_stat;

   private int is_showing_stats = -1;

   private Options user_options;

   private Task_SearchOpsUser task = null;

   private SparseArray<OperationUser> data;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public View onCreateView( LayoutInflater inf, ViewGroup cntr, Bundle b ){

      root_view = inf.inflate( R.layout.frag_tab_ops_user, cntr, false );
      Init();

      return root_view;
   }//onCreateView
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetData( int id_budget, int id_user ){
      this.id_budget = id_budget;
      this.id_user = id_user;
      InitUserOptions();
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void Init(){
      InitCmpStatsUser();
      InitList();
   }//Init
   //---------------------------------------------------------------------------
   private void InitUserOptions(){

      user_options = new Options();

      SparseArray<BudgetUser> attached_users =
         Data.instance.db_budget_users.GetByBudget( id_budget );

      String user_name;

      for( int i = 0; i < attached_users.size(); i++ ){
         int id_user = attached_users.valueAt( i ).id_user;

         user_name = Data.instance.db_user.GetName( id_user );

         user_options.Add( id_user, user_name, false );
      }//for

   }//InitUserOptions
   //---------------------------------------------------------------------------
   private void InitCmpStatsUser(){

      id_stat = Utils.GetSharedPreference( CmpStatsBudget.sp_key_id_stats );

      if( id_stat == -1 ){
         id_stat = ConstsStats.STATS_BALANCE;
         Utils.SetSharedPreference( CmpStatsBudget.sp_key_id_stats, id_stat );
      }//if

      OnEvent on_title_click = new OnEvent(){
         @Override
         public void OnEvent(){

            String  title   = Utils.context.getString( R.string.dialog_pick_user_title );
            Options options = user_options;
            options.SetSelectedOption( id_user );

            OnResultPosition on_result = new OnResultPosition(){
               @Override
               public void OnResult( int position ){

                  id_user = user_options.GetIdByPosition( position );

                  cmp_stats_user.SetIdUser( id_user );
                  cmp_stats_user.Update();

                  InitList();

               }//OnResult
            };//on_result

            DialogPickOptionSingl dialog = new DialogPickOptionSingl();
            dialog.SetData( title, options );
            dialog.SetOnResult( on_result );
            dialog.show( getFragmentManager(), "TAG" );
         }//OnEvent
      };//on_title_click

      OnEvent on_btn_click = new OnEvent(){
         @Override
         public void OnEvent(){
            String  title   = Utils.context.getString( R.string.dialog_pick_stats_type );
            Options options = ConstsStats.stats_options;
            options.SetSelectedOption( id_stat );

            OnResultPosition on_result = new OnResultPosition(){
               @Override
               public void OnResult( int position ){

                  id_stat = ConstsStats.stats_options.GetIdByPosition( position );

                  Utils.SetSharedPreference( CmpStatsBudget.sp_key_id_stats, id_stat );

                  cmp_stats_user.SetIdStat( id_stat );
                  cmp_stats_user.Update();
               }//OnResult
            };//on_result

            DialogPickOptionSingl dialog = new DialogPickOptionSingl();
            dialog.SetData( title, options );
            dialog.SetOnResult( on_result );
            dialog.show( getFragmentManager(), "TAG" );
         }//OnEvent
      };//on_btn_click

      cmp_stats_user = (CmpStatsUser) root_view.findViewById( R.id.cmp_stats_user );
      cmp_stats_user.SetIdBudget( id_budget );
      cmp_stats_user.SetIdUser( id_user );
      cmp_stats_user.SetIdStat( id_stat );
      cmp_stats_user.SetOnTitleClick( on_title_click );
      cmp_stats_user.SetOnBtnClick( on_btn_click );

      if( is_showing_stats == View.VISIBLE ){
         cmp_stats_user.Update();
      }//if

   }//InitCmpStatsUser
   //---------------------------------------------------------------------------
   private void InitList(){

      data = Data.instance.db_user_op.Get( id_budget, id_user );

      RecyclerView.LayoutManager layout_manager = new LinearLayoutManager( Utils.context );

      rv_list = (RecyclerView) root_view.findViewById( R.id.rv_list );
      rv_list.setLayoutManager( layout_manager );

      UpdateList( data );
   }//InitList
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public int GetListPosition(){
      LinearLayoutManager layout_mng = (LinearLayoutManager) rv_list.getLayoutManager();
      int                 position   = layout_mng.findFirstCompletelyVisibleItemPosition();
      return position;
   }//GetListPosition
   //---------------------------------------------------------------------------
   public int GetListSize(){
      if( data != null ){
         return data.size();
      }
      return 0;
   }//GetListSize
   //---------------------------------------------------------------------------
   public void ScrollTop(){
      rv_list.scrollToPosition( 0 );
   }//ScrollTop
   //---------------------------------------------------------------------------
   public void ScrollBottom(){
      rv_list.scrollToPosition( data.size() - 1 );
   }//ScrollBottom
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void UpdateStatsVisibility( int is_showing_stats ){

      this.is_showing_stats = is_showing_stats;

      if( cmp_stats_user == null ){
         return;
      }//if

      switch( is_showing_stats ){

         case View.VISIBLE:
            cmp_stats_user.setVisibility( View.VISIBLE );
            cmp_stats_user.Update();
            break;

         case View.GONE:
            cmp_stats_user.setVisibility( View.GONE );
            break;
      }//switch

   }//UpdateStatsVisibility
   //---------------------------------------------------------------------------
   public void Update(){
      Init();
   }//Update
   //---------------------------------------------------------------------------
   public void Search( String search_str ){

      if( task != null ){
         task.cancel( true );
      }//if

      OnResultSearchOpsUser on_result = new OnResultSearchOpsUser(){
         @Override
         public void OnResult( SparseArray<OperationUser> ops_filtered ){
            UpdateList( ops_filtered );
         }//OnResult
      };//on_result

      task = new Task_SearchOpsUser();
      task.SetData( search_str, data );
      task.SetOnResult( on_result );
      task.execute();
   }//Filter
   //---------------------------------------------------------------------------
   private void UpdateList( SparseArray<OperationUser> data_filtered ){

      OnResultId on_item_click = new OnResultId(){
         @Override
         public void OnResult( int id_budget_opertation ){
            OnListItemClick( id_budget_opertation );
         }//OnResult
      };//on_item_click

      OnResultId on_item_long_click = new OnResultId(){
         @Override
         public void OnResult( int id_operation ){
            OnListItemLongClick( id_operation );
         }//OnResult
      };//on_item_click

      RecycleA_ListOpsUser adapter = new RecycleA_ListOpsUser( data_filtered );
      adapter.SetOnListItemClick( on_item_click );
      adapter.SetOnListItemLongClick( on_item_long_click );

      rv_list.setAdapter( adapter );
   }//UpdateList
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnListItemClick( int id_budget_operation ){
      EditOperation( id_budget_operation );
   }//OnListItemClick
   //---------------------------------------------------------------------------
   private void OnListItemLongClick( final int id_operation ){

      DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int which ){
            switch( which ){
               case R.id.bs_delete:
                  DeleteOperation( id_operation );
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


   //---------------------------------------------------------------------------
   private void EditOperation( int id_operation ){
      Intent intent = new Intent( Utils.context, Act_BudgetOperation.class );
      intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_EDIT );
      intent.putExtra( ConstsAct.KEY_ID_OPERATION, id_operation );
      getActivity().startActivityForResult( intent, ConstsAct.REQUEST_EDIT );
   }//EditOperation
   //---------------------------------------------------------------------------
   private void DeleteOperation( int id_operation ){

      String msg = getString( R.string.snack_msg_delete_failed );

      if( Data.instance.db_user_op.Remove( id_operation ) != -1 ){
         Update();

         msg = getString( R.string.snack_msg_delete_success );
      }//if success

      Utils.SnackL( root_view, msg );
   }//DeleteOperation
   //---------------------------------------------------------------------------

}//Frag_TabOpsUser

