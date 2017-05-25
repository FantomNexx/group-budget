package com.fantomsoftware.groupbudget.data;

public class User{
   //---------------------------------------------------------------------------
   public int    id;
   public String name;
   public String photo_path;
   public String photo_uri;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public User( User user ){
      this.id = user.id;
      this.name = user.name;
      this.photo_path = user.photo_path;
      this.photo_uri = user.photo_uri;
   }//User
   //---------------------------------------------------------------------------
   public User(){
      this.id = -1;
      this.name = "";
      this.photo_path = "";
      this.photo_uri = "";
   }//User
   //---------------------------------------------------------------------------

}//User
