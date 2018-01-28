package com.antest1.kcasniffer;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.antest1.kcasniffer.KcaConstants.BROADCAST_ACTION;
import static com.antest1.kcasniffer.KcaConstants.PACKETSTORE_VERSION;
import static com.antest1.kcasniffer.KcaUtils.joinStr;

public class KcaDataService extends Service {
    Handler handler;
    KcaPacketStore packet_db;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "RUN", Toast.LENGTH_LONG).show();
        handler = new KcaDataServiceHandler(this);
        packet_db = new KcaPacketStore(getApplicationContext(), null, PACKETSTORE_VERSION);
        KcaVpnData.setHandler(handler);
        return super.onStartCommand(intent, flags, startId);
    }

    private static class KcaDataServiceHandler extends Handler {
        private final WeakReference<KcaDataService> mService;

        KcaDataServiceHandler(KcaDataService service) {
            mService = new WeakReference<KcaDataService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            KcaDataService service = mService.get();
            if (service != null) {
                service.handleServiceMessage(msg);
            }
        }
    }

    public void handleServiceMessage(Message msg) {
        String url = msg.getData().getString("url");
        byte[] raw = msg.getData().getByteArray("data");
        Reader data = new InputStreamReader(new ByteArrayInputStream(raw));
        String request = msg.getData().getString("request");
        JsonObject response = new JsonObject();
        try {
            // filter out api_token
            List<String> filtered = new ArrayList<String>();
            String[] requestData = request.split("&");
            for (int i = 0; i < requestData.length; i++) {
                String decodedData = URLDecoder.decode(requestData[i], "utf-8");
                if (!decodedData.startsWith("api_token")) {
                    filtered.add(requestData[i]);
                }
            }
            request = joinStr(filtered, "&");

            // read response
            String init = new String(Arrays.copyOfRange(raw, 0, 7));
            if (init.contains("svdata=")) {
                data.skip("svdata=".length());
            }
            if (raw.length > 0) response = new JsonParser().parse(data).getAsJsonObject();
            // Toast.makeText(getApplicationContext(), String.valueOf(request.length()) +
            //        " " + String.valueOf(response.toString().length()), Toast.LENGTH_LONG).show();
            packet_db.record(url, request, response.toString());
            Intent intent = new Intent();
            intent.setAction(BROADCAST_ACTION);
            sendBroadcast(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), KcaUtils.getStringFromException(e), Toast.LENGTH_LONG).show();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
