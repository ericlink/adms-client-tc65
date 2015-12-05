package net.diabetech.glucomon;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public interface MedicalDeviceFactory {

    public MedicalDevice create();

    public void destroy();
}
