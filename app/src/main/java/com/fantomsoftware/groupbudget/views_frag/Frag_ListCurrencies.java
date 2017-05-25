package com.fantomsoftware.groupbudget.views_frag;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.adapters.RecycleA_Currencies;
import com.fantomsoftware.groupbudget.data.Currency;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.dialogs.DialogPickOptionSingl;
import com.fantomsoftware.groupbudget.interfaces.OnResultPosition;
import com.fantomsoftware.groupbudget.interfaces.OnResultSearchCurrency;
import com.fantomsoftware.groupbudget.tasks.Task_SearchCurrency;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.fantomsoftware.groupbudget.views_act.Act;



public class Frag_ListCurrencies extends Fragment{
   //---------------------------------------------------------------------------
   public static final String TAG = "Frag_ListCurrencies";

   private View root_view;

   private Task_SearchCurrency task = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public View onCreateView( LayoutInflater inf, ViewGroup cntr, Bundle bundle ){
      root_view = inf.inflate( R.layout.frag_list_currencies, cntr, false );
      setHasOptionsMenu( true );
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
   public void onCreateOptionsMenu( Menu menu, MenuInflater inflater ){

      super.onCreateOptionsMenu( menu, inflater );
      inflater.inflate( R.menu.menu_frag_currencies, menu );

      SearchView.OnCloseListener on_close = new SearchView.OnCloseListener(){
         @Override
         public boolean onClose(){
            Search( "" );
            return false;
         }//onClose
      };//on_close

      View.OnClickListener on_click_search = new View.OnClickListener(){
         @Override
         public void onClick( View view ){
            //do nothing
         }//onClick
      };//on_click_search

      SearchView.OnQueryTextListener on_input = new SearchView.OnQueryTextListener(){
         //---------------------------------------------------------------------
         @Override
         public boolean onQueryTextSubmit( String input ){
            //do nothing
            return false;
         }//onQueryTextSubmit
         //---------------------------------------------------------------------
         @Override
         public boolean onQueryTextChange( String input ){
            Search( input );
            return false;
         }//onQueryTextChange
         //---------------------------------------------------------------------
      };//on_input

      MenuItem   menu_item   = menu.findItem( R.id.action_search );
      SearchView search_view = (SearchView) menu_item.getActionView();

      search_view.setQueryHint( getString( R.string.search_hint_currency ) );
      search_view.setOnCloseListener( on_close );
      search_view.setOnSearchClickListener( on_click_search );
      search_view.setOnQueryTextListener( on_input);
   }//onCreateOptionsMenu
   //---------------------------------------------------------------------------
   @Override
   public void onPrepareOptionsMenu( Menu menu ){
      super.onPrepareOptionsMenu( menu );
   }//onPrepareOptionsMenu
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Init(){
      InitViewDefaultCurrency();
      InitListCurrency();
      UpdateDefaultCurrencyCode( Currency.default_currnecy_id );
   }//Init
   //---------------------------------------------------------------------------
   private void InitViewDefaultCurrency(){

      TextView tv = (TextView) root_view.findViewById(
         R.id.tv_btn_change_default_currency );

      View.OnClickListener cl = new View.OnClickListener(){
         @Override
         public void onClick( View v ){
            OnClick_PickCurrencyDef();
         }
      };//cl

      tv.setOnClickListener( cl );
   }//InitViewDefaultCurrency
   //---------------------------------------------------------------------------
   private void InitListCurrency(){
      Search( "" );
   }//InitListCurrency
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Search( String search_str ){

      if( task != null ){
         task.cancel( true );
      }//if task is running


      OnResultSearchCurrency on_result = new OnResultSearchCurrency(){
         @Override
         public void OnResult(
            SparseArray<Currency> currencies_filtered,
            SparseArray<Currency> currencies_used_filtered ){

            RecycleA_Currencies adapter = new RecycleA_Currencies(
               currencies_filtered, currencies_used_filtered );

            RecyclerView.LayoutManager layout_manager =
               new LinearLayoutManager( getContext() );

            RecyclerView rv_list =
               (RecyclerView) root_view.findViewById( R.id.rv_list );

            rv_list.setLayoutManager( layout_manager );
            rv_list.setAdapter( adapter );
         }//OnResult
      };//on_result

      task = new Task_SearchCurrency();
      task.SetData( search_str );
      task.SetOnResult( on_result );
      task.execute();

   }//Search
   //---------------------------------------------------------------------------
   private void UpdateDefaultCurrencyCode( int default_currnecy_id ){

      Data.instance.db_currency.SetDefaultCurrencyId( default_currnecy_id );

      TextView tv_default_currency =
         (TextView) root_view.findViewById( R.id.tv_code_default_currency );

      if( tv_default_currency != null ){
         tv_default_currency.setText( Currency.default_currnecy.code );
      }//if

   }//UpdateDefaultCurrencyCode
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnClick_PickCurrencyDef(){
      String  title   = getString( R.string.dialog_pick_currency_def_title );
      Options options = Data.instance.db_currency.getCurrencyOptions();

      OnResultPosition on_result = new OnResultPosition(){
         @Override
         public void OnResult( int position ){

            Options options     = Data.instance.db_currency.getCurrencyOptions();
            int     id_currency = options.GetIdByPosition( position );

            UpdateDefaultCurrencyCode( id_currency );
         }//OnResult
      };//on_result

      DialogPickOptionSingl dialog = new DialogPickOptionSingl();
      dialog.SetData( title, options );
      dialog.SetOnResult( on_result );
      dialog.show( getFragmentManager(), "TAG" );

   }//OnClick_PickCurrencyDef
   //---------------------------------------------------------------------------

}//Frag_ListCurrencies