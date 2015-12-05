package net.diabetech.microedition.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connection;
import javax.microedition.io.HttpConnection;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class ConnectionHelper {

    private ConnectionHelper() {
    }

    public static void close(HttpConnection resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ex) {
                Logger.log("could not close resource", ex);
            }
        }
    }

    public static void close(Connection resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ex) {
                Logger.log("could not close resource", ex);
            }
        }
    }

    public static void close(InputStream resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ex) {
                Logger.log("could not close resource", ex);
            }
        }
    }

    public static void close(OutputStream resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ex) {
                Logger.log("could not close resource", ex);
            }
        }
    }

    public static void close(CommConnection resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ex) {
                Logger.log("could not close resource", ex);
            }
        }
    }
}
