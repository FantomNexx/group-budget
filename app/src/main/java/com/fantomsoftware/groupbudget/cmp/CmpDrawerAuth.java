package com.fantomsoftware.groupbudget.cmp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.fantomsoftware.groupbudget.R;
import com.fantomsoftware.groupbudget.interfaces.OnResultBitmap;
import com.fantomsoftware.groupbudget.tasks.Task_DownloadImage;
import com.fantomsoftware.groupbudget.consts.ConstsAct;
import com.fantomsoftware.groupbudget.utils.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;

public class CmpDrawerAuth{
   //---------------------------------------------------------------------------
   private View            view_navigation_header = null;
   private CmpCircleAvatar cmp_avatar             = null;
   private TextView        tv_user_anonymous      = null;
   private TextView        tv_username            = null;
   private TextView        tv_email               = null;
   private TextView        btn_authorization      = null;

   private static final int STATUS_SIGNED_IN   = 0;
   private static final int STATUS_SIGNED_OUT  = 1;
   private static final int STATUS_SIGNING_IN  = 2;
   private static final int STATUS_SIGNING_OUT = 3;

   private int authorization_status = -1;

   private GoogleApiClient google_api_client;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public CmpDrawerAuth(){
   }//CmpDrawerAuth
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SetHeaderView( View view_navigation_header ){
      this.view_navigation_header = view_navigation_header;
   }//SetHeaderView
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void Init(){
      InitControls();
      UpdateStatus(STATUS_SIGNED_OUT);//todo delete this
   }//Init
   //---------------------------------------------------------------------------
   private void InitControls(){

      if( view_navigation_header == null ){
         return;
      }//if


      View.OnClickListener cl_btn_auth = new View.OnClickListener(){
         @Override
         public void onClick( View v ){
            OnClick_BtnAuthorization();
         }//onClick
      };//cl_btn_auth


      cmp_avatar = (CmpCircleAvatar) view_navigation_header.findViewById( R.id.profile_image );

      tv_user_anonymous = (TextView) view_navigation_header.findViewById( R.id.tv_user_anonymous );
      tv_username = (TextView) view_navigation_header.findViewById( R.id.tv_username );
      tv_email = (TextView) view_navigation_header.findViewById( R.id.tv_email );

      btn_authorization = (TextView) view_navigation_header.findViewById( R.id.btn_authorization );

      //tv_username.setText( "" );
      //tv_email.setText( "" );

      btn_authorization.setOnClickListener( cl_btn_auth );
   }//InitControls
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void BuildGoogleApiClient(){

      GoogleSignInOptions google_sign_in_options =
         new GoogleSignInOptions.Builder( GoogleSignInOptions.DEFAULT_SIGN_IN )
            .requestEmail()
            .build();


      GoogleApiClient.ConnectionCallbacks on_connection =
         new GoogleApiClient.ConnectionCallbacks(){
            //------------------------------------------------------------------
            @Override
            public void onConnected( Bundle connection_hint ){
               OnConnected( connection_hint );
            }//onConnected
            //------------------------------------------------------------------
            @Override
            public void onConnectionSuspended( int cause ){
               OnConnectionSuspened( cause );
            }//onConnectionSuspended
            //------------------------------------------------------------------
         };//callbacks


      GoogleApiClient.OnConnectionFailedListener on_connection_fail =
         new GoogleApiClient.OnConnectionFailedListener(){
            @Override
            public void onConnectionFailed( ConnectionResult result ){
               OnConnectionFailed( result );
            }//onConnectionFailed
         };//l_on_connection_fail


      GoogleApiClient.Builder builder = new GoogleApiClient.Builder( Utils.activity );
      builder.addConnectionCallbacks( on_connection );
      builder.addOnConnectionFailedListener( on_connection_fail );
      builder.addApiIfAvailable( Plus.API, Plus.PlusOptions.builder().build() );
      builder.addApiIfAvailable( Auth.GOOGLE_SIGN_IN_API, google_sign_in_options );

      google_api_client = builder.build();
   }//BuildGoogleApiClient
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void UpdateStatus( int status_new ){

      authorization_status = status_new;

      switch( authorization_status ){

         case STATUS_SIGNING_IN:
            Utils.ToastS( Utils.context.getString( R.string.navigation_header_signing_in ) );
            tv_user_anonymous.setVisibility( View.VISIBLE );
            tv_username.setVisibility( View.INVISIBLE );
            tv_email.setVisibility( View.INVISIBLE );
            break;

         case STATUS_SIGNING_OUT:
            Utils.ToastS( Utils.context.getString( R.string.navigation_header_signing_out ) );
            tv_user_anonymous.setVisibility( View.INVISIBLE );
            tv_username.setVisibility( View.VISIBLE );
            tv_email.setVisibility( View.VISIBLE );
            btn_authorization.setText( Utils.context.getString( R.string.navigation_header_sign_in ) );
            break;

         case STATUS_SIGNED_IN:
            Utils.ToastS( Utils.context.getString( R.string.navigation_header_signed_in ) );
            tv_user_anonymous.setVisibility( View.INVISIBLE );
            tv_username.setVisibility( View.VISIBLE );
            tv_email.setVisibility( View.VISIBLE );
            break;

         case STATUS_SIGNED_OUT:
            //Utils.ToastS( Utils.context.getString( R.string.navigation_header_signed_out ) );
            tv_user_anonymous.setVisibility( View.VISIBLE );
            tv_username.setVisibility( View.INVISIBLE );
            tv_email.setVisibility( View.INVISIBLE );
            SetDefaultAvatar();
            btn_authorization.setText( Utils.context.getString( R.string.navigation_header_signing_in ) );
            break;
      }//switch

   }//UpdateStatus
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void SignIn(){

      if( google_api_client == null ){
         BuildGoogleApiClient();
      }//if

      try{
         Intent intent_sign_in =
            Auth.GoogleSignInApi.getSignInIntent( google_api_client );

         Utils.activity.startActivityForResult(
            intent_sign_in, ConstsAct.REQUEST_SIGN_IN );

      }catch( Exception e ){
         Utils.ToastL( e.getMessage() );
         OnSignInError();
      }//try

   }//SignIn
   //---------------------------------------------------------------------------
   public void SignOut(){

      if( google_api_client == null ){
         return;
      }//if

      if( !google_api_client.isConnected() ){
         return;
      }//if not connected

      ResultCallback<Status> on_result = new ResultCallback<Status>(){
         @Override
         public void onResult( Status status ){
            OnSignedOut();
         }//onResult
      };//on_result

      Auth.GoogleSignInApi.signOut( google_api_client ).setResultCallback( on_result );

   }//SignOut
   //---------------------------------------------------------------------------
   public void DisconnectAccount(){

      if( google_api_client == null ){
         return;
      }//if

      ResultCallback<Status> on_result = new ResultCallback<Status>(){
         @Override
         public void onResult( Status status ){
            //OnDisconnectAccount()
         }//onResult
      };//on_result

      Auth.GoogleSignInApi.revokeAccess( google_api_client ).setResultCallback( on_result );
   }//DisconnectAccount
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnConnected( Bundle connection_hint ){
      Utils.ToastS( "OnConnected" );
   }//OnConnected
   //---------------------------------------------------------------------------
   private void OnConnectionSuspened( int cause ){
      Utils.ToastS( "OnConnectionSuspened" );
      SignIn();
   }//OnConnectionSuspened
   //---------------------------------------------------------------------------
   private void OnConnectionFailed( ConnectionResult result ){
      Utils.ToastS( "OnConnectionFailed" );
      OnSignInError();
      SignOut();
   }//OnConnectionFailed
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void OnSignedIn( GoogleSignInResult sign_in_result ){

      if( sign_in_result.isSuccess() ){

         google_api_client.connect();

         UpdateStatus( STATUS_SIGNED_IN );

         GoogleSignInAccount account = sign_in_result.getSignInAccount();

         tv_username.setTag( account.getId() );
         tv_username.setText( account.getDisplayName() );
         tv_email.setText( account.getEmail() );

         SetCustomAvatar( account.getPhotoUrl() );

      }else{
         OnSignInError();
         SignOut();
      }//sign in failed

   }//OnSignedIn
   //---------------------------------------------------------------------------
   private void OnSignInError(){
      Utils.ToastS( "You have a play services error" );
   }//OnSignInError
   //---------------------------------------------------------------------------
   private void OnSignedOut(){
      Plus.AccountApi.clearDefaultAccount( google_api_client );
      google_api_client.disconnect();
      UpdateStatus( STATUS_SIGNED_OUT );
   }//OnSignedOut
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public void OnRequestResult( int request, int result, Intent data ){

      switch( request ){

         case ConstsAct.REQUEST_SIGN_IN:
            GoogleSignInResult sign_in_result =
               Auth.GoogleSignInApi.getSignInResultFromIntent( data );

            OnSignedIn( sign_in_result );
            break;
      }//switch

   }//OnRequestResult
   //---------------------------------------------------------------------------
   private void OnClick_BtnAuthorization(){

      switch( authorization_status ){

         case STATUS_SIGNING_OUT:
         case STATUS_SIGNING_IN:
            return;

         case STATUS_SIGNED_IN:
            SignOut();
            break;

         case STATUS_SIGNED_OUT:
            SignIn();
            break;
      }//switch

   }//OnClick_BtnAuthorization
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private void SetCustomAvatar( Uri user_avatar_link ){
      SetDefaultAvatar();

      if( user_avatar_link == null ){
         return;
      }//if

      OnResultBitmap on_result = new OnResultBitmap(){
         @Override
         public void OnResult( Bitmap bitmap ){
            if( cmp_avatar == null ){
               return;
            }//if

            cmp_avatar.setImageBitmap( bitmap );
         }//OnResult
      };//on_result

      String link = user_avatar_link.toString();

      Task_DownloadImage task = new Task_DownloadImage();
      task.SetOnResult( on_result );
      task.execute( link );

   }//SetCustomAvatar
   //---------------------------------------------------------------------------
   private void SetDefaultAvatar(){
      Drawable avatar = Utils.GetDrawable( R.drawable.avatar_default );
      cmp_avatar.setImageDrawable( avatar );
   }//SetDefaultAvatar
   //---------------------------------------------------------------------------

}//CmpDrawerAuth
