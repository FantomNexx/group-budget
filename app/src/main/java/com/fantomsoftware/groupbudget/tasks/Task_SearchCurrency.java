package com.fantomsoftware.groupbudget.tasks;


import android.os.AsyncTask;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.Currency;
import com.fantomsoftware.groupbudget.data.db.DBCurrency;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.interfaces.OnResultSearchCurrency;

/*
AsyncTask
<
1, // init parameters
2, // on upgrade parameters
3, // task return parameter
>
*/
public class Task_SearchCurrency extends AsyncTask<Void, Integer, Void>{
   //---------------------------------------------------------------------------
   private String search_str;

   private SparseArray<Currency> currencies_filtered      = null;
   private SparseArray<Currency> currencies_used_filtered = null;

   private OnResultSearchCurrency on_result = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetData( String search_str ){
      this.search_str = search_str;
   }//SetData
   //---------------------------------------------------------------------------
   public void SetOnResult( OnResultSearchCurrency on_result ){
      this.on_result = on_result;
   }//SetData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected Void doInBackground( Void... param ){

      DBCurrency currency_db     = Data.instance.db_currency;
      SparseArray<Currency> currencies      = currency_db.getCurrencies();
      SparseArray<Currency> currencies_used = currency_db.getCurrenciesUsed();

      currencies_filtered = new SparseArray<>();
      currencies_used_filtered = new SparseArray<>();

      if( search_str.equals( "" ) ){
         currencies_filtered = currencies;
         currencies_used_filtered = currencies_used;
         return null;
      }//if search empty


      boolean  is_match;
      Currency currency;

      for( int i = 0; i < currencies.size(); i++ ){

         if( isCancelled() ){
            currencies_filtered = null;
            currencies_used_filtered = null;
            return null;
         }//if canceled

         currency = currencies.valueAt( i );
         is_match = false;

         if( currency.name.toLowerCase().startsWith( search_str ) ){
            is_match = true;
         }else if( currency.code.toLowerCase().startsWith( search_str ) ){
            is_match = true;
         }else if( currency.country.toLowerCase().startsWith( search_str ) ){
            is_match = true;
         }//if

         if( !is_match ){
            continue;
         }//if

         currencies_filtered.put( currency.id, currency );

         if( currency.is_used == Currency.USED ){
            currencies_used_filtered.put( currency.id, currency );
         }//if
      }//for

      return null;
   }//doInBackground
   //---------------------------------------------------------------------------
   protected void onProgressUpdate( Integer... progress ){
   }//onProgressUpdate
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected void onPreExecute(){
      super.onPreExecute();
   }//onPreExecute
   //---------------------------------------------------------------------------
   @Override
   protected void onPostExecute( Void result ){
      if( on_result != null && !isCancelled()){
         on_result.OnResult( currencies_filtered, currencies_used_filtered );
      }//if
   }//onPostExecute
   //---------------------------------------------------------------------------

}//Task_SearchCurrency