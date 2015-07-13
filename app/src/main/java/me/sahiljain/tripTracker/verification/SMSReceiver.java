package me.sahiljain.tripTracker.verification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 11/6/15.
 */
public class SMSReceiver extends BroadcastReceiver {

    private String userName;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
        String origNumber = msg.getOriginatingAddress();
        userName = origNumber.replaceAll("[-+.^:,]", "");
        userName = origNumber.replaceAll("[^\\d.]", "");
        Intent verificationActivityIntent = new Intent(context, VerificationActivity.class);
        verificationActivityIntent.putExtra(Constants.SMS_NUMBER, userName);
        verificationActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(verificationActivityIntent);

    }
}
