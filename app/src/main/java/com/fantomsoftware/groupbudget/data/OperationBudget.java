package com.fantomsoftware.groupbudget.data;

public class OperationBudget{
   //---------------------------------------------------------------------------
   public int id;

   public int id_budget;
   public int id_curr;

   public int type;

   public String name;
   public String comment;
   public String icon_name;

   public float amount;
   public long  date_created;
   public float latitude;
   public float longitude;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public OperationBudget(){
      this.id = -1;
      this.id_budget = -1;
      this.id_curr = -1;

      this.type = Operation.TYPE_OUT;

      this.name = "";
      this.comment = "";
      this.icon_name = "";

      this.amount = 0;
      this.date_created = 0;
      this.latitude = -1;
      this.longitude = -1;
   }//OperationBudget
   //---------------------------------------------------------------------------
   public OperationBudget( OperationBudget item ){
      this.id = -1;
      this.id_budget = item.id_budget;
      this.id_curr = item.id_curr;

      this.name = item.name;
      this.comment = item.comment;
      this.icon_name = item.icon_name;

      this.type = item.type;

      this.amount = -1;
      this.date_created = item.date_created;
      this.latitude = item.latitude;
      this.longitude = item.longitude;
   }//OperationBudget
   //---------------------------------------------------------------------------

}//OperationBudget

