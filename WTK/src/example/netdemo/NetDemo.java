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
 * @(#)NetDemo.java 0.1 02/18/05
 */

package example.netdemo;

import javax.microedition.midlet.*;
import java.io.*;
import javax.microedition.io.*;


public class NetDemo extends MIDlet {
    
    static String destHost = "192.168.1.2"; 
    static String destPort = "80";
    static String connProfile = "bearer_type=gprs;access_point=internet.t-d1.de;username=anyone;password=something";

    /**
    * NetDemo - default constructor
    */
    public NetDemo() 
    {
        System.out.println("NetDemo: Constructor"); 
    }

    /**
    * startApp()
    */
    public void startApp() throws MIDletStateChangeException 
    {  
        SocketConnection    sc  = null;
        InputStream         is  = null;
        OutputStream        os  = null;

        System.out.println("NetDemo: startApp");

        try
        {       
            /* Open all */
            String openParm = "socket://" + destHost + ":" + destPort+ ";" + connProfile;            
            System.out.println("NetDemo: Connector open: " + openParm);             
            sc = (SocketConnection) Connector.open(openParm);

            is = sc.openInputStream();
            os = sc.openOutputStream();

            /* Write Data */
            String outTxt = "GET /somefile.txt HTTP/1.0\r\n\r\n";
            System.out.println("NetDemo: sending: " + outTxt);                
            os.write(outTxt.getBytes());

            /* Read Data */
            StringBuffer    str = new StringBuffer();
            int             ch;
           
            while ((ch = is.read()) != -1) 
            {
                str.append((char)ch);
            }

            System.out.println("NetDemo: received: " + str );

            /* Close all */
            is.close();
            os.close();
            sc.close();

        }
        catch (Exception e)
        {
            System.out.println("NetDemo: " + e.getMessage()); 
        }    

        destroyApp(true);
    }

    /**
    * pauseApp()
    */
    public void pauseApp() {
        System.out.println("NetDemo: pauseApp()");
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
        System.out.println("NetDemo: destroyApp(" + cond + ")");

        notifyDestroyed();
    }
}
