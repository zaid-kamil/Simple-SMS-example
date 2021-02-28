package com.digipodium.smsexample;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.digipodium.smsexample.databinding.FragmentFirstBinding;
import com.google.android.material.snackbar.Snackbar;

public class FirstFragment extends Fragment {

    String scAddress = null;
    PendingIntent sentIntent = null;
    PendingIntent deliveryIntent = null;
    private FragmentFirstBinding bind;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        Intent sms_sent = new Intent("SMS_SENT");
        Intent sms_delivered = new Intent("SMS_DELIVERED");
        sentIntent = PendingIntent.getBroadcast(getActivity(),0, sms_sent,0);
        deliveryIntent = PendingIntent.getBroadcast(getActivity(),0, sms_delivered,0);
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SmsManager smsManager = SmsManager.getDefault();
        bind = FragmentFirstBinding.bind(view);
        bind.sendBtn.setOnClickListener(v -> {
            String msgData = bind.textsms.getText().toString();
            String deliveryAddr = bind.textDestination.getText().toString();
            if (msgData.length() > 0) {
                smsManager.sendTextMessage(deliveryAddr, scAddress, msgData, sentIntent, deliveryIntent);
            } else {
                Snackbar.make(bind.getRoot(), "message empty", Snackbar.LENGTH_LONG).show();
            }
        });

    }

    BroadcastReceiver sentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (getResultCode()){
                case Activity.RESULT_OK:
                    Toast.makeText(context, "message sent", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(context, "message not sent", Toast.LENGTH_SHORT).show();
                    break;

            }
        }
    };
    BroadcastReceiver deliveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, "msg delivered", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        getActivity().registerReceiver(sentReceiver,new IntentFilter("SMS_SENT"));
        getActivity().registerReceiver(deliveryReceiver,new IntentFilter("SMS_DELIVERED"));
    }
}