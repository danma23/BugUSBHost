package com.yoctopuce.bugusbhost;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;


public class MainActivity extends ActionBarActivity
{

    private static final String TAG = "USB_HOST_BUG";
    private TextView _textView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _textView = (TextView) findViewById(R.id.textView);
        doEnum(null);
    }


    void log(String line)
    {
        Log.i(TAG, line);
        _textView.append(line + "\n");
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void doEnum(View view)
    {
        _textView.setText("");
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            log("Unable to access to USB manager");
            return;
        }

        HashMap<String, UsbDevice> connectedDevices = usbManager.getDeviceList();
        if (connectedDevices == null || connectedDevices.size()==0) {
            log("No USB devices detected");
            return;
        }
        for (UsbDevice usbdevice : connectedDevices.values()) {
            String deviceName = usbdevice.getProductName();
            String vendorName = usbdevice.getManufacturerName();
            int interfaceCount = usbdevice.getInterfaceCount();
            int configurationCount = 0;
            String tmp_serial = "unknown";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tmp_serial = usbdevice.getSerialNumber();
                configurationCount = usbdevice.getConfigurationCount();
            }
            log("====================================");
            log(String.format("Detected device:\n" +
                    "Manufacturer: %s\n" +
                    "Device: %s\n" +
                    "Serial: %s\n" +
                    "Interface count: %x\n", vendorName, deviceName, tmp_serial, interfaceCount));

            for (int j = 0; j < configurationCount; j++) {
                UsbConfiguration configuration = usbdevice.getConfiguration(j);
                int config_interfaces = configuration.getInterfaceCount();
                log(String.format("   USB configuration %d reported %d interfaces", j, config_interfaces));
            }
        }
    }
}
