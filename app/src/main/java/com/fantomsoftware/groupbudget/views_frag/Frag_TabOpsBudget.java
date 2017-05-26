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
import com.fantomsoftware.groupbudget.adapters.RecycleA_ListOpsBudget;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.OperationBudget;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;
import com.fantomsoftware.groupbudget.interfaces.OnResultPosition;
import com.fantomsoftware.groupbudget.interfaces.OnResultSearchOpsBudget;
import com.fantomsoftware.groupbudget.tasks.Task_SearchOpsBudget;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.consts.ConstsStats;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.fantomsoftware.groupbudget.views_act.Act_BudgetOperation;
import com.fantomsoftware.groupbudget.cmp.CmpStatsBudget;
import com.fantomsoftware.groupbudget.dialogs.DialogPickOptionSingl;
import com.fantomsoftware.groupbudget.views_act.Act_BudgetOperations;


public class Frag_TabOpsBudget extends Fragment{
//--------------------------------------------------------------------
private View           root_view;
private CmpStatsBudget cmp_stats_budget;
private RecyclerView   rv_list;

private int id_budget;
private int id_stat;

private int is_showing_stats = -1;

private Task_SearchOpsBudget task = null;

private SparseArray<OperationBudget> data;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
@Override
public View onCreateView( LayoutInflater inf, ViewGroup cntr, Bundle b ){

  root_view = inf.inflate( R.layout.frag_tab_ops_budget, cntr, false );
  Init();

  return root_view;
}//onCreateView
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetData( int id_budget ){
  this.id_budget = id_budget;
}//SetData
//--------------------------------------------------------------------

//--------------------------------------------------------------------
public void Init(){
  InitCmpStatsBudget();
  InitList();
}//Init
//--------------------------------------------------------------------
private void InitCmpStatsBudget(){

  id_stat = Utils.GetSharedPreference( CmpStatsBudget.sp_key_id_stats );

  if( id_stat == -1 ){
    id_stat = ConstsStats.STATS_BALANCE;
    Utils.SetSharedPreference( CmpStatsBudget.sp_key_id_stats, id_stat );
  }//if

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

          cmp_stats_budget.SetIdStat( id_stat );
          cmp_stats_budget.Update();
        }//OnResult
      };//on_result

      DialogPickOptionSingl dialog = new DialogPickOptionSingl();
      dialog.SetData( title, options );
      dialog.SetOnResult( on_result );
      dialog.show( getFragmentManager(), "TAG" );
    }//OnEvent
  };//on_event

  cmp_stats_budget = (CmpStatsBudget) root_view.findViewById( R.id.cmp_stats_budget );
  cmp_stats_budget.SetIdBudget( id_budget );
  cmp_stats_budget.SetIdStat( id_stat );
  cmp_stats_budget.SetOnBtnClick( on_btn_click );

  if( is_showing_stats == View.VISIBLE ){
    cmp_stats_budget.Update();
  }//if

}//InitCmpStatsBudget
//--------------------------------------------------------------------
private void InitList(){

  data = Data.instance.db_budget_op.GetOperations( id_budget );

  RecyclerView.LayoutManager layout_manager =
      new LinearLayoutManager( Utils.context );

  rv_list = (RecyclerView) root_view.findViewById( R.id.rv_list );
  rv_list.setLayoutManager( layout_manager );

  UpdateList( data );
}//InitList
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public int GetListPosition(){
  LinearLayoutManager layout_mng = (LinearLayoutManager) rv_list.getLayoutManager();
  int                 position   = layout_mng.findFirstCompletelyVisibleItemPosition();
  return position;
}//GetListPosition
//--------------------------------------------------------------------
public int GetListSize(){
  if( data != null ){
    return data.size();
  }
  return 0;
}//GetListSize
//--------------------------------------------------------------------
public void ScrollTop(){
  rv_list.scrollToPosition( 0 );
}//ScrollTop
// ---------------------------------------------------------------------------
public void ScrollBottom(){
  rv_list.scrollToPosition( data.size() - 1 );
}//ScrollBottom
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void UpdateStatsVisibility( int is_showing_stats ){

  this.is_showing_stats = is_showing_stats;

  if( cmp_stats_budget == null ){
    return;
  }//if

  switch( is_showing_stats ){

    case View.VISIBLE:
      cmp_stats_budget.setVisibility( View.VISIBLE );
      cmp_stats_budget.Update();
      break;

    case View.GONE:
      cmp_stats_budget.setVisibility( View.GONE );
      break;
  }//switch

}//UpdateStatsVisibility
//--------------------------------------------------------------------
public void Update(){
  Init();
}//Update
//--------------------------------------------------------------------
public void Search( String search_str ){

  if( task != null ){
    task.cancel( true );
  }//if

  OnResultSearchOpsBudget on_result = new OnResultSearchOpsBudget(){
    @Override
    public void OnResult( SparseArray<OperationBudget> ops_filtered ){
      UpdateList( ops_filtered );
    }//OnResult
  };//on_result

  task = new Task_SearchOpsBudget();
  task.SetData( search_str, data );
  task.SetOnResult( on_result );
  task.execute();
}//Filter
//--------------------------------------------------------------------
private void UpdateList( SparseArray<OperationBudget> data_filtered ){

  OnResultId on_item_click = new OnResultId(){
    @Override
    public void OnResult( int id_operation ){
      OnListItemClick( id_operation );
    }//OnResult
  };//on_item_click

  OnResultId on_item_long_click = new OnResultId(){
    @Override
    public void OnResult( int id_operation ){
      OnListItemLongClick( id_operation );
    }//OnResult
  };//on_item_click

  RecycleA_ListOpsBudget adapter = new RecycleA_ListOpsBudget( data_filtered );
  adapter.SetOnListItemClick( on_item_click );
  adapter.SetOnListItemLongClick( on_item_long_click );

  rv_list.setAdapter( adapter );
}//UpdateList
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnListItemClick( int id_operation ){
  EditOperation( id_operation );
}//OnListItemClick
//--------------------------------------------------------------------
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

  BottomSheet.Builder sheet_builder =
      new BottomSheet.Builder( getActivity(), R.style.BottomSheet );

  sheet_builder.title( getString( R.string.bottom_sheet_title ) );
  sheet_builder.sheet( R.menu.bottom_sheet_delete_option );
  sheet_builder.listener( cl );

  BottomSheet bottom_sheet_options = sheet_builder.build();
  bottom_sheet_options.show();
}//OnListItemLongClick
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void EditOperation( int id_operation ){
  Intent intent = new Intent( Utils.context, Act_BudgetOperation.class );
  intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_EDIT );
  intent.putExtra( ConstsAct.KEY_ID_OPERATION, id_operation );
  getActivity().startActivityForResult( intent, ConstsAct.REQUEST_EDIT );
}//EditOperation
//--------------------------------------------------------------------
private void DeleteOperation( int id_operation ){

  String msg = getString( R.string.snack_msg_delete_failed );

  if( Data.instance.db_budget_op.Remove( id_operation ) != -1 ){

    Data.instance.db_user_op.RemoveByBudgetOperation( id_budget, id_operation );
    //Update();

    msg = getString( R.string.snack_msg_delete_success );
  }//if success

  Utils.SnackL( root_view, msg );

  ((Act_BudgetOperations) getActivity()).Update();

}//DeleteOperation
//--------------------------------------------------------------------

}//Frag_TabOpsBudget

