package com.fantomsoftware.groupbudget.data.db;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.SparseArray;

import com.fantomsoftware.groupbudget.data.Currency;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.utils.Utils;

import java.util.ArrayList;
import java.util.List;


public class DBCurrency{
   //---------------------------------------------------------------------------
   private DBHelper db_helper;
   private String tablename = "currencies";

   private SparseArray<Currency> currencies       = null;
   private SparseArray<Currency> currencies_used  = null;
   private Options               currency_options = null;

   public SparseArray<Currency> getCurrencies(){
      return currencies;
   }//getCurrencies
   public SparseArray<Currency> getCurrenciesUsed(){
      return currencies_used;
   }//getCurrenciesUsed
   public Options getCurrencyOptions(){
      return currency_options;
   }//getCurrencyOptions
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public DBCurrency( DBHelper db_helper ){
      this.db_helper = db_helper;
      //Clear();
      Init();
   }//DBCurrency
   //---------------------------------------------------------------------------
   private boolean Init(){

      if( !db_helper.IsTableExists( tablename ) ){
         CreateTable();
      }//if not exist

      if( db_helper.IsTableEmpty( tablename ) ){
         SetManually();
      }//if empty

      RefreshCurrencies();
      RefreshCurrenciesUsed();
      RefreshCurrencyOptions();
      RefreshDefaultCurrency();

      return true;
   }//Init
   //---------------------------------------------------------------------------
   private void CreateTable(){

      String query = "create table " + tablename
         + " ("
         + "id integer primary key autoincrement,"
         + "name text,"
         + "country text,"
         + "code text,"
         + "is_used integer"
         + ");";

      if( !db_helper.ExecuteSQL( query )  ){
         return;
      }//if

   }//CreateTable
   //---------------------------------------------------------------------------
   private void SetManually(){

      List<Currency> items = new ArrayList<Currency>();

      items.add( new Currency( 0, "USD", "United States", "Dollar", 1 ) );
      items.add( new Currency( 0, "EUR", "Euro Member Countries", "Euro", 1 ) );
      items.add( new Currency( 0, "UAH", "Ukraine", "Hryvnia", 1 ) );

      items.add( new Currency( 0, "AED", "United Arab Emirates", "Dirham", 0 ) );
      items.add( new Currency( 0, "AFN", "Afghanistan", "Afghani", 0 ) );
      items.add( new Currency( 0, "ALL", "Albania", "Lek", 0 ) );
      items.add( new Currency( 0, "AMD", "Armenia", "Dram", 0 ) );
      items.add( new Currency( 0, "ANG", "Netherlands Antilles", "Guilder", 0 ) );
      items.add( new Currency( 0, "AOA", "Angola", "Kwanza", 0 ) );
      items.add( new Currency( 0, "ARS", "Argentina", "Peso", 0 ) );
      items.add( new Currency( 0, "AUD", "Australia", "Dollar", 0 ) );
      items.add( new Currency( 0, "AWG", "Aruba", "Guilder", 0 ) );
      items.add( new Currency( 0, "AZN", "Azerbaijan", "New Manat", 0 ) );
      items.add( new Currency( 0, "BAM", "Bosnia and Herzegovina", "Convertible Marka", 0 ) );
      items.add( new Currency( 0, "BBD", "Barbados", "Dollar", 0 ) );
      items.add( new Currency( 0, "BDT", "Bangladesh", "Taka", 0 ) );
      items.add( new Currency( 0, "BGN", "Bulgaria", "Lev", 0 ) );
      items.add( new Currency( 0, "BHD", "Bahrain", "Dinar", 0 ) );
      items.add( new Currency( 0, "BIF", "Burundi", "Franc", 0 ) );
      items.add( new Currency( 0, "BMD", "Bermuda", "Dollar", 0 ) );
      items.add( new Currency( 0, "BND", "Brunei Darussalam", "Dollar", 0 ) );
      items.add( new Currency( 0, "BOB", "Bolivia", "Boliviano", 0 ) );
      items.add( new Currency( 0, "BRL", "Brazil", "Real", 0 ) );
      items.add( new Currency( 0, "BSD", "Bahamas", "Dollar", 0 ) );
      items.add( new Currency( 0, "BTN", "Bhutan", "Ngultrum", 0 ) );
      items.add( new Currency( 0, "BWP", "Botswana", "Pula", 0 ) );
      items.add( new Currency( 0, "BYR", "Belarus", "Ruble", 0 ) );
      items.add( new Currency( 0, "BZD", "Belize", "Dollar", 0 ) );
      items.add( new Currency( 0, "CAD", "Canada", "Dollar", 0 ) );
      items.add( new Currency( 0, "CDF", "Congo/Kinshasa", "Franc", 0 ) );
      items.add( new Currency( 0, "CHF", "Switzerland", "Franc", 0 ) );
      items.add( new Currency( 0, "CLP", "Chile", "Peso", 0 ) );
      items.add( new Currency( 0, "CNY", "China Yuan", "Renminbi", 0 ) );
      items.add( new Currency( 0, "COP", "Colombia", "Peso", 0 ) );
      items.add( new Currency( 0, "CRC", "Costa Rica", "Colon", 0 ) );
      items.add( new Currency( 0, "CUC", "Cuba Convertible", "Peso", 0 ) );
      items.add( new Currency( 0, "CUP", "Cuba", "Peso", 0 ) );
      items.add( new Currency( 0, "CVE", "Cape Verde", "Escudo", 0 ) );
      items.add( new Currency( 0, "CZK", "Czech Republic", "Koruna", 0 ) );
      items.add( new Currency( 0, "DJF", "Djibouti", "Franc", 0 ) );
      items.add( new Currency( 0, "DKK", "Denmark", "Krone", 0 ) );
      items.add( new Currency( 0, "DOP", "Dominican Republic", "Peso", 0 ) );
      items.add( new Currency( 0, "DZD", "Algeria", "Dinar", 0 ) );
      items.add( new Currency( 0, "EGP", "Egypt", "Pound", 0 ) );
      items.add( new Currency( 0, "ERN", "Eritrea", "Nakfa", 0 ) );
      items.add( new Currency( 0, "ETB", "Ethiopia", "Birr", 0 ) );
      items.add( new Currency( 0, "FJD", "Fiji", "Dollar", 0 ) );
      items.add( new Currency( 0, "FKP", "Falkland Islands", "Pound", 0 ) );
      items.add( new Currency( 0, "GBP", "United Kingdom", "Pound", 0 ) );
      items.add( new Currency( 0, "GEL", "Georgia", "Lari", 0 ) );
      items.add( new Currency( 0, "GGP", "Guernsey", "Pound", 0 ) );
      items.add( new Currency( 0, "GHS", "Ghana", "Cedi", 0 ) );
      items.add( new Currency( 0, "GIP", "Gibraltar", "Pound", 0 ) );
      items.add( new Currency( 0, "GMD", "Gambia", "Dalasi", 0 ) );
      items.add( new Currency( 0, "GNF", "Guinea", "Franc", 0 ) );
      items.add( new Currency( 0, "GTQ", "Guatemala", "Quetzal", 0 ) );
      items.add( new Currency( 0, "GYD", "Guyana", "Dollar", 0 ) );
      items.add( new Currency( 0, "HKD", "Hong Kong", "Dollar", 0 ) );
      items.add( new Currency( 0, "HNL", "Honduras", "Lempira", 0 ) );
      items.add( new Currency( 0, "HRK", "Croatia", "Kuna", 0 ) );
      items.add( new Currency( 0, "HTG", "Haiti", "Gourde", 0 ) );
      items.add( new Currency( 0, "HUF", "Hungary", "Forint", 0 ) );
      items.add( new Currency( 0, "IDR", "Indonesia", "Rupiah", 0 ) );
      items.add( new Currency( 0, "ILS", "Israel", "Shekel", 0 ) );
      items.add( new Currency( 0, "IMP", "Isle of Man", "Pound", 0 ) );
      items.add( new Currency( 0, "INR", "India", "Rupee", 0 ) );
      items.add( new Currency( 0, "IQD", "Iraq", "Dinar", 0 ) );
      items.add( new Currency( 0, "IRR", "Iran", "Rial", 0 ) );
      items.add( new Currency( 0, "ISK", "Iceland", "Krona", 0 ) );
      items.add( new Currency( 0, "JEP", "Jersey", "Pound", 0 ) );
      items.add( new Currency( 0, "JMD", "Jamaica", "Dollar", 0 ) );
      items.add( new Currency( 0, "JOD", "Jordan", "Dinar", 0 ) );
      items.add( new Currency( 0, "JPY", "Japan", "Yen", 0 ) );
      items.add( new Currency( 0, "KES", "Kenya", "Shilling", 0 ) );
      items.add( new Currency( 0, "KGS", "Kyrgyzstan", "Som", 0 ) );
      items.add( new Currency( 0, "KHR", "Cambodia", "Riel", 0 ) );
      items.add( new Currency( 0, "KMF", "Comoros", "Franc", 0 ) );
      items.add( new Currency( 0, "KPW", "Korea (North)", "Won", 0 ) );
      items.add( new Currency( 0, "KRW", "Korea (South)", "Won", 0 ) );
      items.add( new Currency( 0, "KWD", "Kuwait", "Dinar", 0 ) );
      items.add( new Currency( 0, "KYD", "Cayman Islands", "Dollar", 0 ) );
      items.add( new Currency( 0, "KZT", "Kazakhstan", "Tenge", 0 ) );
      items.add( new Currency( 0, "LAK", "Laos", "Kip", 0 ) );
      items.add( new Currency( 0, "LBP", "Lebanon", "Pound", 0 ) );
      items.add( new Currency( 0, "LKR", "Sri Lanka", "Rupee", 0 ) );
      items.add( new Currency( 0, "LRD", "Liberia", "Dollar", 0 ) );
      items.add( new Currency( 0, "LSL", "Lesotho", "Loti", 0 ) );
      items.add( new Currency( 0, "LTL", "Lithuania", "Litas", 0 ) );
      items.add( new Currency( 0, "LYD", "Libya", "Dinar", 0 ) );
      items.add( new Currency( 0, "MAD", "Morocco", "Dirham", 0 ) );
      items.add( new Currency( 0, "MDL", "Moldova", "Leu", 0 ) );
      items.add( new Currency( 0, "MGA", "Madagascar", "Ariary", 0 ) );
      items.add( new Currency( 0, "MKD", "Macedonia", "Denar", 0 ) );
      items.add( new Currency( 0, "MMK", "Myanmar (Burma)", "Kyat", 0 ) );
      items.add( new Currency( 0, "MNT", "Mongolia", "Tughrik", 0 ) );
      items.add( new Currency( 0, "MOP", "Macau", "Pataca", 0 ) );
      items.add( new Currency( 0, "MRO", "Mauritania", "Ouguiya", 0 ) );
      items.add( new Currency( 0, "MUR", "Mauritius", "Rupee", 0 ) );
      items.add( new Currency( 0, "MVR", "Maldives (Maldive Islands)", "Rufiyaa", 0 ) );
      items.add( new Currency( 0, "MWK", "Malawi", "Kwacha", 0 ) );
      items.add( new Currency( 0, "MXN", "Mexico", "Peso", 0 ) );
      items.add( new Currency( 0, "MYR", "Malaysia", "Ringgit", 0 ) );
      items.add( new Currency( 0, "MZN", "Mozambique", "Metical", 0 ) );
      items.add( new Currency( 0, "NAD", "Namibia", "Dollar", 0 ) );
      items.add( new Currency( 0, "NGN", "Nigeria", "Naira", 0 ) );
      items.add( new Currency( 0, "NIO", "Nicaragua", "Cordoba", 0 ) );
      items.add( new Currency( 0, "NOK", "Norway", "Krone", 0 ) );
      items.add( new Currency( 0, "NPR", "Nepal", "Rupee", 0 ) );
      items.add( new Currency( 0, "NZD", "New Zealand", "Dollar", 0 ) );
      items.add( new Currency( 0, "OMR", "Oman", "Rial", 0 ) );
      items.add( new Currency( 0, "PAB", "Panama", "Balboa", 0 ) );
      items.add( new Currency( 0, "PEN", "Peru Nuevo", "Sol", 0 ) );
      items.add( new Currency( 0, "PGK", "Papua New Guinea", "Kina", 0 ) );
      items.add( new Currency( 0, "PHP", "Philippines", "Peso", 0 ) );
      items.add( new Currency( 0, "PKR", "Pakistan", "Rupee", 0 ) );
      items.add( new Currency( 0, "PLN", "Poland", "Zloty", 0 ) );
      items.add( new Currency( 0, "PYG", "Paraguay", "Guarani", 0 ) );
      items.add( new Currency( 0, "QAR", "Qatar", "Riyal", 0 ) );
      items.add( new Currency( 0, "RON", "Romania New", "Leu", 0 ) );
      items.add( new Currency( 0, "RSD", "Serbia", "Dinar", 0 ) );
      items.add( new Currency( 0, "RUB", "Russia", "Ruble", 0 ) );
      items.add( new Currency( 0, "RWF", "Rwanda", "Franc", 0 ) );
      items.add( new Currency( 0, "SAR", "Saudi Arabia", "Riyal", 0 ) );
      items.add( new Currency( 0, "SBD", "Solomon Islands", "Dollar", 0 ) );
      items.add( new Currency( 0, "SCR", "Seychelles", "Rupee", 0 ) );
      items.add( new Currency( 0, "SDG", "Sudan", "Pound", 0 ) );
      items.add( new Currency( 0, "SEK", "Sweden", "Krona", 0 ) );
      items.add( new Currency( 0, "SGD", "Singapore", "Dollar", 0 ) );
      items.add( new Currency( 0, "SHP", "Saint Helena", "Pound", 0 ) );
      items.add( new Currency( 0, "SLL", "Sierra Leone", "Leone", 0 ) );
      items.add( new Currency( 0, "SOS", "Somalia", "Shilling", 0 ) );
      items.add( new Currency( 0, "SPL", "Seborga", "Luigino", 0 ) );
      items.add( new Currency( 0, "SRD", "Suriname", "Dollar", 0 ) );
      items.add( new Currency( 0, "STD", "Sao Tome and Principe", "Dobra", 0 ) );
      items.add( new Currency( 0, "SVC", "El Salvador", "Colon", 0 ) );
      items.add( new Currency( 0, "SYP", "Syria", "Pound", 0 ) );
      items.add( new Currency( 0, "SZL", "Swaziland", "Lilangeni", 0 ) );
      items.add( new Currency( 0, "THB", "Thailand", "Baht", 0 ) );
      items.add( new Currency( 0, "TJS", "Tajikistan", "Somoni", 0 ) );
      items.add( new Currency( 0, "TMT", "Turkmenistan", "Manat", 0 ) );
      items.add( new Currency( 0, "TND", "Tunisia", "Dinar", 0 ) );
      items.add( new Currency( 0, "TOP", "Tonga", "Pa'anga", 0 ) );
      items.add( new Currency( 0, "TRY", "Turkey", "Lira", 0 ) );
      items.add( new Currency( 0, "TTD", "Trinidad and Tobago", "Dollar", 0 ) );
      items.add( new Currency( 0, "TVD", "Tuvalu", "Dollar", 0 ) );
      items.add( new Currency( 0, "TWD", "Taiwan New", "Dollar", 0 ) );
      items.add( new Currency( 0, "TZS", "Tanzania", "Shilling", 0 ) );
      items.add( new Currency( 0, "UGX", "Uganda", "Shilling", 0 ) );
      items.add( new Currency( 0, "UYU", "Uruguay", "Peso", 0 ) );
      items.add( new Currency( 0, "UZS", "Uzbekistan", "Som", 0 ) );
      items.add( new Currency( 0, "VEF", "Venezuela", "Bolivar", 0 ) );
      items.add( new Currency( 0, "VND", "Viet Nam", "Dong", 0 ) );
      items.add( new Currency( 0, "VUV", "Vanuatu", "Vatu", 0 ) );
      items.add( new Currency( 0, "WST", "Samoa", "Tala", 0 ) );
      items.add( new Currency( 0, "XAF", "Communaute Financiere Africaine", "Franc", 0 ) );
      items.add( new Currency( 0, "XCD", "East Caribbean", "Dollar", 0 ) );
      items.add( new Currency( 0, "XDR", "International Monetary Fund", "(IMF)", 0 ) );
      items.add( new Currency( 0, "XOF", "Communaute Financiere Africaine", "Franc", 0 ) );
      items.add( new Currency( 0, "XPF", "Comptoirs Francais du Pacifique", "Franc", 0 ) );
      items.add( new Currency( 0, "YER", "Yemen", "Rial", 0 ) );
      items.add( new Currency( 0, "ZAR", "South Africa", "Rand", 0 ) );
      items.add( new Currency( 0, "ZMW", "Zambia", "Kwacha", 0 ) );
      items.add( new Currency( 0, "ZWD", "Zimbabwe", "Dollar", 0 ) );

      AddMultiple( items );

   }//SetManually
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public long Add( Currency item ){

      ContentValues cv = new ContentValues();

      cv.put( "name", item.name );
      cv.put( "country", item.country );
      cv.put( "code", item.code );
      cv.put( "is_used", item.is_used );

      return db_helper.AddRow( tablename, cv );
   }//Add
   //---------------------------------------------------------------------------
   public boolean AddMultiple( List<Currency> items ){
      db_helper.Connect();

      String sql = "INSERT INTO " + tablename +
         " (code, country, name, is_used )" +
         " VALUES (?, ?, ?, ? )";

      SQLiteDatabase db = db_helper.getDb();

      db.beginTransaction();

      SQLiteStatement stmt = db.compileStatement( sql );

      for( Currency currency : items ){
         stmt.bindString( 1, currency.code );
         stmt.bindString( 2, currency.country );
         stmt.bindString( 3, currency.name );
         stmt.bindLong( 4, currency.is_used );
         stmt.execute();
         stmt.clearBindings();
      }

      db.setTransactionSuccessful();
      db.endTransaction();

      db_helper.Close();

      return true;
   }//AddMultiple
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public boolean UpdateUsage( int id, boolean is_used ){
      if( is_used ){
         return UpdateUsage( id, Currency.USED );
      }else{
         return UpdateUsage( id, Currency.NOT_USED );
      }
   }//UpdateUsage BOOLEAN
   //---------------------------------------------------------------------------
   public boolean UpdateUsage( int id, int is_used ){

      if( is_used == Currency.USED ){
         Currency currency = currencies.get( id );
         currencies_used.append( currency.id, currency );
      }else{
         currencies_used.delete( id );
      }//if

      currencies.get( id ).is_used = is_used;

      String query = "UPDATE " + tablename
         + " SET is_used = " + is_used
         + " WHERE id = " + id;

      boolean result = db_helper.ExecuteSQL( query );
      db_helper.Close();

      RefreshCurrencyOptions();

      return result;
   }//UpdateUsage INT
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void RefreshCurrencies(){
      currencies = new SparseArray<>();
      Cursor cursor = db_helper.GetWholeTable( tablename );

      if( cursor.moveToFirst() ){
         int col_id = cursor.getColumnIndex( "id" );
         int col_code = cursor.getColumnIndex( "code" );
         int col_country = cursor.getColumnIndex( "country" );
         int col_name = cursor.getColumnIndex( "name" );
         int col_is_used = cursor.getColumnIndex( "is_used" );

         do{
            currencies.put(
               cursor.getInt( col_id ),
               new Currency(
                  cursor.getInt( col_id ),
                  cursor.getString( col_code ),
                  cursor.getString( col_country ),
                  cursor.getString( col_name ),
                  cursor.getInt( col_is_used )
               )
            );

         }while( cursor.moveToNext() );
      }

      cursor.close();

   }//RefreshCurrencies
   //---------------------------------------------------------------------------
   public void RefreshCurrenciesUsed(){

      currencies_used = new SparseArray<>();

      Currency currency;

      for( int i = 0; i < currencies.size(); i++ ){
         currency = currencies.get( currencies.keyAt( i ) );

         if( currency.is_used == Currency.USED ){
            currencies_used.put( currency.id, currency );
         }//if
      }//for

   }//RefreshCurrenciesUsed
   //---------------------------------------------------------------------------
   public void RefreshCurrencyOptions(){

      currency_options = new Options();

      Currency currency;
      for( int i = 0; i < currencies_used.size(); i++ ){
         currency = currencies_used.get( currencies_used.keyAt( i ) );
         currency_options.Add( currency.id, currency.code );
      }//for

      if( Currency.default_currnecy != null ){
         currency_options.SetSelectedOption( Currency.default_currnecy.id );
      }//if

   }//RefreshCurrencyOptions
   //---------------------------------------------------------------------------
   private void RefreshDefaultCurrency(){

      Currency.default_currnecy_id =
         Utils.GetSharedPreference( Currency.KEY_DEFAULT_CURRENCY );

      if( Currency.default_currnecy_id == -1 ){
         Currency.default_currnecy = currencies_used.valueAt( 0 );
         Utils.SetSharedPreference(
            Currency.KEY_DEFAULT_CURRENCY,
            Currency.default_currnecy.id );
      }else{
         Currency.default_currnecy = currencies.get( Currency.default_currnecy_id );
         UpdateUsage( Currency.default_currnecy_id, Currency.USED );
      }

      currency_options.SetSelectedOption( Currency.default_currnecy_id );

   }//RefreshDefaultCurrency
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetDefaultCurrencyId( int id ){
      Currency.default_currnecy_id = id;

      Currency.default_currnecy = currencies.get( Currency.default_currnecy_id );
      UpdateUsage( Currency.default_currnecy_id, Currency.USED );

      Utils.SetSharedPreference(
         Currency.KEY_DEFAULT_CURRENCY,
         Currency.default_currnecy_id );
   }//SetDefaultCurrencyId
   //---------------------------------------------------------------------------
   public String GetCodeById( int id ){
      Currency currency = currencies.get( id );

      if( currency == null ){
         return "";
      }//if

      return currency.code;
   }//GetCodeById
   // ---------------------------------------------------------------------------
   public String GetNameById( int id ){
      Currency currency = currencies.get( id );

      if( currency == null ){
         return "";
      }//if

      return currency.name;
   }//GetCodeById
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void Clear(){
      db_helper.DeleteTable( tablename );
   }
   //---------------------------------------------------------------------------

}//DBCurrency
