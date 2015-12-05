package net.diabetech.test;

import net.diabetech.glucomon.MedicalDevice;
import net.diabetech.glucomon.MedicalDeviceFactory;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class MockMedicalDeviceFactory implements MedicalDeviceFactory {

    public MockMedicalDeviceFactory() {
    }

    public MedicalDevice create() {
        return new MockMedicalDevice();
    }

    public void destroy() {
    }
}
