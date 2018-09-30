package com.wphokomp.fixme.core.Control;

public class CoreControl {
    public static String getChecksum(String message) {
        int j = 0;
        char t[];
        String soh = "" + (char) 1;
        String datum[] = message.split(soh);

        for (int k = 0; k < datum.length; k++) {
            t = datum[k].toCharArray();
            for (int i = 0; i < t.length; i++)
                j += (int) t[i];
            j += 1;
        }

        return (String.format("10=%d%s", j % 256, soh));
    }
}
