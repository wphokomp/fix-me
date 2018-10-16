package com.wphokomp.fixme.core.Controller;

public class CoreControl {
    public static String getCheckSum(String msg) {
        int j = 0;
        char t[];
        String soh = "" + (char) 1;
        String datum[] = msg.split(soh);
        for (int k = 0; k < datum.length; k++) {
            t = datum[k].toCharArray();
            for (int i = 0; i < t.length; i++) {
                j += (int) t[i];
            }
            j += 1;
        }
        return ("10=" + (j % 256) + soh);
    }
}
