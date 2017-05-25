package com.fantomsoftware.groupbudget.cmp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;


public class CustomExceptionHandler implements Thread.UncaughtExceptionHandler{
//--------------------------------------------------------------------
private Thread.UncaughtExceptionHandler defaultUEH;
private String                          localPath;
private String                          url;
//--------------------------------------------------------------------


/*
 * if any of the parameters is null, the respective functionality
 * will not be used
 */
public CustomExceptionHandler( String localPath ){

  this.localPath = localPath;
  this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
}
//--------------------------------------------------------------------
public void uncaughtException( Thread t, Throwable e ){

  String            timestamp   = currentTimeStamp();
  final Writer      result      = new StringWriter();
  final PrintWriter printWriter = new PrintWriter( result );

  e.printStackTrace( printWriter );
  String stacktrace = result.toString();
  printWriter.close();

  String filename = timestamp + ".log";

  if( localPath != null ){
    writeToFile( stacktrace, filename );
  }//if

  defaultUEH.uncaughtException( t, e );

}//uncaughtException
//--------------------------------------------------------------------
private void writeToFile( String stacktrace, String filename ){

  try{
    FileWriter     fw  = new FileWriter( localPath + "../" + filename );
    BufferedWriter bos = new BufferedWriter( fw );

    bos.write( stacktrace );
    bos.flush();
    bos.close();

  } catch( Exception e ){
    e.printStackTrace();
  }//try

}//writeToFile
//--------------------------------------------------------------------
private static String currentTimeStamp(){

  String ret = "";

  Date d = new Date();

  final SimpleDateFormat timeStampFormatter =
      new SimpleDateFormat( "dd-mm-yyyy hh:mm:ss" );

  ret = timeStampFormatter.format( d );

  return ret;
}//currentTimeStamp
//--------------------------------------------------------------------

}//CustomExceptionHandler