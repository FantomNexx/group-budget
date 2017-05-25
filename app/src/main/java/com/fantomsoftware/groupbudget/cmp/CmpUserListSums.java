package com.fantomsoftware.groupbudget.cmp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.data.Data;
import com.fantomsoftware.groupbudget.data.OperationUser;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;
import com.fantomsoftware.groupbudget.interfaces.OnResultId;
import com.fantomsoftware.groupbudget.utils.Utils;


public class CmpUserListSums extends RelativeLayout{
//--------------------------------------------------------------------
private ImageView       iv            = null;
private TextInputLayout etl           = null;
private EditText        et            = null;
private TextView        tv_btn        = null;
private LinearLayout    ll_list_items = null;

private SparseArray<OperationUser> data = null;

private OnEvent    on_button_click    = null;
private OnResultId on_list_item_click = null;
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public CmpUserListSums( Context context ){
  super( context );

  if( !isInEditMode() ){
    Init( context, null );
  }
}//CmpUserListSums
//--------------------------------------------------------------------
public CmpUserListSums( Context context, AttributeSet attrs ){
  super( context, attrs );

  if( !isInEditMode() ){
    Init( context, attrs );
  }
}//CmpUserListSums
//--------------------------------------------------------------------
public CmpUserListSums( Context context, AttributeSet attrs, int defStyle ){
  super( context, attrs, defStyle );

  if( !isInEditMode() ){
    Init( context, attrs );
  }
}//CmpUserListSums
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void Init( Context context, AttributeSet attrs ){

  OnClickListener cl_text_btn = new OnClickListener(){
    @Override
    public void onClick( View view ){
      OnButtonClick();
    }//onClick
  };//cl_text_btn

  View.inflate( context, R.layout.cmp_user_list_sums, this );

  iv = (ImageView) findViewById( R.id.iv );
  etl = (TextInputLayout) findViewById( R.id.etl );
  et = (EditText) findViewById( R.id.et );
  tv_btn = (TextView) findViewById( R.id.tv_btn );

  tv_btn.setOnClickListener( cl_text_btn );


  ProcessAttributes( attrs );

  InitList();
}//Init
//--------------------------------------------------------------------
private void InitList(){
  ll_list_items = (LinearLayout) findViewById( R.id.ll_list_items );
  UpdateData( data );
}//InitList
//--------------------------------------------------------------------
private void ProcessAttributes( AttributeSet attrs ){

  if( attrs == null ){
    return;
  }//if

  TypedArray attributes = Utils.context.getTheme().obtainStyledAttributes(
      attrs, R.styleable.CmpInputText, 0, 0 );

  int resid_icon = attributes.getResourceId(
      R.styleable.CmpInputText_resid_icon, -1 );

  int resid_label = attributes.getResourceId(
      R.styleable.CmpInputText_resid_label, -1 );

  int resid_label_btn = attributes.getResourceId(
      R.styleable.CmpInputText_resid_label_btn, -1 );

  attributes.recycle();


  String hint      = Utils.context.getString( resid_label );
  String label_btn = Utils.context.getString( resid_label_btn );

  iv.setImageResource( resid_icon );
  etl.setHint( hint );
  tv_btn.setText( label_btn );
}//ProcessAttributes
//--------------------------------------------------------------------


//--------------------------------------------------------------------
public void SetEnabled( boolean is_enabled ){

  if( is_enabled ){
    et.setOnTouchListener( null );
    etl.setOnTouchListener( null );
    return;
  }//if

  OnTouchListener tl = new OnTouchListener(){
    @Override
    public boolean onTouch( View v, MotionEvent event ){
      if( event.getAction() == MotionEvent.ACTION_UP ){
      }//if
      return true;
    }//onTouch
  };//tl

  et.setOnTouchListener( tl );
  etl.setOnTouchListener( tl );

}//SetEnabled
//--------------------------------------------------------------------
public void SetData( SparseArray<OperationUser> data ){
  UpdateData( data );
}//SetData
//--------------------------------------------------------------------
public void SetOnBtnClick( OnEvent on_btn_click ){
  this.on_button_click = on_btn_click;
}//SetOnItemClick
//--------------------------------------------------------------------
public void SetOnItemClick( OnResultId on_item_click ){
  this.on_list_item_click = on_item_click;
}//SetOnItemClick
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void UpdateData( SparseArray<OperationUser> data ){
  this.data = data;

  if( this.data == null ){
    this.data = new SparseArray<>();
  }//if

  int user_count = this.data.size();
  UpdateUserCount( user_count );
  UpdateUserList();

}//UpdateData
//--------------------------------------------------------------------
private void UpdateUserCount( int user_count ){

  if( et == null ){
    return;
  }//if

  String str_users;
  if( user_count == 1 ){
    str_users = Utils.context.getString( R.string.input_value_user_1 );
  }else if( this.data.size() > 1 && this.data.size() < 5 ){
    str_users = Utils.context.getString( R.string.input_value_user_2 );
  }else{
    str_users = Utils.context.getString( R.string.input_value_user_5 );
  }//if

  str_users = "" + user_count + " " + str_users;


  et.setText( str_users );
}//UpdateUserCount
//--------------------------------------------------------------------
private void UpdateUserList(){

  View.OnClickListener cl_item = new View.OnClickListener(){
    @Override
    public void onClick( View view ){
      int           position = (int) view.getTag();
      OperationUser item     = data.valueAt( position );
      OnListItemClick( item.id_user );
    }//onClick
  };//cl_item


  ll_list_items.removeAllViews();

  View view;

  for( int i = 0; i < data.size(); i++ ){
    view = View.inflate( getContext(), R.layout.list_item_user_sum, null );
    view.setOnClickListener( cl_item );

    UpdateUserInfo( view, i );

    ll_list_items.addView( view );
  }//for

  if( data.size() > 0 ){
    UpdateTotalRow();
  }//if

}//UpdateUserList
//--------------------------------------------------------------------
private void UpdateUserInfo( View view, int position ){

  TextView tv_name          = (TextView) view.findViewById( R.id.tv_name );
  TextView tv_amount_pers   = (TextView) view.findViewById( R.id.tv_amount_pers );
  TextView tv_amount_shared = (TextView) view.findViewById( R.id.tv_amount_shared );

  OperationUser item          = data.valueAt( position );
  String        amount_pers   = Utils.FormatFloatToString( item.amount_pers );
  String        user_name     = Data.instance.db_user.GetName( item.id_user );
  String        amount_shared = "";

  if( user_name == null ){
    user_name = "";
  }//if

  if( item.amount_pers != 0 && item.amount_shared != 0 ){
    tv_amount_pers.setVisibility( View.VISIBLE );
    amount_shared = " + " + Utils.FormatFloatToString( item.amount_shared );
  }else{
    tv_amount_pers.setVisibility( View.GONE );
    amount_shared = Utils.FormatFloatToString( item.amount_shared + item.amount_pers );
  }//if

  view.setTag( position );
  tv_name.setText( user_name );
  tv_amount_pers.setText( amount_pers );
  tv_amount_shared.setText( amount_shared );

}//UpdateUserInfo
//--------------------------------------------------------------------
private void UpdateTotalRow(){

  View           view             = View.inflate( getContext(), R.layout.list_item_user_sum, null );
  RelativeLayout rl_circle        = (RelativeLayout) view.findViewById( R.id.rl_circle );
  TextView       tv_name          = (TextView) view.findViewById( R.id.tv_name );
  TextView       tv_amount_pers   = (TextView) view.findViewById( R.id.tv_amount_pers );
  TextView       tv_amount_shared = (TextView) view.findViewById( R.id.tv_amount_shared );
  float          total            = 0;
  OperationUser  item             = null;

  for( int i = 0; i < data.size(); i++ ){
    item = data.valueAt( i );
    total += (item.amount_pers + item.amount_shared);
  }//for


  tv_name.setTypeface( Typeface.DEFAULT );

  tv_name.setText( Utils.GetResString( R.string.total ) );
  tv_amount_shared.setText( Utils.FormatFloatToString( total ) );

  rl_circle.setVisibility( View.INVISIBLE );
  tv_amount_pers.setVisibility( View.GONE );

  ll_list_items.addView( view );
}//UpdateTotalRow
//--------------------------------------------------------------------


//--------------------------------------------------------------------
private void OnListItemClick( int id ){

  if( on_list_item_click != null ){
    on_list_item_click.OnResult( id );
  }//if

}//OnListItemClick
//--------------------------------------------------------------------
private void OnButtonClick(){

  if( on_button_click != null ){
    on_button_click.OnEvent();
  }//if

}//OnButtonClick
//--------------------------------------------------------------------

}//CmpUserListSums
