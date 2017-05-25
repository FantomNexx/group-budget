package com.fantomsoftware.groupbudget.adapters;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.User;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;



public class RecycleA_UserNames
   extends RecyclerView.Adapter<RecycleA_UserNames.ViewHolder>{

   //---------------------------------------------------------------------------
   private SparseArray<User> data;

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
   public RecycleA_UserNames( SparseArray<User> data ){
      this.data = data;
   }//RecycleA_Currencies
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public RecycleA_UserNames.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType ){

      View.OnClickListener cl_item = new View.OnClickListener(){
         @Override
         public void onClick( View view ){
            int  position = (int) view.getTag();
            User item     = data.get( data.keyAt( position ) );

            if( on_item_click != null ){
               on_item_click.OnResult( item.id );
            }//if
         }//onClick
      };//cl_item

      View.OnLongClickListener lcl_item = new View.OnLongClickListener(){
         @Override
         public boolean onLongClick( View view ){
            int  position = (int) view.getTag();
            User item     = data.get( data.keyAt( position ) );

            if( on_item_long_click != null ){
               on_item_long_click.OnResult( item.id );
            }//if
            return false;
         }
      };//lcl_item


      LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
      View           view     = inflater.inflate( R.layout.list_item_user_name, null );

      view.setOnClickListener( cl_item );
      view.setOnLongClickListener( lcl_item );


      RelativeLayout rl_view = (RelativeLayout) view.findViewById( R.id.root_view );
      TextView       tv_name = (TextView) view.findViewById( R.id.tv_name );

      return new ViewHolder( rl_view, tv_name );
   }//onCreateViewHolder
   //---------------------------------------------------------------------------
   @Override
   public void onBindViewHolder( ViewHolder holder, int position ){
      User item = data.get( data.keyAt( position ) );

      holder.root_view.setTag( position );
      holder.tv_name.setText( item.name );
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
      //------------------------------------------------------------------------
      public ViewHolder( RelativeLayout root_view, TextView tv_name ){

         super( root_view );

         this.root_view = root_view;
         this.tv_name = tv_name;
      }//ViewHolder
   }//ViewHolder
   //---------------------------------------------------------------------------

}//RecycleA_UserNames
