package com.antest1.kcasniffer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.antest1.kcasniffer.KcaConstants.AUTHORITY;
import static com.antest1.kcasniffer.KcaConstants.PACKETSTORE_VERSION;
import static com.antest1.kcasniffer.KcaConstants.PATH;

public class KcaContentProvider extends ContentProvider {
    private KcaPacketStore packet_db;
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sUriMatcher.addURI(AUTHORITY, PATH, 1);
    }

    @Override
    public boolean onCreate() {
        packet_db = new KcaPacketStore(getContext(), null, PACKETSTORE_VERSION);
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projections, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = packet_db.getReadableDatabase();
        Cursor cursor = null;
        switch (sUriMatcher.match(uri)){
            case 1:
                cursor = db.rawQuery(KcaUtils.format("SELECT URL, REQUEST, RESPONSE, TIMESTAMP FROM %s ORDER BY KEY DESC LIMIT 1",
                        KcaPacketStore.getTableName()), null);
                break;
            default:
                break;
        }
        if (cursor != null && getContext() != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        // Do Nothing
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        // Do Nothing
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        // Do Nothing
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        // Do Nothing
        return 0;
    }
}
