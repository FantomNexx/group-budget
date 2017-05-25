package com.fantomsoftware.groupbudget.data;

public class OperationUser{
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public int id;
   public int id_op;
   public int id_budget;
   public int id_user;
   public int id_curr;

   public String name;
   public String comment;
   public String icon_name;

   public int type;
   public float amount_pers;
   public float amount_shared;

   public long  date_created;
   public float latitude;
   public float longitude;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public OperationUser(){
      this.id = -1;
      this.id_budget = -1;
      this.id_user = -1;
      this.id_curr = -1;

      this.name = "";
      this.comment = "";
      this.icon_name = "";

      this.type = Operation.TYPE_OUT;
      this.amount_pers = 0;
      this.amount_shared = 0;

      this.date_created = 0;
      this.latitude = -1;
      this.longitude = -1;
   }//OperationUser
   //---------------------------------------------------------------------------
   public OperationUser( OperationUser item ){
      this.id = -1;
      this.id_op = item.id_op;
      this.id_budget = item.id_budget;
      this.id_user = item.id_user;
      this.id_curr = item.id_curr;

      this.name = item.name;
      this.comment = item.comment;
      this.icon_name = item.icon_name;

      this.type = item.type;
      this.amount_pers = item.amount_pers;
      this.amount_shared = item.amount_shared;

      this.date_created = item.date_created;
      this.latitude = item.latitude;
      this.longitude = item.longitude;
   }//OperationUser
   //---------------------------------------------------------------------------

}//OperationUser
