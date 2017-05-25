package com.fantomsoftware.groupbudget.data;
import android.util.SparseArray;


public class Options{
   //---------------------------------------------------------------------------
   public SparseArray<Option> list_options = null;
   public Option              selected     = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public Options(){
      list_options = new SparseArray<>();
   }//Options
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void DeselectAll(){
      for( int position = 0; position < list_options.size(); position++ ){
         Option option = list_options.valueAt( position );
         option.is_selected = false;
      }//for
   }//DeselectAll
   //---------------------------------------------------------------------------
   public void SetSelectedOption( int id ){

      if( list_options == null ){
         return;
      }//if

      Option option = list_options.get( id );

      if( option == null ){
         return;
      }//if

      SetSelectedOption( option );
   }//SetSelectedOption
   //---------------------------------------------------------------------------
   public void SetSelectedOption( Option option ){
      option.is_selected = true;
      this.selected = option;
   }//SetSelectedOption
   //---------------------------------------------------------------------------
   public void SetOptionStateByPos( int position, boolean state ){
      Option option = list_options.valueAt( position );

      if( option == null ){
         return;
      }

      option.is_selected = state;
      selected = option;
   }//SetOptionStateByPos
   //---------------------------------------------------------------------------
   public void SetOptionStateById( int id, boolean state ){
      Option option = list_options.get( id );

      if( option == null ){
         return;
      }
      
      option.is_selected = state;
      selected = option;
   }//SetOptionStateByPos
   //---------------------------------------------------------------------------
   public int GetSelectedPosition(){
      if( selected == null ){
         return -1;
      }//if
      return selected.position;
   }//GetSelectedPosition
   //---------------------------------------------------------------------------
   public CharSequence[] GetListStrings(){

      CharSequence[] list_strings = new CharSequence[list_options.size()];

      for( int position = 0; position < list_options.size(); position++ ){
         Option option = list_options.valueAt( position );
         list_strings[position] = option.string_value;
      }//for

      return list_strings;
   }//GetListStrings
   // ---------------------------------------------------------------------------
   public boolean[] GetSelectedBooleans(){

      boolean[] selected_booleans = new boolean[list_options.size()];

      for( int position = 0; position < list_options.size(); position++ ){
         Option option = list_options.valueAt( position );
         selected_booleans[position] = option.is_selected;
      }//for

      return selected_booleans;
   }//GetListStrings
   //---------------------------------------------------------------------------
   public int GetIdByPosition( int position ){

      Option option = list_options.valueAt( position );

      if( option == null ){
         return -1;
      }//if

      return option.id;
   }//GetIdByPosition
   // ---------------------------------------------------------------------------
   public Option GetByPosition( int position ){
      return list_options.valueAt( position );
   }//GetIdByPosition
   //---------------------------------------------------------------------------
   public String GetStringValueById( int id ){

      Option option = list_options.get( id );

      if( option == null ){
         return null;
      }//if

      return option.string_value;
   }//GetStringValueById
   //---------------------------------------------------------------------------



   //---------------------------------------------------------------------------
   public void Add( int id, String string_value, String string_value_descr, boolean is_selected ){

      if( list_options == null ){
         list_options = new SparseArray<>();
      }//if list_options == null

      int    position = list_options.size();
      Option option   = new Option( id, position, string_value, string_value_descr );
      option.is_selected = is_selected;

      list_options.put( option.id, option );
   }//Add
   //---------------------------------------------------------------------------
   public void Add( int id, String string_value, String string_value_descr ){
      Add( id, string_value, string_value_descr, false );
   }//Add
   //---------------------------------------------------------------------------
   public void Add( int id, String string_value, boolean is_selected ){
      Add( id, string_value, "", is_selected );
   }//Add
   // ---------------------------------------------------------------------------
   public void Add( int id, String string_value ){
      Add( id, string_value, "", false );
   }//Add
   //---------------------------------------------------------------------------
   public void Add( int id ){
      Add( id, "", "", false );
   }//Add
   //---------------------------------------------------------------------------
   public void Add( Option option ){
      Add( option.id, option.string_value, option.string_value_descr, false );
   }//Add
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public class Option{
      //------------------------------------------------------------------------
      public int    id                 = -1;
      public int    position           = -1;
      public String string_value       = "";
      public String string_value_descr = "";

      public boolean is_selected = false;
      //------------------------------------------------------------------------

      //------------------------------------------------------------------------
      public Option( int id, int position, String string_value ){
         this.id = id;
         this.position = position;
         this.string_value = string_value;
      }//Option
      //------------------------------------------------------------------------
      public Option( int id, int position, String string_value, String string_value_descr ){
         this.id = id;
         this.position = position;
         this.string_value = string_value;
         this.string_value_descr = string_value_descr;
      }//Option
      //------------------------------------------------------------------------
   }//Option
   //---------------------------------------------------------------------------

}//Options
