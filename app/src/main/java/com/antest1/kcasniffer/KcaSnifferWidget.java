package com.antest1.kcasniffer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import static com.antest1.kcasniffer.KcaConstants.PREF_VPN_ENABLED;
import static com.antest1.kcasniffer.KcaConstants.VPN_STOP_REASON;
import static com.antest1.kcasniffer.KcaConstants.WIDGET_SET_ACTION;
import static com.antest1.kcasniffer.KcaConstants.WIDGET_TOGGLE_ACTION;

public class KcaSnifferWidget extends AppWidgetProvider {
    private static final int REQUEST_VPN = 1;

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isSnifferOn = prefs.getBoolean(PREF_VPN_ENABLED, false);
        int layout_id = isSnifferOn ? R.layout.widget_btn_on : R.layout.widget_btn_off;
        for (int i = 0; i < appWidgetIds.length; i++) {
            int widgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), layout_id);
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
    }

    public void setWidget(Context context, AppWidgetManager manager, int[] appWidgetIds, boolean isSnifferOn) {
        int layout_id = isSnifferOn ? R.layout.widget_btn_on : R.layout.widget_btn_off;
        RemoteViews views = new RemoteViews(context.getPackageName(), layout_id);
        Intent toggleIntent = new Intent(context, KcaSnifferWidget.class);
        toggleIntent.setAction(WIDGET_TOGGLE_ACTION);
        PendingIntent togglePendingIntent = PendingIntent.getBroadcast(context, 0, toggleIntent, 0);
        views.setOnClickPendingIntent(R.id.widget_vpnbtn, togglePendingIntent);

        for (int i = 0; i < appWidgetIds.length; i++) {
            int widgetId = appWidgetIds[i];
            manager.updateAppWidget(widgetId, views);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        boolean sniffer_status = prefs.getBoolean(PREF_VPN_ENABLED, false);

        Log.e("KCA", String.valueOf(action));
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            setWidget(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())), sniffer_status);
        }

        if (WIDGET_TOGGLE_ACTION.equals(action)) {
            if (sniffer_status) {
                KcaVpnService.stop(VPN_STOP_REASON, context);
                prefs.edit().putBoolean(PREF_VPN_ENABLED, false).apply();
                Toast.makeText(context, context.getString(R.string.ma_vpn_toggleoff).toUpperCase(), Toast.LENGTH_SHORT).show();
            } else {
                try {
                    final Intent prepare = VpnService.prepare(context);
                    if (prepare == null) {
                        KcaVpnService.start("prepared", context);
                        Intent dsIntent = new Intent(context, KcaDataService.class);
                        context.startService(dsIntent);
                    } else {
                        context.startActivity(prepare);
                    }
                    prefs.edit().putBoolean(PREF_VPN_ENABLED, true).apply();
                } catch (Throwable ex) {
                    // Prepare failed
                    Log.e("KCA", ex.toString() + "\n" + Log.getStackTraceString(ex));
                }
                Toast.makeText(context, context.getString(R.string.ma_vpn_toggleon).toUpperCase(), Toast.LENGTH_SHORT).show();
            }
            sniffer_status = !sniffer_status;
            setWidget(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())), sniffer_status);
        }

        if (WIDGET_SET_ACTION.equals(action)) {
            setWidget(context, manager, manager.getAppWidgetIds(new ComponentName(context, getClass())), sniffer_status);
        }
    }



}
