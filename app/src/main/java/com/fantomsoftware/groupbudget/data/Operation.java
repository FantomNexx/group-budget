package com.fantomsoftware.groupbudget.data;


import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.utils.Utils;

public class Operation{
   //---------------------------------------------------------------------------
   public static final int UNDEF = -1;

   public static final int TYPE_IN     = 10;
   public static final int TYPE_OUT    = 11;
   public static final int TYPE_IN_OUT = 12;

   public static Options options_type_new = null;
   public static Options options_type_edit = null;
   //---------------------------------------------------------------------------

   //---------------------------------------------------------------------------
   public static String ToString( int type ){

      switch( type ){

         case TYPE_IN:
            return Utils.GetResString( R.string.op_type_in );

         case TYPE_OUT:
            return Utils.GetResString( R.string.op_type_out );

         case TYPE_IN_OUT:
            return Utils.GetResString( R.string.op_type_in_out );
         /*
         case TYPE_OUT_PERS:
            return Utils.GetResString( R.string.op_type_out_pers );

         case TYPE_OUT_PERS_TIP:
            return Utils.GetResString( R.string.op_type_out_pers_tips );
         */

         default:
            return "";
      }//switch

   }//StateToString
   //---------------------------------------------------------------------------
   public static void InitOptions(){

      Operation.options_type_new = new Options();

      options_type_new.Add(
         TYPE_IN, Utils.GetResString( R.string.op_type_in )
      );

      options_type_new.Add(
         TYPE_OUT, Utils.GetResString( R.string.op_type_out )
      );

      options_type_new.Add(
         TYPE_IN_OUT, Utils.GetResString( R.string.op_type_in_out )
      );


      Operation.options_type_edit = new Options();

      options_type_edit.Add(
         TYPE_IN, Utils.GetResString( R.string.op_type_in )
      );

      options_type_edit.Add(
         TYPE_OUT, Utils.GetResString( R.string.op_type_out )
      );




      /*
      options_type_new.Add(
         TYPE_IN,
         Utils.GetResString( R.string.op_type_in ),
         Utils.GetResString( R.string.op_type_in_descr )
      );

      options_type_new.Add(
         TYPE_IN_PERS,
         Utils.GetResString( R.string.op_type_in ),
         Utils.GetResString( R.string.op_type_in_descr )
      );

      options_type_new.Add(
         TYPE_IN_OUT,
         Utils.GetResString( R.string.op_type_in_out ),
         Utils.GetResString( R.string.op_type_in_out_descr )
      );

      options_type_new.Add(
         TYPE_OUT,
         Utils.GetResString( R.string.op_type_out_group ),
         Utils.GetResString( R.string.op_type_out_group_descr )
      );

      options_type_new.Add(
         TYPE_OUT_PERS,
         Utils.GetResString( R.string.op_type_out_pers ),
         Utils.GetResString( R.string.op_type_out_pers_descr )
      );

      options_type_new.Add(
         TYPE_OUT_PERS_TIP,
         Utils.GetResString( R.string.op_type_out_pers_tips ),
         Utils.GetResString( R.string.op_type_out_pers_tips_descr )
      );
      */

   }//InitOptions
   //---------------------------------------------------------------------------

}//Operation
