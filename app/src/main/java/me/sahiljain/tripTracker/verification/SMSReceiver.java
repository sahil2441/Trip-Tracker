package me.sahiljain.tripTracker.verification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsMessage;

import me.sahiljain.tripTracker.main.Constants;

/**
 * Created by sahil on 11/6/15.
 */
public class SMSReceiver extends BroadcastReceiver {
    private String userName;
    private Context context;
    private SharedPreferences preferences;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extras = intent.getExtras();

        if (extras == null)
            return;

        Object[] pdus = (Object[]) extras.get("pdus");
        SmsMessage msg = SmsMessage.createFromPdu((byte[]) pdus[0]);
        String origNumber = msg.getOriginatingAddress();
        origNumber = origNumber.replaceAll("[-+.^:,]", "");
        origNumber = origNumber.replaceAll("[^\\d.]", "");
        this.userName = origNumber;
        this.context = context;
//        new BackgroundVerificationActivity().execute();
        Intent verificationActivityIntent = new Intent(context, VerificationActivity.class);
        verificationActivityIntent.putExtra(Constants.SMS_NUMBER, userName);
        verificationActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(verificationActivityIntent);

    }

    private class BackgroundVerificationActivity extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Intent verificationActivityIntent = new Intent(context, VerificationActivity.class);
            verificationActivityIntent.putExtra(Constants.SMS_NUMBER, userName);
            verificationActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(verificationActivityIntent);
            return null;
        }
    }
}
