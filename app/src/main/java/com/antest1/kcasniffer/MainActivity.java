package com.antest1.kcasniffer;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.VpnService;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Locale;

import static com.antest1.kcasniffer.KcaConstants.PREF_VPN_ENABLED;
import static com.antest1.kcasniffer.KcaConstants.VPN_STOP_REASON;
import static com.antest1.kcasniffer.KcaConstants.WIDGET_SET_ACTION;
import static com.antest1.kcasniffer.KcaConstants.WIDGET_TOGGLE_ACTION;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_VPN = 1;

    Toolbar toolbar;
    ToggleButton vpnbtn;
    TextView version;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.app_name);

        vpnbtn = (ToggleButton) findViewById(R.id.vpnbtn);
        vpnbtn.setTextOff(getString(R.string.ma_vpn_toggleoff));
        vpnbtn.setTextOn(getString(R.string.ma_vpn_toggleon));
        vpnbtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        final Intent prepare = VpnService.prepare(MainActivity.this);
                        if (prepare == null) {
                            //Log.i(TAG, "Prepare done");
                            onActivityResult(REQUEST_VPN, RESULT_OK, null);
                        } else {
                            startActivityForResult(prepare, REQUEST_VPN);
                        }
                    } catch (Throwable ex) {
                        // Prepare failed
                        Log.e("KCA", ex.toString() + "\n" + Log.getStackTraceString(ex));
                    }
                } else {
                    KcaVpnService.stop(VPN_STOP_REASON, MainActivity.this);
                    prefs.edit().putBoolean(PREF_VPN_ENABLED, false).apply();
                    Intent dsIntent = new Intent(getApplicationContext(), KcaDataService.class);
                    stopService(dsIntent);
                }
                Intent toggleIntent = new Intent(getApplicationContext(), KcaSnifferWidget.class);
                toggleIntent.setAction(WIDGET_SET_ACTION);
                sendBroadcast(toggleIntent);
            }
        });

        version = findViewById(R.id.app_version);
        version.setText(String.format(Locale.US, "%s %s\n%s", getString(R.string.app_name), BuildConfig.VERSION_NAME, getString(R.string.project_github)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        setVpnBtn();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVpnBtn();
    }

    public void setVpnBtn() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        vpnbtn.setChecked(prefs.getBoolean(PREF_VPN_ENABLED, false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_socks5) {
            startActivity(new Intent(this, NetworkSettingActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (requestCode == REQUEST_VPN) {
            prefs.edit().putBoolean(PREF_VPN_ENABLED, resultCode == RESULT_OK).apply();
            if (resultCode == RESULT_OK) {
                KcaVpnService.start("prepared", this);
                Intent dsIntent = new Intent(this, KcaDataService.class);
                startService(dsIntent);
            } else if (resultCode == RESULT_CANCELED) {
                // Canceled
            }
        }
    }
}
