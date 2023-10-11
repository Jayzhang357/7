// IUniGpioListener.aidl
package com.unistrong.service;

// Declare any non-default types here with import statements

interface IUniGpioListener {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void onGpioStatusListener(String result);
}
