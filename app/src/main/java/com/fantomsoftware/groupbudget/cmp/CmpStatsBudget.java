package com.fantomsoftware.groupbudget.cmp;

import android.content.Context;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.OperationBudget;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.interfaces.OnResultStats;
import com.fantomsoftware.groupbudget.tasks.Task_UpdateStatsBudget;
import com.fantomsoftware.groupbudget.consts.ConstsStats;
import com.fantomsoftware.groupbudget.utils.Utils;


public class CmpStatsBudget extends RelativeLayout{
//---------------------------------------------------------------------------
private LinearLayout ll_list  = null;
private TextView     tv_title = null;
private TextView     tv_btn   = null;

private int id_budget = -1;
private int id_stats  = ConstsStats.STATS_BALANCE;

private OnResultStats on_stats_ready = null;
private OnEvent       on_btn_click   = null;

public static final String sp_key_id_stats = "sp_key_id_stats";
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
public CmpStatsBudget( Context context ){
  super( context );

  if( !isInEditMode() ){
    Init( context, null );
  }
}//CmpStatsBalance
//---------------------------------------------------------------------------
public CmpStatsBudget( Context context, AttributeSet attrs ){
  super( context, attrs );

  if( !isInEditMode() ){
    Init( context, attrs );
  }
}//CmpStatsBalance
//---------------------------------------------------------------------------
public CmpStatsBudget( Context context, AttributeSet attrs, int defStyle ){
  super( context, attrs, defStyle );

  if( !isInEditMode() ){
    Init( context, attrs );
  }
}//CmpStatsBalance
//---------------------------------------------------------------------------
@Override
protected void onLayout( boolean changed, int left, int top, int right, int bottom ){
  super.onLayout( changed, left, top, right, bottom );
}//onLayout
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
private void Init( Context context, AttributeSet attrs ){

  on_stats_ready = new OnResultStats(){
    public void OnResult( SparseArray<OperationBudget> data ){
      UpdateList( data );
    }//OnResultStats
  };//on_stats_ready

  OnClickListener cl_text_btn = new OnClickListener(){
    @Override
    public void onClick( View view ){
      if( on_btn_click == null ){
        return;
      }//if

      on_btn_click.OnEvent();
    }//onClick
  };//cl_text_btn


  View.inflate( context, R.layout.cmp_stats, this );

  ll_list = (LinearLayout) findViewById( R.id.ll_list );
  tv_title = (TextView) findViewById( R.id.tv_title );
  tv_btn = (TextView) findViewById( R.id.tv_btn );

  tv_title.setText( Utils.context.getString( R.string.stats_option_title ) );
  tv_btn.setText( ConstsStats.ToString( ConstsStats.STATS_BALANCE ) );

  tv_btn.setOnClickListener( cl_text_btn );
}//Init
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
public void SetIdBudget( int id_budget ){
  this.id_budget = id_budget;
}//SetIdBudget
//---------------------------------------------------------------------------
public void SetIdStat( int id_stats ){
  this.id_stats = id_stats;
  tv_btn.setText( ConstsStats.ToString( id_stats ) );
}//SetIdStat
//---------------------------------------------------------------------------
public void SetOnBtnClick( OnEvent on_btn_click ){
  this.on_btn_click = on_btn_click;
}//SetOnBtnClick
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
public void Update(){
  Task_UpdateStatsBudget task = new Task_UpdateStatsBudget();
  task.SetData( id_budget, id_stats );
  task.SetOnStatsReady( on_stats_ready );
  task.execute();
}//Update
//---------------------------------------------------------------------------
private void UpdateList( SparseArray<OperationBudget> data ){

  if( data == null ){
    return;
  }//if

  View     view;
  TextView tv_currency;
  TextView tv_amount;

  OperationBudget item;

  String currency;
  String amount;

  ll_list.removeAllViews();

  for( int i = 0; i < data.size(); i++ ){

    item = data.valueAt( i );

    currency = Data.instance.db_currency.GetCodeById( item.id_curr ) + " " +
        Data.instance.db_currency.GetNameById( item.id_curr );

    amount = Utils.FormatFloatToString( item.amount );

    view = ll_list.findViewWithTag( item.id_curr );

    if( view == null ){
      view = View.inflate( getContext(), R.layout.list_item_stats_content, null );
      view.setTag( item.id_curr );
      ll_list.addView( view );
    }//if view == null

    tv_currency = (TextView) view.findViewById( R.id.tv_key );
    tv_amount = (TextView) view.findViewById( R.id.tv_value );
    tv_currency.setText( currency );
    tv_amount.setText( amount );
  }//for
}//UpdateList
//---------------------------------------------------------------------------

}//CmpStatsBalance
