package com.dryver.ClassTests;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Looper;
import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;

import com.dryver.Utility.ConnectionCheck;

import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getContext;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Primary tests for US 08.xx.xx (Offline use)
 * Created by colemackenzie on 2016-11-27.
 */

@RunWith(AndroidJUnit4.class)
public class OfflineTests {


    /**
     * http://stackoverflow.com/questions/3930990/android-how-to-enable-disable-wifi-or-internet-connection-programmatically
     * Accessed: 2016-11-27
     */
    public void enableWifi() throws InterruptedException {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);

    }

    /**
     * http://stackoverflow.com/questions/3930990/android-how-to-enable-disable-wifi-or-internet-connection-programmatically
     * Accessed: 2016-11-27
     */

    public void disableWifi() throws InterruptedException {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
    }

    /**
     * Tests the internet connection.
     * Since Wi-Fi is only available on device, with 4g disabled. Test will fail on emulator
     */

    @FlakyTest
    @Test
    public void testHasInternetConnection() throws InterruptedException {
        Looper.prepare();
        ConnectionCheck connectionCheck = new ConnectionCheck();
        // Connect to Wi-Fi
        enableWifi();
        Thread.sleep(5000);

        assertTrue(connectionCheck.isConnected(getContext()));

        // Disable Wi-Fi
        disableWifi();
        Thread.sleep(1000);

        assertFalse(connectionCheck.isConnected(getContext()));

        cleanUp();
    }

    private void cleanUp() throws InterruptedException {
        enableWifi();
    }

}
