package com.example.sim;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.SpeechRecognizer;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SpannableString spannableString;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private TextView simCountryIsoTextView;
    private TextView simOperatorNameTextView;
    private TextView simOperatorIdTextView;
    private TextView simServiceStateTextView;
    private TextView simRoamingTextView;
    private TextView simStateTextView;
    private TextView deviceNameTextView;
    private TextView androidVersionTextView;
    private TextView deviceHardwareTextView;
    private TextView deviceArchTextView;
    private TextView totalRamTextView;
    private TextView availableRamTextView;
    private TextView ramThresholdTextView;
    private TextView lowMemoryTextView;
    private TextView batteryTextView;
    private TextView phoneTypeTextView;
    private TextView worldPhoneTextView;
    private TextView voiceCapableTextView;
    private TextView volteCapableTextView;
    private TextView wifiCallTextView;
    private TextView videoCallavTextView;
    private TextView videoCallenTextView;
    private ImageButton reloadImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Fetch references to the TextView elements in the XML layout
        simCountryIsoTextView = findViewById(R.id.sim_country_iso_textview);
        simOperatorNameTextView = findViewById(R.id.sim_operator_name_textview);
        simOperatorIdTextView = findViewById(R.id.sim_operator_id_textview);
        simStateTextView = findViewById(R.id.sim_state_textview);
        simServiceStateTextView = findViewById(R.id.service_state_textview);
        simRoamingTextView = findViewById(R.id.sim_roaming_textview);
        phoneTypeTextView = findViewById(R.id.phone_type_textview);
        worldPhoneTextView = findViewById(R.id.world_phone_textview);
        voiceCapableTextView = findViewById(R.id.voice_capable_textview);
        volteCapableTextView = findViewById(R.id.volte_capable_textview);
        wifiCallTextView = findViewById(R.id.wifi_call_textview);
        videoCallavTextView = findViewById(R.id.video_call_av_textview);
        videoCallenTextView = findViewById(R.id.video_call_en_textview);
        deviceNameTextView = findViewById(R.id.device_name_textview);
        androidVersionTextView = findViewById(R.id.android_version_textview);
        deviceHardwareTextView = findViewById(R.id.device_hardware_textview);
        deviceArchTextView = findViewById(R.id.device_arch_textview);
        totalRamTextView = findViewById(R.id.total_ram_textview);
        availableRamTextView = findViewById(R.id.available_ram_textview);
        ramThresholdTextView = findViewById(R.id.ram_threshold_textview);
        lowMemoryTextView = findViewById(R.id.low_memory_textview);
        batteryTextView = findViewById(R.id.battery_textview);
        reloadImageButton = findViewById(R.id.reload_imagebutton);

        // Check if the app has permission to read phone state
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, fetch SIM information
            fetchSimInformation();
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        }

        reloadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadValues();
            }
        });
    }
    private void reloadValues() {
        // Check if the app has permission to read phone state
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
            // Permission is already granted, fetch SIM information
            fetchSimInformation();
        } else {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, fetch SIM information
                fetchSimInformation();
            } else {
                // Permission denied, show a message and close the activity
                Toast.makeText(this, "Read Phone State permission is required to access SIM information", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
    private void fetchSimInformation() {
        // Fetch device information
        String deviceName = Build.MANUFACTURER + " " + Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String hardware = Build.HARDWARE;
        String arch = Build.CPU_ABI;
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalMegs = memoryInfo.totalMem / 1048576L; // Total RAM in MB
        long availableMegs = memoryInfo.availMem / 1048576L; // Available RAM in MB
        long thresholdMegs = memoryInfo.threshold / 1048576L; // RAM threshold in MB
        boolean isLowMemory = memoryInfo.lowMemory; // Check if RAM is low or not

        // Convert to GB
        double totalRAM = (double) Math.round(totalMegs / 1024.0 * 100) / 100;
        double availableRAM = (double) Math.round(availableMegs / 1024.0 * 100) / 100;
        BatteryManager batteryManager = (BatteryManager) getSystemService(Context.BATTERY_SERVICE);
        int batteryPercentage = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        int chargingStatus = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
        boolean isCharging = chargingStatus == BatteryManager.BATTERY_STATUS_CHARGING || chargingStatus == BatteryManager.BATTERY_STATUS_FULL;
        String batteryStatus = batteryPercentage + "% (" + (isCharging ? "Charging" : "Not charging") + ")";

        // Fetch SIM information
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String simCountryIso = telephonyManager.getSimCountryIso();
        String simOperatorName = telephonyManager.getSimOperatorName();
        String simOperatorId = telephonyManager.getSimOperator();
        String mcc = simOperatorId.substring(0, 3);
        String mnc = simOperatorId.substring(3);
        boolean isRoaming = telephonyManager.isNetworkRoaming();
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        boolean wifiCallingAvailable = wifiManager.isWifiEnabled();

        PackageManager packageManager = getPackageManager();
        boolean isVideoCallaven;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:12345"));
        intent.putExtra("android.telecom.extra.CALL_TYPE_VIDEO", true);
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            // Video call is available on the device
            isVideoCallaven = false;
        } else {
            // Video call is not available on the device
            isVideoCallaven = true;
        }

        boolean isVolteCapable;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            isVolteCapable = packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_SUBSCRIPTION );
            if (isVolteCapable) {
                //volteCapableTextView.setText("VoLTE capable : Yes" );
                isVolteCapable = true;
            } else {
                //volteCapableTextView.setText("VoLTE capable : No" );
                isVolteCapable = false;
            }
        } else {
           // volteCapableTextView.setText("VoLTE capable : No" );
            isVolteCapable = false;
        }

        String phoneType = getPhoneTypeString(telephonyManager.getPhoneType());
        String networkCountryIso = telephonyManager.getNetworkCountryIso();
        String simState = getSimStateString(telephonyManager.getSimState());
        spannableString = new SpannableString("SIM state: " + simState);
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.WHITE);
        ForegroundColorSpan colorSpan2;
        switch (simState) {
            case "Unknown":
            case "Absent":
            case "PIN Required":
            case "PUK Required":
            case "Network Locked":
                colorSpan2 = new ForegroundColorSpan(Color.RED);
                break;
            case "Ready":
                colorSpan2 = new ForegroundColorSpan(Color.GREEN);
                break;
            default:
                colorSpan2 = new ForegroundColorSpan(Color.RED);
        }
        int startIndex1 = 0;
        int endIndex1 = 11; // length of "SIM state: "
        spannableString.setSpan(colorSpan1, startIndex1, endIndex1, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(colorSpan2, endIndex1, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        simStateTextView.setText(spannableString);

        @SuppressLint("MissingPermission") ServiceState serviceState = telephonyManager.getServiceState();
        String serviceStateString = getServiceStateString(serviceState.getState());
        spannableString = new SpannableString("Service state: " + serviceStateString);
        ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(Color.WHITE);
        ForegroundColorSpan colorSpan4;
        switch(serviceStateString) {
            case "Out-of-Service":
            case "Emergency Only":
            case "Power Off":
            case "Unknown":
                colorSpan4 = new ForegroundColorSpan(Color.RED);
                break;
            case "In-Service":
                colorSpan4 = new ForegroundColorSpan(Color.GREEN);
                break;
            default:
                colorSpan4 = new ForegroundColorSpan(Color.RED);
        }
        int startIndex2 = 0;
        int endIndex2 = 15; // length of "SIM state: "
        spannableString.setSpan(colorSpan3, startIndex2, endIndex2, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        spannableString.setSpan(colorSpan4, endIndex2, spannableString.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        simServiceStateTextView.setText(spannableString);

        // Update the TextView elements with the fetched information
        simCountryIsoTextView.setText("Country: " + simCountryIso);
        simOperatorNameTextView.setText("Operator: " + simOperatorName);
        simOperatorIdTextView.setText("Operator ID: " + simOperatorId + " (MCC: " + mcc + ", MNC: " + mnc + ")");
        phoneTypeTextView.setText("Phone type: " + phoneType);
        deviceNameTextView.setText("Model: " + deviceName);
        androidVersionTextView.setText("Android: " + androidVersion);
        deviceHardwareTextView.setText("Hardware: " + hardware);
        deviceArchTextView.setText("CPU arch: " + arch);
        totalRamTextView.setText("RAM total: " + totalRAM + " GB");
        availableRamTextView.setText("RAM free: " + availableRAM + " GB");
        ramThresholdTextView.setText("RAM thres: " + thresholdMegs + " MB");
        batteryTextView.setText("Battery: " + batteryStatus);

        if (isRoaming) {
            String text = "Roaming: Yes";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            int startIndex = text.indexOf("Yes");
            int endIndex = startIndex + "Yes".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String text = "Roaming: No";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
            int startIndex = text.indexOf("No");
            int endIndex = startIndex + "No".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        simRoamingTextView.setText(spannableString);

        if (isLowMemory) {
            String text = "RAM is low: Yes";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            int startIndex = text.indexOf("Yes");
            int endIndex = startIndex + "Yes".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String text = "RAM is low: No";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
            int startIndex = text.indexOf("No");
            int endIndex = startIndex + "No".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        lowMemoryTextView.setText(spannableString);

        if (networkCountryIso != null && simCountryIso != null && !networkCountryIso.equals(simCountryIso)) {
            String text = "World Phone: Yes";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
            int startIndex = text.indexOf("Yes");
            int endIndex = startIndex + "Yes".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String text = "World Phone: No";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            int startIndex = text.indexOf("No");
            int endIndex = startIndex + "No".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        worldPhoneTextView.setText(spannableString);

        //SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            String text = "Voice Capable: Yes";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
            int startIndex = text.indexOf("Yes");
            int endIndex = startIndex + "Yes".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String text = "Voice Capable: No";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            int startIndex = text.indexOf("No");
            int endIndex = startIndex + "No".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        voiceCapableTextView.setText(spannableString);

        if (wifiCallingAvailable) {
            String text = "Wifi call avail.: Yes";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
            int startIndex = text.indexOf("Yes");
            int endIndex = startIndex + "Yes".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String text = "Wifi call avail.: No";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            int startIndex = text.indexOf("No");
            int endIndex = startIndex + "No".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        wifiCallTextView.setText(spannableString);

        if (isVideoCallaven) {
            String text = "Video call av.: Yes";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
            int startIndex = text.indexOf("Yes");
            int endIndex = startIndex + "Yes".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String text = "Video call av.: No";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            int startIndex = text.indexOf("No");
            int endIndex = startIndex + "No".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        videoCallavTextView.setText(spannableString);

        if (isVideoCallaven) {
            String text = "Video call en.: Yes";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
            int startIndex = text.indexOf("Yes");
            int endIndex = startIndex + "Yes".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String text = "Video call en.: No";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            int startIndex = text.indexOf("No");
            int endIndex = startIndex + "No".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        videoCallenTextView.setText(spannableString);

        if (isVolteCapable) {
            String text = "VoLTE capable: Yes";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
            int startIndex = text.indexOf("Yes");
            int endIndex = startIndex + "Yes".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        } else {
            String text = "VoLTE capable: No";
            spannableString = new SpannableString(text);
            ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.RED);
            int startIndex = text.indexOf("No");
            int endIndex = startIndex + "No".length();
            spannableString.setSpan(colorSpan, startIndex, endIndex, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }
        volteCapableTextView.setText(spannableString);
        volteCapableTextView.setText(spannableString);
    }
    private String getSimStateString ( int state){
        switch (state) {
            case TelephonyManager.SIM_STATE_UNKNOWN:
                return "Unknown";
            case TelephonyManager.SIM_STATE_ABSENT:
                return "Absent";
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                return "PIN Required";
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                return "PUK Required";
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                return "Network Locked";
            case TelephonyManager.SIM_STATE_READY:
                return "Ready";
            default:
                return "Null";
        }
    }
    private String getServiceStateString ( int state){
        switch (state) {
            case ServiceState.STATE_IN_SERVICE:
                return "In-Service";
            case ServiceState.STATE_OUT_OF_SERVICE:
                return "Out-of-Service";
            case ServiceState.STATE_EMERGENCY_ONLY:
                return "Emergency Only";
            case ServiceState.STATE_POWER_OFF:
                return "Power Off";
            default:
                return "Unknown";
        }
    }
    private String getPhoneTypeString ( int type){
        switch (type) {
            case TelephonyManager.PHONE_TYPE_GSM:
                return "GSM";
            case TelephonyManager.PHONE_TYPE_CDMA:
                return "CDMA";
            case TelephonyManager.PHONE_TYPE_SIP:
                return "SIP";
            default:
                return "Unknown";
        }
    }
}