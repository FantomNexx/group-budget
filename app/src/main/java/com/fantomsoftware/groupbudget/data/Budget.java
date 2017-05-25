package com.fantomsoftware.groupbudget.data;

public class Budget{
   //---------------------------------------------------------------------------
   public int id;
   public String name;
   public String comment;
   public int id_curr_def;
   public long date_created;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public Budget(){
      this.id = -1;
      this.name = "";
      this.comment = "";
      this.id_curr_def = 0;
   }//Budget
   //---------------------------------------------------------------------------
   
}//Budget
