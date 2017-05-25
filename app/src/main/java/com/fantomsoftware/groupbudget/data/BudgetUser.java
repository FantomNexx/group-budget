package com.fantomsoftware.groupbudget.data;

public class BudgetUser{
   //---------------------------------------------------------------------------
   public int id = -1;
   public int id_user = -1;
   public int id_budget = -1;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public BudgetUser(){
      this.id = -1;
      this.id_user = -1;
      this.id_budget = -1;
   }//BudgetUser
   //---------------------------------------------------------------------------
   public BudgetUser(int id_budget, int id_user){
      this.id_user = id_user;
      this.id_budget = id_budget;
   }//BudgetUser
   //---------------------------------------------------------------------------

}//BudgetUser

