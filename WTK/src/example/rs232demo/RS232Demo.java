/*
 * Copyright (C) Siemens AG 2005. All Rights reserved.
 *
 * In addition and subject to the terms and conditions of the License Agreement 
 * Siemens grants Licensee a non-exclusive, non-transferable, limited license 
 * to transmit, reproduce, disseminate, utililize and/or edit the binary content 
 * of this Software (IMlet) for the sole purpose of designing, developping and 
 * testing Licensee's applications only in connection with a Siemens wireless 
 * module. Offenders will be held liable for payment of damages. 
 * All rights created by patent grant or registration of a utility model or 
 * design patent are reserved.
 *
 */

/*
 * @(#)RS232Demo.java 0.1 02/07/02
 */

package example.rs232demo;

import javax.microedition.midlet.*;
import java.io.*;
import javax.microedition.io.*;
import com.siemens.icm.io.*;


public class RS232Demo extends MIDlet {

  CommConnection  commConn;
  InputStream     inStream;
  OutputStream    outStream;

  /**
   * RS232Demo - default constructor
   */
  public RS232Demo() {
    System.out.println("RS232Demo: Constructor");
    System.out.println("Available COM-Ports: " + System.getProperty("microedition.commports"));
    try {
      String strCOM = "comm:com0;blocking=on;baudrate=115200";
      commConn = (CommConnection)Connector.open(strCOM);
      System.out.println("CommConnection(" + strCOM + ") opened");
      System.out.println("Real baud rate: " + commConn.getBaudRate());
      inStream  = commConn.openInputStream();
      outStream = commConn.openOutputStream();
      System.out.println("InputStream and OutputStream opened");
    } catch(IOException e) {
      System.out.println(e);
      notifyDestroyed();
    }
  }

  /**
   * startApp()
   */
  public void startApp() throws MIDletStateChangeException {
    System.out.println("RS232Demo: startApp");
    System.out.println("Looping back received data, leave with 'Q'...");
    try {
      int ch = -1;
      while(ch != 'Q') {
        ch = inStream.read();
        if (ch >= 0) {
          outStream.write(ch);
          System.out.print((char)ch);
        }
      }
    } catch(IOException e) {
      System.out.println(e);
    }
    System.out.println();
    destroyApp(true);
  }

  /**
   * pauseApp()
   */
  public void pauseApp() {
    System.out.println("RS232Demo: pauseApp()");
  }

  /**
   * destroyApp()
   *
   * This is important.  It closes the app's RecordStore
   * @param cond true if this is an unconditional destroy
   *             false if it is not
   *             currently ignored and treated as true
   */
  public void destroyApp(boolean cond) {
    System.out.println("RS232Demo: destroyApp(" + cond + ")");
    try {
      inStream.close();
      outStream.close();
      commConn.close();
      System.out.println("Streams and connection closed");
    } catch(IOException e) {
      System.out.println(e);
    }

    notifyDestroyed();
  }
}
