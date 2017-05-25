package com.fantomsoftware.groupbudget.data;

public class Currency{
   //---------------------------------------------------------------------------
   public final static int USED = 1;
   public final static int NOT_USED = 0;

   public final static String KEY_DEFAULT_CURRENCY = "KEY_DEFAULT_CURRENCY";
   public static int default_currnecy_id = -1;
   public static Currency default_currnecy = null;


   public int id;
   public String country;
   public String name;
   public String code;
   public int is_used;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public Currency(
      int id,
      String code,
      String country,
      String name,
      int is_used ){

      this.id = id;
      this.country = country;
      this.name = name;
      this.code = code;
      this.is_used = is_used;
   }//Currency
   //---------------------------------------------------------------------------

}//Currency
