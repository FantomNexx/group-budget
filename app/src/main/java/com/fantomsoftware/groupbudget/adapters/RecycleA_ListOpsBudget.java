package com.fantomsoftware.groupbudget.adapters;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.Operation;
import com.fantomsoftware.groupbudget.data.OperationBudget;
import com.fantomsoftware.groupbudget.data.OperationUser;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;
import com.fantomsoftware.groupbudget.utils.Utils;



public class RecycleA_ListOpsBudget
   extends RecyclerView.Adapter<RecycleA_ListOpsBudget.ViewHolder>{

   //---------------------------------------------------------------------------
   private SparseArray<OperationBudget> data;

   private OnResultId on_item_click = null;
   private OnResultId on_item_long_click = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetOnListItemClick( OnResultId on_item_click  ){
      this.on_item_click = on_item_click;
   }//SetOnListItemClick
   //---------------------------------------------------------------------------
   public void SetOnListItemLongClick(OnResultId on_item_long_click ){
      this.on_item_long_click = on_item_long_click;
   }//SetOnListItemLongClick
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public RecycleA_ListOpsBudget( SparseArray<OperationBudget> data ){
      this.data = data;
   }//RecycleA_Budgets
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public RecycleA_ListOpsBudget.ViewHolder onCreateViewHolder(
      ViewGroup parent, int view_type ){

      View.OnClickListener cl_item = new View.OnClickListener(){
         @Override
         public void onClick( View view ){
            int             position = (int) view.getTag();
            OperationBudget item     = data.get( data.keyAt( position ) );

            if( on_item_click != null ){
               on_item_click.OnResult( item.id );
            }//if
         }//onClick
      };//cl_item

      View.OnLongClickListener lcl_item = new View.OnLongClickListener(){
         @Override
         public boolean onLongClick( View view ){
            int             position = (int) view.getTag();
            OperationBudget item     = data.get( data.keyAt( position ) );

            if( on_item_long_click != null ){
               on_item_long_click.OnResult( item.id );
            }//if
            return false;
         }
      };//lcl_item


      int layout_id = R.layout.list_item_op_budget;

      LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
      View           view     = inflater.inflate( layout_id, null );

      view.setOnClickListener( cl_item );
      view.setOnLongClickListener( lcl_item );


      RelativeLayout rl_view    = (RelativeLayout) view.findViewById( R.id.root_view );
      TextView       tv_name    = (TextView) view.findViewById( R.id.tv_name );
      TextView       tv_date    = (TextView) view.findViewById( R.id.tv_date );
      TextView       tv_sum     = (TextView) view.findViewById( R.id.tv_sum );
      TextView       tv_users   = (TextView) view.findViewById( R.id.tv_users );
      TextView       tv_comment = (TextView) view.findViewById( R.id.tv_comment );

      return new ViewHolder( rl_view, tv_name, tv_date, tv_sum, tv_users, tv_comment );
   }//onCreateViewHolder
   //---------------------------------------------------------------------------
   @Override
   public void onBindViewHolder( ViewHolder holder, int position ){
      OperationBudget item = data.get( data.keyAt( position ) );

      String        currency_code = Data.instance.db_currency.GetCodeById( item.id_curr );
      String        date          = Utils.FormatLongDateToStringFull( item.date_created );
      String        amount        = Utils.FormatFloatToString( item.amount );
      String        sum           = amount + " " + currency_code;
      String        users         = "";
      String        user_name     = "";
      OperationUser operation     = null;


      SparseArray<OperationUser> operations =
         Data.instance.db_user_op.GetOperations( item.id );

      if( operations.size() == 0 ){
         return;
      }//if


      if( item.type == Operation.TYPE_IN ){
         sum = "+" + sum;
      }//if


      for( int i = 0; i < operations.size(); i++ ){
         operation = operations.valueAt( i );
         user_name = Data.instance.db_user.GetName( operation.id_user );
         users += user_name + ", ";
      }//for

      users = users.substring( 0, users.length() - 2 );

      holder.root_view.setTag( position );
      holder.tv_name.setText( item.name );
      holder.tv_date.setText( date );
      holder.tv_sum.setText( sum );
      holder.tv_users.setText( users );
      holder.tv_comment.setText( item.comment );

      if( item.comment.equals( "" ) ){
         holder.tv_comment.setVisibility( View.GONE );
      }else{
         holder.tv_comment.setVisibility( View.VISIBLE );
      }//if

   }//onBindViewHolder
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
      public TextView       tv_date;
      public TextView       tv_sum;
      public TextView       tv_users;
      public TextView       tv_comment;
      //------------------------------------------------------------------------
      public ViewHolder(
         RelativeLayout root_view,
         TextView tv_name,
         TextView tv_date,
         TextView tv_sum,
         TextView tv_users,
         TextView tv_comment ){

         super( root_view );

         this.root_view = root_view;
         this.tv_name = tv_name;
         this.tv_date = tv_date;
         this.tv_sum = tv_sum;
         this.tv_users = tv_users;
         this.tv_comment = tv_comment;
      }//ViewHolder
   }//ViewHolder
   //---------------------------------------------------------------------------

}//RecycleA_ListOpsBudget
