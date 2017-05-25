package com.fantomsoftware.groupbudget.views_act;


import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;


public class Act_Authorization extends AppCompatActivity
   implements
   GoogleApiClient.ConnectionCallbacks,
   GoogleApiClient.OnConnectionFailedListener,
   View.OnClickListener{
   //---------------------------------------------------------------------------
   private static final int STATUS_SIGNED_IN   = 0;
   private static final int STATUS_SIGNING_IN  = 1;
   private static final int STATUS_IN_PROGRESS = 2;

   private static final int REQUEST_SIGN_IN = 0;


   private GoogleApiClient google_api_client;
   private PendingIntent   intent_sign_in;
   private int             authorization_status;


   private Button   btn_sign_in;
   private Button   btn_sign_out;
   private Button   btn_revoke_access;
   private TextView tv_status;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   protected void onCreate( Bundle savedInstanceState ){
      super.onCreate( savedInstanceState );
      setContentView( R.layout.act_authorization );

      // Get references to all of the UI views
      //btn_sign_in = (SignInButton) findViewById( R.id.btn_sign_in );
      btn_sign_in = (Button) findViewById( R.id.btn_sign_in );
      btn_sign_out = (Button) findViewById( R.id.btn_sign_out );
      btn_revoke_access = (Button) findViewById( R.id.btn_revoke_access );
      tv_status = (TextView) findViewById( R.id.tv_status );

      // Add click listeners for the buttons
      btn_sign_in.setOnClickListener( this );
      btn_sign_out.setOnClickListener( this );
      btn_revoke_access.setOnClickListener( this );

      // Build a GoogleApiClient
      google_api_client = buildGoogleApiClient();

   }//onCreate
   //---------------------------------------------------------------------------
   @Override
   protected void onResume(){
      super.onResume();
      Utils.UpdateActivity( this );
   }//onResume
   //---------------------------------------------------------------------------
   @Override
   protected void onStart(){
      super.onStart();
      google_api_client.connect();
   }//onStart
   //---------------------------------------------------------------------------
   @Override
   protected void onStop(){
      super.onStop();
      google_api_client.disconnect();
   }//onStop
   //---------------------------------------------------------------------------
   @Override
   protected void onActivityResult( int request, int result, Intent data ){

      switch( request ){

         case REQUEST_SIGN_IN:

            if( result == RESULT_OK ){
               authorization_status = STATUS_SIGNING_IN;
            }else{
               authorization_status = STATUS_SIGNED_IN;
            }//if OK

            if( !google_api_client.isConnecting() ){
               google_api_client.connect();
            }//if not connecting
            break;
      }//switch

   }//onActivityResult
   //---------------------------------------------------------------------------
   @Override
   public void onClick( View v ){

      if( !google_api_client.isConnecting() ){

         switch( v.getId() ){

            case R.id.btn_sign_in:
               tv_status.setText( "Signing In" );
               resolveSignInError();
               break;

            case R.id.btn_sign_out:
               Plus.AccountApi.clearDefaultAccount( google_api_client );
               google_api_client.disconnect();
               google_api_client.connect();
               break;

            case R.id.btn_revoke_access:
               Plus.AccountApi.clearDefaultAccount( google_api_client );
               Plus.AccountApi.revokeAccessAndDisconnect( google_api_client );

               google_api_client = buildGoogleApiClient();
               google_api_client.connect();
               break;
         }//switch
      }//if
   }//onClick
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   @Override
   public void onConnected( Bundle connectionHint ){

      btn_sign_in.setEnabled( false );
      btn_sign_out.setEnabled( true );
      btn_revoke_access.setEnabled( true );

      // Indicate that the sign in process is complete.
      authorization_status = STATUS_SIGNED_IN;

      try{
         String emailAddress = Plus.AccountApi.getAccountName( google_api_client );
         tv_status.setText( String.format( "Signed In to My App as %s", emailAddress ) );
      }catch( Exception ex ){
         String exception = ex.getLocalizedMessage();
         String exceptionString = ex.toString();
         //Note that you should log these errors in a ‘real’ app to aid in debugging
      }//try
   }
   //---------------------------------------------------------------------------
   @Override
   public void onConnectionSuspended( int cause ){
      google_api_client.connect();
   }
   //---------------------------------------------------------------------------
   @Override
   public void onConnectionFailed( ConnectionResult result ){
      if( authorization_status != STATUS_IN_PROGRESS ){
         intent_sign_in = result.getResolution();
         if( authorization_status == STATUS_SIGNING_IN ){
            resolveSignInError();
         }
      }
      // Will implement shortly
      onSignedOut();
   }
   //---------------------------------------------------------------------------
   private void onSignedOut(){
      // Update the UI to reflect that the user is signed out.
      btn_sign_in.setEnabled( true );
      btn_sign_out.setEnabled( false );
      btn_revoke_access.setEnabled( false );

      tv_status.setText( "Signed out" );
   }
   //---------------------------------------------------------------------------
   private void resolveSignInError(){

      if( intent_sign_in != null ){
         try{
            authorization_status = STATUS_IN_PROGRESS;
            startIntentSenderForResult(
               intent_sign_in.getIntentSender(),
               REQUEST_SIGN_IN, null, 0, 0, 0 );

         }catch( IntentSender.SendIntentException e ){
            authorization_status = STATUS_SIGNING_IN;
            google_api_client.connect();
         }
      }else{
         // You have a play services error -- inform the user
      }
   }
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private GoogleApiClient buildGoogleApiClient(){
      return new GoogleApiClient.Builder( this )
         .addConnectionCallbacks( this )
         .addOnConnectionFailedListener( this )
         .addApi( Plus.API, Plus.PlusOptions.builder().build() )
         .addScope( new Scope( "email" ) )
         .build();
   }
   //---------------------------------------------------------------------------

}//Act_Authorization
