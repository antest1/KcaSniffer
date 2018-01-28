This document explains how to connect KcaSniffer with your own application.  
The content can be changed during the update.

ContentProvider
--------
KcaSniffer provides [ContentProvider](https://developer.android.com/reference/android/content/ContentProvider.html) query cursor to get latest kancolle packet. (request/response data)  

The Content URI for get data cursor is ``content://com.antest1.kcasniffer.contentprovider/request``.  
These are the columns in the cursor. All of the column names are in UPPER case.

| Column | Type | Description |
| --- | --- | --- |
| TIMESTAMP | Long | The timestamp in milliseconds when received response (ex: 1517144169410) | 
| URL | String | Request URL (ex: ``/api_port/port``) |
| REQUEST | String | Request Data (URL-Encoded) |
| RESPONSE | String | Response Data (JSON Object) | 

This is the example code to get all of colums and convert to Gson JsonObject (in Activity or Service).
```java
String CONTENT_URI = Uri.parse("content://com.antest1.kcasniffer.contentprovider/request");
JsonObject data = new JsonObject();
Cursor cursor = getContentResolver().query(CONTENT_URI, null, null, null, null, null);
if(cursor != null) {
    if (cursor.moveToFirst()) { // moveToNext() if multiple data exist, here is only one data
        // Convert to JsonObject data
        data.addProperty("url", cursor.getString(cursor.getColumnIndex("URL")));
        data.addProperty("request", cursor.getString(cursor.getColumnIndex("REQUEST")));
        data.addProperty("response", cursor.getString(cursor.getColumnIndex("RESPONSE")));
        data.addProperty("timestamp", cursor.getLong(cursor.getColumnIndex("TIMESTAMP")));
    }
    cursor.close();
    // Do something you want
} else {
    Toast.makeText(context, "cursor is null", Toast.LENGTH_LONG).show();
}
```

In the application manifest, you must specify read permission to your own application to read data from database in KcaSniffer. Add this row under the ``<manifest>``.
```xml
<uses-permission android:name="com.antest1.kcasniffer.contentprovider.READ_DATA"/>
```

About APIs in Kancolle Game itself, check [this link](https://github.com/andanteyk/ElectronicObserver/blob/master/ElectronicObserver/Other/Information/apilist.txt).


BroadcastReceiver
--------
Yon can get [Broadcast](https://developer.android.com/guide/components/broadcasts.html) when new packet information is available.  
Use this with the ContentProvider when you need the real-time process like viewer application. 

The action URI is ``com.antest1.kcasniffer.broadcast``. Add this URI to ``<intent-filter>``.

(1) Use service to register the receiver
```xml
<service android:name=".KcaClientService">
    <intent-filter>
        <action android:name="com.antest1.kcasniffer.broadcast"/>
    </intent-filter>
</service>
```
(2) Register the receiver in the application
```xml
<receiver
    android:name=".KcaReceiver"
    android:exported="true">
    <intent-filter>
        <action android:name="com.antest1.kcasniffer.broadcast"/>
    </intent-filter>
</receiver>
```

Here is a example code of custom BroadcastReceiver, which get broadcast and get latest data from the ContentProvider.
```java
public class KcaReceiver extends BroadcastReceiver {
    public static final String BROADCAST_ACTION = "com.antest1.kcasniffer.broadcast";
    
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action == null) return;
        if(action.equals(BROADCAST_ACTION)) {
            JsonObject data = new JsonObject();
            Cursor cursor = context.getContentResolver().query(CONTENT_URI, null, null, null, null, null);
            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    // Convert to JsonObject data
                    data.addProperty("url", cursor.getString(cursor.getColumnIndex("URL")));
                    data.addProperty("request", cursor.getString(cursor.getColumnIndex("REQUEST")));
                    data.addProperty("response", cursor.getString(cursor.getColumnIndex("RESPONSE")));
                    data.addProperty("timestamp", cursor.getLong(cursor.getColumnIndex("TIMESTAMP")));
                }
                cursor.close();
                // Do something you want
            } else {
                Toast.makeText(context, "cursor is null", Toast.LENGTH_LONG).show();
            }
        }
    }
}
``` 

Demo
--------
You can check [KcaSampleClient](https://github.com/antest1/KcaSampleClient) to check the example codes.
(I'll prepare the demo video soon.)
