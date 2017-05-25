package com.fantomsoftware.groupbudget.utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Storage{

   //---------------------------------------------------------------------------
   public static final int STATE_OK                   = 0;
   public static final int STATE_FAILED_GET_STORAGE   = 1;
   public static final int STATE_FAILED_MAKE_FLODER   = 2;
   public static final int STATE_FAILED_MAKE_LOG_FILE = 3;

   private int storage_state = STATE_OK;

   private String folder_storage = "/storage/emulated/0";//will be set after initialization

   private String name_app_root_folder = "GroupBudget";
   private String name_database_folder = "Data";
   private String name_database_file   = "groupbudget.db";

   //will be set after initialization
   private        String path_app_root_folder = "";
   private        String path_database_folder = "";
   private static String path_database        = "";

   HashSet<String> storages = null;
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public Storage(){
   }//Storage
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public boolean Init(){

      if( !InitStorage() ){
         return false;
      }//if

      path_app_root_folder = folder_storage + "/" + name_app_root_folder;
      path_database_folder = path_app_root_folder + "/" + name_database_folder;
      path_database = path_database_folder + "/" + name_database_file;

      if( !MakeFolder( path_app_root_folder ) ){
         return false;
      }//if

      if( !MakeFolder( path_database_folder ) ){
         return false;
      }//if


      //todo pass back errors
      return true;
   }//Init
   //---------------------------------------------------------------------------
   private boolean InitStorage(){
      try{
         storages = Storage.GetStorageSet();
      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
         return false;
      }

      Iterator iterator = storages.iterator();

      if( iterator.hasNext() ){
         folder_storage = (String) iterator.next();

         if( !folder_storage.equals( "" ) ){
            storage_state = STATE_OK;
            return true;
         }
      }//interating

      storage_state = STATE_FAILED_GET_STORAGE;
      Log.d( Utils.log_key, "STATE_FAILED_GET_STORAGE" );

      return false;
   }//InitStorage
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private boolean MakeFolder( String folder_path ){
      try{
         File directory = new File( folder_path );
         try{
            if( directory.exists() ){
               return true;
            }

            if( directory.mkdir() ){
               return true;
            }
         }catch( Exception e ){
            Log.d( Utils.log_key, e.getMessage() );
         }

      }catch( Exception e ){
         Log.d( Utils.log_key, e.getMessage() );
      }

      return false;
   }//MakeFolder
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   public static String GetPath_Database(){
      return path_database;
   }//GetPath_Database
   //---------------------------------------------------------------------------
   public String GetPath_AppRootFolder(){
      return path_app_root_folder;
   }//GetPath_AppRootFolder
   //---------------------------------------------------------------------------
   public static HashSet<String> GetStorageSet(){
      HashSet<String> storageSet = new HashSet<>();
      storageSet.add( Environment.getExternalStorageDirectory().getAbsolutePath() );
      return storageSet;
   }//GetStorageSet
   //---------------------------------------------------------------------------
   public static HashSet<String> GetStorageSet( File file, boolean is_fstab_file ){
      HashSet<String> storageSet = new HashSet<String>();
      BufferedReader  reader     = null;
      try{
         reader = new BufferedReader( new InputStreamReader( new FileInputStream( file ) ) );
         String line;
         while( ( line = reader.readLine() ) != null ){
            HashSet<String> _storage = null;
            if( is_fstab_file ){
               _storage = ParseVoldFile( line );
            }else{
               _storage = ParseMountsFile( line );
            }
            if( _storage == null )
               continue;
            storageSet.addAll( _storage );
         }
      }catch( Exception e ){
         e.printStackTrace();
      }finally{
         try{
            reader.close();
         }catch( Exception e ){
            e.printStackTrace();
         }
         reader = null;
      }
            /*
             * set default external storage
             */
      storageSet.add( Environment.getExternalStorageDirectory().getAbsolutePath() );
      return storageSet;
   }
   //---------------------------------------------------------------------------



   //---------------------------------------------------------------------------
   public static boolean IsFolderExists( String folder_path ){
      return IsExists( folder_path );
   }//IsFolderExists
   //---------------------------------------------------------------------------
   public static boolean IsFileExists( String file_path ){
      return IsExists( file_path );
   }//IsFolderExists
   //---------------------------------------------------------------------------
   public static boolean IsExists( String path ){
      File file = new File( path );
      if( file.exists() == false ){
         return false;
      }
      return true;
   }//IsExists
   //---------------------------------------------------------------------------

   //---------------------------------------------------------------------------
   public static boolean DeleteFile( String file_path ){
      try{
         File file = new File( file_path );
         return file.delete();
      }catch( Exception e ){
         return false;
      }
   }//DeleteFile
   //---------------------------------------------------------------------------
   public static boolean DeleteFile( File file ){
      try{
         return file.delete();
      }catch( Exception e ){
         return false;
      }
   }//DeleteFile
   //---------------------------------------------------------------------------


   //---------------------------------------------------------------------------
   private static HashSet<String> ParseMountsFile( String str ){
      if( str == null )
         return null;
      if( str.length() == 0 )
         return null;
      if( str.startsWith( "#" ) )
         return null;
      HashSet<String> storageSet = new HashSet<String>();
            /*
             * /dev/block/vold/179:19 /mnt/sdcard2 vfat rw,dirsync,nosuid,nodev,noexec,relatime,uid=1000,gid=1015,fmask=0002,dmask=0002,allow_utime=0020,codepage=cp437,iocharset=iso8859-1,shortname=mixed,utf8,errors=remount-ro 0 0
             * /dev/block/vold/179:33 /mnt/sdcard vfat rw,dirsync,nosuid,nodev,noexec,relatime,uid=1000,gid=1015,fmask=0002,dmask=0002,allow_utime=0020,codepage=cp437,iocharset=iso8859-1,shortname=mixed,utf8,errors=remount-ro 0 0
             */
      Pattern patter  = Pattern.compile( "/dev/block/vold.*?(/mnt/.+?) vfat .*" );
      Matcher matcher = patter.matcher( str );
      boolean b       = matcher.find();
      if( b ){
         String _group = matcher.group( 1 );
         storageSet.add( _group );
      }

      return storageSet;
   }
   //---------------------------------------------------------------------------
   private static HashSet<String> ParseVoldFile( String str ){
      if( str == null )
         return null;
      if( str.length() == 0 )
         return null;
      if( str.startsWith( "#" ) )
         return null;
      HashSet<String> storageSet = new HashSet<String>();
            /*
             * dev_mount sdcard /mnt/sdcard auto /devices/platform/msm_sdcc.1/mmc_host
             * dev_mount SdCard /mnt/sdcard/extStorages /mnt/sdcard/extStorages/SdCard auto sd /devices/platform/s3c-sdhci.2/mmc_host/mmc1
             */
      Pattern patter1 = Pattern.compile( "(/mnt/[^ ]+?)((?=[ ]+auto[ ]+)|(?=[ ]+(\\d*[ ]+)))" );
            /*
             * dev_mount ins /mnt/emmc emmc /devices/platform/msm_sdcc.3/mmc_host
             */
      Pattern patter2  = Pattern.compile( "(/mnt/.+?)[ ]+" );
      Matcher matcher1 = patter1.matcher( str );
      boolean b1       = matcher1.find();
      if( b1 ){
         String _group = matcher1.group( 1 );
         storageSet.add( _group );
      }

      Matcher matcher2 = patter2.matcher( str );
      boolean b2       = matcher2.find();
      if( !b1 && b2 ){
         String _group = matcher2.group( 1 );
         storageSet.add( _group );
      }
            /*
         * dev_mount ins /storage/emmc emmc /devices/sdi2/mmc_host/mmc0/mmc0:0001/block/mmcblk0/mmcblk0p
         */
      Pattern patter3  = Pattern.compile( "/.+?(?= )" );
      Matcher matcher3 = patter3.matcher( str );
      boolean b3       = matcher3.find();
      if( !b1 && !b2 && b3 ){
         String _group = matcher3.group( 1 );
         storageSet.add( _group );
      }
      return storageSet;
   }
   //---------------------------------------------------------------------------

}//Storage
