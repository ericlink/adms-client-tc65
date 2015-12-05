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

package example.helloworld;

import javax.microedition.midlet.*;

/**
 * MIDlet with some simple text output
 */

public class HelloWorld extends MIDlet {

    /**
    * Default constructor
    */
    public HelloWorld() {
        System.out.println("Constructor");
    }

    /**
    * This is the main application entry point. Here we simply give some
    * text output and close the application immediately again.
    */
    public void startApp() throws MIDletStateChangeException {
        System.out.println("startApp");
        System.out.println("\nHello World\n");

        destroyApp(true);
    }

    /**
    * Called when the application has to be temporary paused.
    */
    public void pauseApp() {
        System.out.println("pauseApp()");
    }

    /**
    * Called when the application is destroyed. Here we must clean
    * up everything not handled by the garbage collector.
    * In this case there is nothing to clean.
    */
    public void destroyApp(boolean cond) {
        System.out.println("destroyApp(" + cond + ")");

        notifyDestroyed();    
    }
}
