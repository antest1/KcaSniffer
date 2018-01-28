package com.antest1.kcasniffer;

import android.net.Uri;

public class KcaConstants {
    public static final String PREF_VPN_ENABLED = "enabled";
    public static final int PACKETSTORE_VERSION = 1;

    public static final String AUTHORITY = "com.antest1.kcasniffer.contentprovider";
    public static final String PATH  = "/request";

    public static final String BROADCAST_ACTION = "com.antest1.kcasniffer.broadcast";

    public static final Uri CONTENT_URI = Uri.parse("content://".concat(AUTHORITY).concat(PATH));
}
