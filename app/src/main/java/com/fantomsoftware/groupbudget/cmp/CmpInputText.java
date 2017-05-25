package com.fantomsoftware.groupbudget.cmp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.design.widget.TextInputLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.interfaces.OnEvent;


public class CmpInputText extends RelativeLayout{
   //---------------------------------------------------------------------------
   private Context         context = null;
   private RelativeLayout  rl      = null;
   private ImageView       iv      = null;
   private TextInputLayout etl     = null;
   private EditText        et      = null;

   private OnEvent on_item_click = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public CmpInputText( Context context ){
      super( context );

      if( !isInEditMode() ){
         Init( context, null );
      }
   }//CmpInputText
   //---------------------------------------------------------------------------
   public CmpInputText( Context context, AttributeSet attrs ){
      super( context, attrs );

      if( !isInEditMode() ){
         Init( context, attrs );
      }
   }//CmpInputText
   //---------------------------------------------------------------------------
   public CmpInputText( Context context, AttributeSet attrs, int defStyle ){
      super( context, attrs, defStyle );

      if( !isInEditMode() ){
         Init( context, attrs );
      }
   }//CmpInputText
   //---------------------------------------------------------------------------
   @Override
   protected void onLayout( boolean changed, int left, int top, int right, int bottom ){
      super.onLayout( changed, left, top, right, bottom );
   }//onLayout
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void Init( Context context, AttributeSet attrs ){
      this.context = context;

      View.inflate( context, R.layout.cmp_input_text, this );

      rl = (RelativeLayout) findViewById( R.id.root_layout );
      iv = (ImageView) findViewById( R.id.iv );
      etl = (TextInputLayout) findViewById( R.id.etl );
      et = (EditText) findViewById( R.id.et );


      ProcessAttributes( attrs );


      OnLongClickListener clc = new OnLongClickListener(){
         @Override
         public boolean onLongClick( View v ){
            if( etl == null ){
               return true;
            }

            //etl.setErrorEnabled( true );
            //etl.setError( "This is an error" );
            return true;
         }
      };

      OnClickListener cl = new OnClickListener(){
         @Override
         public void onClick( View view ){
            //etl.setErrorEnabled( false );
            OnItemClick();
         }//onClick
      };

      rl.setOnClickListener( cl );
      rl.setOnLongClickListener( clc );
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

      attributes.recycle();


      iv.setImageResource( resid_icon );

      String hint = context.getString( resid_label );
      etl.setHint( hint );

   }//ProcessAttributes
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetInputValue( String value ){
      et.setText( value );
   }//SetUserCount
   //---------------------------------------------------------------------------
   public String GetInputValue(){
      if( et == null ){
         return null;
      }

      return et.getText().toString();
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
   public void HideInputLine(){
      et.setBackgroundColor( Color.TRANSPARENT );
   }//HideInputLine
   //---------------------------------------------------------------------------
   public void SetErrorOn( String error ){
      etl.setErrorEnabled( true );
      etl.setError( error );
   }//SetErrorOn
   //---------------------------------------------------------------------------
   public void SetErrorOff(){
      etl.setErrorEnabled( false );
   }//SetError
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
   //---------------------------------------------------------------------------

}//CmpInputText
