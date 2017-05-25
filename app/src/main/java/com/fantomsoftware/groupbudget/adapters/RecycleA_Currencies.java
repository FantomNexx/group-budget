package com.fantomsoftware.groupbudget.adapters;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Currency;
import com.fantomsoftware.groupbudget.data.db.DBCurrency;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.tasks.Task_UpdateCurrencyUsage;



public class RecycleA_Currencies
   extends RecyclerView.Adapter<RecycleA_Currencies.ViewHolder>{

   //---------------------------------------------------------------------------
   private SparseArray<Currency> data;
   private SparseArray<Currency> data_selected;

   private ViewGroup root_view;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public RecycleA_Currencies( SparseArray<Currency> data, SparseArray<Currency> data_selected ){
      this.data = data;
      this.data_selected = data_selected;
   }//RecycleA_Currencies
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public RecycleA_Currencies.ViewHolder onCreateViewHolder(
      ViewGroup parent, int viewType ){

      root_view = parent;

      final View.OnClickListener cl_item = new View.OnClickListener(){
         @Override
         public void onClick( View view ){
            onListItemClick( view );
         }//onClick
      };//cl_item


      LayoutInflater inflater = LayoutInflater.from( parent.getContext() );
      View           view     = inflater.inflate( R.layout.list_item_currency, null );

      view.setOnClickListener( cl_item );

      RelativeLayout root_view  = (RelativeLayout) view.findViewById( R.id.root_view );
      CheckBox       cb_used    = (CheckBox) view.findViewById( R.id.cb_used );
      TextView       tv_code    = (TextView) view.findViewById( R.id.tv_code );
      TextView       tv_name    = (TextView) view.findViewById( R.id.tv_name );
      TextView       tv_country = (TextView) view.findViewById( R.id.tv_country );

      return new ViewHolder( root_view, cb_used, tv_code, tv_name, tv_country );
   }//RecycleA_Currencies
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void onListItemClick( View view ){

      int        id                    = (int) view.getTag();
      CheckBox   cb                    = (CheckBox) view.findViewById( R.id.cb_used );
      boolean    is_used               = !cb.isChecked();
      DBCurrency currency_db           = Data.instance.db_currency;
      int        used_currencies_count = currency_db.getCurrenciesUsed().size();


      if( used_currencies_count <= 1 && !is_used ){
         String msg = "Mops needs at least one currecny.";
         Snackbar.make( root_view, msg, Snackbar.LENGTH_LONG ).show();
         return;
      }//if


      SparseArray<Currency> currencies = currency_db.getCurrencies();
      Currency              c          = currencies.get( id );


      if( c.id == Currency.default_currnecy_id ){
         String msg = c.code + " is a default currency. It cannot be disabled.";
         Snackbar.make( root_view, msg, Snackbar.LENGTH_LONG ).show();
         return;
      }//if

      cb.setChecked( is_used );

      Task_UpdateCurrencyUsage task = new Task_UpdateCurrencyUsage();
      task.SetData( id, is_used );
      task.execute( "" );
   }//onListItemClick
   //---------------------------------------------------------------------------
   @Override
   public void onBindViewHolder( ViewHolder holder, int position ){

      Currency currency = data.get( data.keyAt( position ) );

      holder.root_view.setTag( currency.id );

      if( data_selected.get( currency.id ) != null ){
         holder.cb_checkbox.setChecked( true );
      }else{
         holder.cb_checkbox.setChecked( false );
      }//if

      holder.tv_code.setText( currency.code );
      holder.tv_name.setText( currency.name );
      holder.tv_country.setText( currency.country );
   }//onBindViewHolder
   //---------------------------------------------------------------------------


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
   public static class ViewHolder extends RecyclerView.ViewHolder{
      // each data item is just a string in this case
      public RelativeLayout root_view;
      public CheckBox       cb_checkbox;
      public TextView       tv_code;
      public TextView       tv_name;
      public TextView       tv_country;

      public ViewHolder(
         RelativeLayout root_view,
         CheckBox cb_checkbox,
         TextView tv_code,
         TextView tv_name,
         TextView tv_country ){

         super( root_view );

         this.root_view = root_view;
         this.cb_checkbox = cb_checkbox;
         this.tv_code = tv_code;
         this.tv_name = tv_name;
         this.tv_country = tv_country;
      }
   }//class ViewHolder
   //---------------------------------------------------------------------------

}//RecycleA_Currencies