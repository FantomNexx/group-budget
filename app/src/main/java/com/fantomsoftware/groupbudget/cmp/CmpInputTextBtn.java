package com.fantomsoftware.groupbudget.cmp;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;


public class CmpInputTextBtn extends RelativeLayout{
   //---------------------------------------------------------------------------
   private Context         context = null;
   private RelativeLayout  rl      = null;
   private ImageView       iv      = null;
   private TextInputLayout etl     = null;
   private EditText        et      = null;
   private TextView        tv_btn  = null;

   private OnEvent on_button_click = null;
   private OnEvent on_item_click   = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public CmpInputTextBtn( Context context ){
      super( context );

      if( !isInEditMode() ){
         Init( context, null );
      }
   }//CmpInputTextBtn
   //---------------------------------------------------------------------------
   public CmpInputTextBtn( Context context, AttributeSet attrs ){
      super( context, attrs );

      if( !isInEditMode() ){
         Init( context, attrs );
      }
   }//CmpInputTextBtn
   //---------------------------------------------------------------------------
   public CmpInputTextBtn( Context context, AttributeSet attrs, int defStyle ){
      super( context, attrs, defStyle );

      if( !isInEditMode() ){
         Init( context, attrs );
      }
   }//CmpInputTextBtn
   //---------------------------------------------------------------------------
   @Override
   protected void onLayout( boolean changed, int left, int top, int right, int bottom ){
      super.onLayout( changed, left, top, right, bottom );
   }//onLayout
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Init( Context context, AttributeSet attrs ){
      this.context = context;

      View.inflate( context, R.layout.cmp_input_text_btn, this );

      rl = (RelativeLayout) findViewById( R.id.root_layout );
      iv = (ImageView) findViewById( R.id.iv );
      etl = (TextInputLayout) findViewById( R.id.etl );
      et = (EditText) findViewById( R.id.et );
      tv_btn = (TextView) findViewById( R.id.tv_btn );


      ProcessAttributes( attrs );


      OnClickListener cl_item = new OnClickListener(){
         @Override
         public void onClick( View view ){
            OnItemClick();
         }//onClick
      };//cl_item

      OnClickListener cl_btn = new OnClickListener(){
         @Override
         public void onClick( View view ){
            OnButtonClick();
         }//onClick
      };//cl_btn

      rl.setOnClickListener( cl_item );
      tv_btn.setOnClickListener( cl_btn );
   }//Init
   //---------------------------------------------------------------------------
   private void ProcessAttributes( AttributeSet attrs ){

      if( attrs == null ){
         return;
      }

      TypedArray attributes = context.getTheme().obtainStyledAttributes(
         attrs, R.styleable.CmpInputText, 0, 0 );

      int resid_icon = attributes.getResourceId(
         R.styleable.CmpInputText_resid_icon, -1 );

      int resid_label = attributes.getResourceId(
         R.styleable.CmpInputText_resid_label, -1 );

      int resid_label_btn = attributes.getResourceId(
         R.styleable.CmpInputText_resid_label_btn, -1 );

      attributes.recycle();


      iv.setImageResource( resid_icon );

      String hint = context.getString( resid_label );
      etl.setHint( hint );

      String label_btn = context.getString( resid_label_btn );
      tv_btn.setText( label_btn );
   }//ProcessAttributes
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetInputType( int type ){
      if( et == null ){
         return;
      }

      et.setInputType( type );
   }//SetUserCount
   //---------------------------------------------------------------------------
   public String GetInputValue(){
      if( et == null ){
         return null;
      }

      return et.getText().toString();
   }//SetUserCount
   //---------------------------------------------------------------------------
   public void SetInputValue( String value ){
      if( et == null ){
         return;
      }

      et.setText( value );
   }//SetUserCount
   //---------------------------------------------------------------------------
   public void SetEnabled( boolean is_enabled ){
      //et.setEnabled( is_enabled );
      if( !is_enabled ){
         OnTouchListener tl = new OnTouchListener(){
            @Override
            public boolean onTouch( View v, MotionEvent event ){
               if( event.getAction() == MotionEvent.ACTION_UP ){
                  OnItemClick();
               }
               return true;
            }
         };

         et.setOnTouchListener( tl );
         etl.setOnTouchListener( tl );
      }else{
         et.setOnTouchListener( null );
         etl.setOnTouchListener( null );
      }
   }//SetEnabled
   //---------------------------------------------------------------------------
   public void SetButtonLabel( String value ){
      if( tv_btn == null ){
         return;
      }

      tv_btn.setText( value );
   }//SetButtonLabel
   //---------------------------------------------------------------------------
   public void SetErrorOn( String error ){
      etl.setErrorEnabled( true );
      etl.setError( error );
   }//SetErrorOn
   //---------------------------------------------------------------------------
   public void SetErrorOff(){
      etl.setErrorEnabled( false );
   }//SetErrorOff
   //---------------------------------------------------------------------------
   public void SetOnBtnClick( OnEvent on_btn_click ){
      this.on_button_click = on_btn_click;
   }//SetOnItemClick
   //---------------------------------------------------------------------------
   public void SetOnItemClick( OnEvent on_item_click ){
      this.on_item_click = on_item_click;
   }//SetOnItemClick
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnItemClick(){

      if( on_item_click != null ){
         on_item_click.OnEvent();
      }//if

   }//OnItemClick
   //--------------------------------------------------------------------------
   private void OnButtonClick(){

      if( on_button_click != null ){
         on_button_click.OnEvent();
      }//if

   }//OnButtonClick
   //---------------------------------------------------------------------------

}//CmpInputTextBtn
