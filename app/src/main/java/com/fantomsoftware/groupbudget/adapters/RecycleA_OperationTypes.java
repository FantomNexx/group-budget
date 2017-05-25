package com.fantomsoftware.groupbudget.adapters;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.interfaces.OnResultPosition;



public class RecycleA_OperationTypes
   extends RecyclerView.Adapter<RecycleA_OperationTypes.ViewHolder>{

   //---------------------------------------------------------------------------
   private Options data;

   private OnResultPosition on_item_click = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetOnListItemClick( OnResultPosition on_item_click ){
      this.on_item_click = on_item_click;
   }//SetOnListItemClick
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public RecycleA_OperationTypes( Options data ){
      this.data = data;
   }//RecycleA_Currencies
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public RecycleA_OperationTypes.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType ){

      View.OnClickListener cl_item = new View.OnClickListener(){

         @Override
         public void onClick( View view ){

            if( on_item_click != null ){
               on_item_click.OnResult( (int) view.getTag() );
            }//if

         }//onClick
      };//cl_item


      LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
      View           view     = inflater.inflate( R.layout.list_item_op_type, null );

      view.setOnClickListener( cl_item );


      RelativeLayout rl_view     = (RelativeLayout) view.findViewById( R.id.root_view );
      RadioButton    rb_selected = (RadioButton) view.findViewById( R.id.rb_selected );
      TextView       tv_title    = (TextView) view.findViewById( R.id.tv_title );
      TextView       tv_descr    = (TextView) view.findViewById( R.id.tv_descr );

      return new ViewHolder( rl_view, rb_selected, tv_title, tv_descr );
   }//onCreateViewHolder
   //---------------------------------------------------------------------------
   @Override
   public void onBindViewHolder( ViewHolder holder, int position ){

      Options.Option item = data.GetByPosition( position );

      holder.root_view.setTag( position );

      holder.rb_selected.setChecked( item.is_selected );

      holder.tv_title.setText( item.string_value );
      holder.tv_descr.setText( item.string_value_descr );

   }//onBindViewHolder
   //---------------------------------------------------------------------------
   @Override
   public int getItemCount(){

      if( data == null ){
         return 0;
      }//if

      return data.list_options.size();
   }//getItemCount
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static class ViewHolder extends RecyclerView.ViewHolder{
      //------------------------------------------------------------------------
      public RelativeLayout root_view;
      public RadioButton    rb_selected;
      public TextView       tv_title;
      public TextView       tv_descr;
      //------------------------------------------------------------------------


      //------------------------------------------------------------------------
      public ViewHolder(
         RelativeLayout root_view,
         RadioButton rb_selected,
         TextView tv_title,
         TextView tv_descr ){

         super( root_view );

         this.root_view = root_view;
         this.rb_selected = rb_selected;
         this.tv_title = tv_title;
         this.tv_descr = tv_descr;

      }//ViewHolder
      //------------------------------------------------------------------------

   }//ViewHolder
   //---------------------------------------------------------------------------

}//RecycleA_Users
