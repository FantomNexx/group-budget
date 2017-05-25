package com.fantomsoftware.groupbudget.adapters;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Budget;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;


public class RecycleA_Budgets
    extends RecyclerView.Adapter<RecycleA_Budgets.ViewHolder>{

//-----------------------------------------------------------------------------
private SparseArray<Budget> data;

public final static int TYPE_1LINE  = 1;
public final static int TYPE_2LINEs = 2;

private OnResultId on_item_click      = null;
private OnResultId on_item_long_click = null;
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
public void SetOnListItemClick( OnResultId on_item_click ){
  this.on_item_click = on_item_click;
}//SetOnListItemClick
//---------------------------------------------------------------------------
public void SetOnListItemLongClick( OnResultId on_item_long_click ){
  this.on_item_long_click = on_item_long_click;
}//SetOnListItemLongClick
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
public RecycleA_Budgets( SparseArray<Budget> data ){
  this.data = data;
}//RecycleA_Budgets
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
@Override
public RecycleA_Budgets.ViewHolder onCreateViewHolder(
    ViewGroup parent, int view_type ){

  View.OnClickListener cl_item = new View.OnClickListener(){
    @Override
    public void onClick( View view ){
      int    position = (int) view.getTag();
      Budget item     = data.get( data.keyAt( position ) );

      if( on_item_click != null ){
        on_item_click.OnResult( item.id );
      }//if
    }//onClick
  };//cl_item

  View.OnLongClickListener lcl_item = new View.OnLongClickListener(){
    @Override
    public boolean onLongClick( View view ){
      int    position = (int) view.getTag();
      Budget item     = data.get( data.keyAt( position ) );

      if( on_item_long_click != null ){
        on_item_long_click.OnResult( item.id );
      }//if
      return false;
    }
  };//lcl_item

  int layout_id = R.layout.list_item_text_2lines;
  if( view_type == TYPE_1LINE ){
    layout_id = R.layout.list_item_text_1line;
  }//if

  LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
  View           view     = inflater.inflate( layout_id, null );

  view.setOnClickListener( cl_item );
  view.setOnLongClickListener( lcl_item );


  RelativeLayout rl_view    = (RelativeLayout) view.findViewById( R.id.root_view );
  TextView       tv_name    = (TextView) view.findViewById( R.id.tv_name );
  TextView       tv_comment = (TextView) view.findViewById( R.id.tv_comment );

  return new ViewHolder( rl_view, tv_name, tv_comment );
}//onCreateViewHolder
//---------------------------------------------------------------------------
@Override
public void onBindViewHolder( ViewHolder holder, int position ){
  Budget item = data.get( data.keyAt( position ) );

  holder.root_view.setTag( position );
  holder.tv_name.setText( item.name );

  if( holder.tv_comment != null ){
    holder.tv_comment.setText( item.comment );
  }//if
}//onBindViewHolder
//---------------------------------------------------------------------------
@Override
public int getItemViewType( int position ){
  Budget item = data.get( data.keyAt( position ) );

  if( item.comment.equals( "" ) ){
    return TYPE_1LINE;
  }//if

  return TYPE_2LINEs;
}//getItemViewType
//---------------------------------------------------------------------------
@Override
public int getItemCount(){
  if( data == null ){
    return 0;
  }//if

  return data.size();
}//getItemCount
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
public void Update(){
  notifyDataSetChanged();
}//Update
//---------------------------------------------------------------------------


//---------------------------------------------------------------------------
public static class ViewHolder extends RecyclerView.ViewHolder{
  //------------------------------------------------------------------------
  public RelativeLayout root_view;
  public TextView       tv_name;
  public TextView       tv_comment;
  //------------------------------------------------------------------------
  public ViewHolder( RelativeLayout root_view, TextView tv_name, TextView tv_comment ){

    super( root_view );

    this.root_view = root_view;
    this.tv_name = tv_name;
    this.tv_comment = tv_comment;
  }//ViewHolder
}//ViewHolder
//---------------------------------------------------------------------------

}//RecycleA_Budgets
