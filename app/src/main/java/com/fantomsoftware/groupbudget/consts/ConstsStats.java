package com.fantomsoftware.groupbudget.consts;


import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Options;
import com.fantomsoftware.groupbudget.utils.Utils;


public class ConstsStats{
   //---------------------------------------------------------------------------
   public final static int STATS_BALANCE     = 1;
   public final static int STATS_TOTAL_ADDED = 2;
   public final static int STATS_TOTAL_SPENT = 3;

   public static Options stats_options = null;
   //---------------------------------------------------------------------------

   //---------------------------------------------------------------------------
   public static String ToString( int state ){
      switch( state ){
         case STATS_BALANCE:
            return Utils.context.getString( R.string.stats_option_balance );

         case STATS_TOTAL_ADDED:
            return Utils.context.getString( R.string.stats_option_total_added );

         case STATS_TOTAL_SPENT:
            return Utils.context.getString( R.string.stats_option_total_spent );

         default:
            return "";
      }//switch
   }//ToString
   //---------------------------------------------------------------------------
   public static void InitOptions(){
      stats_options = new Options();
      stats_options.Add( STATS_BALANCE, Utils.context.getString( R.string.stats_option_balance ) );
      stats_options.Add( STATS_TOTAL_ADDED, Utils.context.getString( R.string.stats_option_total_added ) );
      stats_options.Add( STATS_TOTAL_SPENT, Utils.context.getString( R.string.stats_option_total_spent ) );
   }//InitOptions
   //---------------------------------------------------------------------------

}//ConstsAct
