package com.fantomsoftware.groupbudget.views_frag;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fantomsoftware.bottomsheet.BottomSheet;
import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.adapters.RecycleA_Users;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.User;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.fantomsoftware.groupbudget.views_act.Act;
import com.fantomsoftware.groupbudget.views_act.Act_User;



public class Frag_ListUsers extends Fragment{
   //---------------------------------------------------------------------------
   public static final String TAG = "Frag_ListUsers";

   private View              root_view;
   private RecycleA_Users    adapter;
   private SparseArray<User> data;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public View onCreateView(
      LayoutInflater inflater, ViewGroup cntr, Bundle bundle ){
      root_view = inflater.inflate( R.layout.frag_list_users, cntr, false );
      Init();
      return root_view;
   }//onCreateView
   //---------------------------------------------------------------------------
   @Override
   public void onResume(){
      super.onResume();
      Utils.UpdateActivity( ( (Act) getActivity() ) );
      Init();
   }//onResume
   //---------------------------------------------------------------------------
   @Override
   public void onActivityResult(
      int requestCode, int resultCode, Intent intent ){

      if( resultCode != Activity.RESULT_OK ){
         return;
      }

      switch( requestCode ){
         case ConstsAct.REQUEST_NEW:
            OnItemAdded( intent );
            break;

         case ConstsAct.REQUEST_EDIT:
            OnItemEdited( intent );
            break;
      }//switch
   }//onActivityResult
   //---------------------------------------------------------------------------



   //---------------------------------------------------------------------------
   private void Init(){
      InitList();
      InitFloatingActionButton();
   }//Init
   //---------------------------------------------------------------------------
   private void InitList(){
      UpdateData();
   }//InitList
   //---------------------------------------------------------------------------
   private void InitFloatingActionButton(){
      View.OnClickListener cl_fab = new View.OnClickListener(){
         @Override
         public void onClick( View v ){
            Intent intent = new Intent( Utils.context, Act_User.class );
            intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_NEW );
            startActivityForResult( intent, ConstsAct.REQUEST_NEW );
         }//onClick
      };

      FloatingActionButton fab = (FloatingActionButton)
         root_view.findViewById( R.id.fab_main );

      fab.setOnClickListener( cl_fab );
   }//InitFloatingActionButton
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void UpdateData(){

      OnResultId on_item_click = new OnResultId(){
         @Override
         public void OnResult( int id ){
            OnListItemClick( id );
         }//OnResult
      };//on_item_click

      OnResultId on_item_long_click = new OnResultId(){
         @Override
         public void OnResult( int id ){
            OnListItemLongClick( id );
         }//OnResult
      };//on_item_click


      Data.instance.db_user.UpdateLocal();
      data = Data.instance.db_user.users;

      adapter = new RecycleA_Users( data );
      adapter.SetOnListItemClick( on_item_click );
      adapter.SetOnListItemLongClick( on_item_long_click );


      RecyclerView.LayoutManager layout_manager =
         new LinearLayoutManager( Utils.context );

      RecyclerView rv_list =
         (RecyclerView) root_view.findViewById( R.id.rv_list );

      rv_list.setLayoutManager( layout_manager );
      rv_list.setAdapter( adapter );
   }//UpdateData
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnListItemClick( int id_item ){
      Intent intent = new Intent( Utils.context, Act_User.class );
      intent.putExtra( ConstsAct.KEY_MODE, ConstsAct.MODE_EDIT );
      intent.putExtra( ConstsAct.KEY_ID, id_item );
      startActivityForResult( intent, ConstsAct.REQUEST_EDIT );
   }//OnListItemClick
   //---------------------------------------------------------------------------
   private void OnListItemLongClick( final int id_item ){

      DialogInterface.OnClickListener cl = new DialogInterface.OnClickListener(){
         @Override
         public void onClick( DialogInterface dialog, int which ){
            switch( which ){
               case R.id.bs_delete:
                  DeleteItem( id_item );
                  break;
            }//switch
         }//onClick
      };//OnClickListener

      final BottomSheet.Builder sheet_builder =
         new BottomSheet.Builder( getActivity(), R.style.BottomSheet );

      sheet_builder.title( getString( R.string.bottom_sheet_title ) );
      sheet_builder.sheet( R.menu.bottom_sheet_delete_option );
      sheet_builder.listener( cl );

      final BottomSheet bottom_sheet_options = sheet_builder.build();
      bottom_sheet_options.show();

   }//OnListItemLongClick
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnItemAdded( Intent intent ){

      Bundle extras = intent.getExtras();
      if( !extras.containsKey( ConstsAct.KEY_ID ) ){
         return;
      }//if

      int  id   = extras.getInt( ConstsAct.KEY_ID );
      User item = Data.instance.db_user.Get( id );

      data.put( data.size(), item );

      adapter.Update();

   }//OnItemAdded
   //---------------------------------------------------------------------------
   private void OnItemEdited( Intent intent ){

      Bundle extras = intent.getExtras();
      if( !extras.containsKey( ConstsAct.KEY_ID ) ){
         return;
      }//if

      int  id   = extras.getInt( ConstsAct.KEY_ID );
      User item = Data.instance.db_user.Get( id );

      boolean is_found = false;
      for( int i = 0; i < data.size(); i++ ){
         if( data.valueAt( i ).id == item.id ){
            data.setValueAt( i, item );
            is_found = true;
            break;
         }//if
      }//for

      if( !is_found ){
         data.put( data.size(), item );
      }//if not found

      adapter.Update();

   }//OnItemEdited
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void DeleteItem( int id_item ){

      String msg = getString( R.string.snack_msg_delete_failed );

      if( Data.instance.db_user.Remove( id_item ) != -1 ){

         data.remove( id_item );
         adapter.Update();

         msg = getString( R.string.snack_msg_delete_success );
      }//if success

      Utils.SnackL( root_view, msg );

   }//DeleteItem
   //---------------------------------------------------------------------------

}//Frag_ListUsers

