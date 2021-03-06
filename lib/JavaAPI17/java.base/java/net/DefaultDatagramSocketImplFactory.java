/*
 * Copyright (c) 2007, 2019, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package java.net;

import sun.security.action.GetPropertyAction;

/**
 * This class defines a factory for creating DatagramSocketImpls. It defaults
 * to creating plain DatagramSocketImpls, but may create other DatagramSocketImpls
 * by setting the impl.prefix system property.
 *
 * @author Chris Hegarty
 */

class DefaultDatagramSocketImplFactory {
    static Class<?> prefixImplClass = null;

    static {
        String prefix = null;
        try {
            prefix = GetPropertyAction.privilegedGetProperty("impl.prefix");
            if (prefix != null)
                prefixImplClass = Class.forName("java.net."+prefix+"DatagramSocketImpl");
        } catch (Exception e) {
            System.err.println("Can't find class: java.net." +
                                prefix +
                                "DatagramSocketImpl: check impl.prefix property");
            //prefixImplClass = null;
        }
    }

    /**
     * Creates a new <code>DatagramSocketImpl</code> instance.
     *
     * @param   isMulticast     true if this impl if for a MutlicastSocket
     * @return  a new instance of a <code>DatagramSocketImpl</code>.
     */
    static DatagramSocketImpl createDatagramSocketImpl(boolean isMulticast /*unused on unix*/)
        throws SocketException {
        if (prefixImplClass != null) {
            try {
                @SuppressWarnings("deprecation")
                DatagramSocketImpl result = (DatagramSocketImpl)prefixImplClass.newInstance();
                return result;
            } catch (Exception e) {
                throw new SocketException("can't instantiate DatagramSocketImpl");
            }
        } else {
            return new java.net.PlainDatagramSocketImpl(isMulticast);
        }
    }
}
