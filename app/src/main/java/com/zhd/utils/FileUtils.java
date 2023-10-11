package com.zhd.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;

public class FileUtils {

    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void wirteStringToFile(final String str, final int type) {//can:type=0
        new Thread(new Runnable() {
            @Override
            public void run() {
                switch (type) {
                    case 0:
                        writeToFile(str, "can");
                        break;
                    case 1:
                        writeToFile(str, "J1939");
                        break;
                }
            }
        }).start();
    }

    private static void writeToFile(String str, String type) {
        String path = StorageManager.getSdCardPath();
        if (path != null) {
            File dir = new File(path + "/" + type);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            int fileIndex = SharedPreferencesUtils.getIntData(type);
            if (fileIndex == -1) {
                try {
                    File[] files = dir.listFiles();
                    Arrays.sort(files, new CompratorByLastModified());//按修改时间排序
                    String name = files[files.length - 1].getName();
                    fileIndex = Integer.parseInt(name.split(".")[0]);
                } catch (Exception e) {
                    fileIndex = -1;
                }
            }
            if (fileIndex == -1) {
                fileIndex = 1;
            }
            try {
                File dest = new File(path + "/" + type + "/" + fileIndex + ".txt");
                BufferedWriter writer;
                if (dest.length() > 100 * 1024 * 1024 /*100000*/) {//文件大小100M
                    fileIndex = fileIndex + 1;
                    if (fileIndex > 10)//文件个数10
                        fileIndex = 1;
                    dest = new File(path + "/" + type + "/" + fileIndex + ".txt");
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest, false)));
                    SharedPreferencesUtils.setIntData(type, fileIndex);
                } else {
                    writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dest, true)));
                }
                writer.write(str + " " + format.format(System.currentTimeMillis()));
                writer.write("\r\n");
                writer.flush();
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class CompratorByLastModified implements Comparator<File> {
        @Override
        public int compare(File o1, File o2) {
            long diff = o1.lastModified() - o2.lastModified();
            if (diff > 0)
                return 1;
            else if (diff == 0)
                return 0;
            else
                return -1;
        }
    }
}
