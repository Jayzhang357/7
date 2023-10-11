package com.zhd.utils;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageVolume;


import com.zhd.AppHelper;

import java.util.ArrayList;
import java.util.List;

public class StorageManager {

    public static String getSdCardPath() {
        String path = null;
        Context context = AppHelper.getContext();
        android.os.storage.StorageManager manager = (android.os.storage.StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> volumes = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            volumes = manager.getStorageVolumes();
        }
        List<String> tfPath = new ArrayList<>();

        for (StorageVolume volume : volumes) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tfPath.add(StorageVolume.class.getMethod("getPath").invoke(volume).toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        for (String s : tfPath) {
            if (!s.contains("emulated")) {
                path = s;
                break;
            }
        }

        return path;
    }
}
