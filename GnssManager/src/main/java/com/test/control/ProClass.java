package com.test.control;

import android.app.Activity;

public class ProClass extends Activity
{
	public static native int protest(int args);

    static {
        System.loadLibrary("ProTest");
    }
}
