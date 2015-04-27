package me.sahiljain.tripTracker.main;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.DataSetObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.matesnetwork.callverification.Cognalys;
import com.matesnetwork.interfaces.VerificationListner;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.sahiljain.locationstat.R;
import me.sahiljain.tripTracker.notificationService.NotificationService;

/**
 * Created by sahil on 16/2/15.
 */
public class IntroActivity extends Activity {
    /**
     * Copy paste  from Telegram souce code
     */
    private ViewPager viewPager;
    private ImageView topImage1;
    private ImageView topImage2;
    private ViewGroup bottomPages;
    private int lastPage = 0;
    private int[] icons;
    private int[] titles;
    private int[] messages;


    private CountryCodeMap countryCodeMap;
    private static HashMap<String, String> country2phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.intro_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //Skipping 6th page
        icons = new int[]{
                R.drawable.intro1,
                R.drawable.intro2,
                R.drawable.intro3,
                R.drawable.intro5,
        };
        titles = new int[]{
                R.string.Page1Title,
                R.string.Page2Title,
                R.string.Page3Title,
                R.string.Page5Title,
        };
        messages = new int[]{
                R.string.Page1Message,
                R.string.Page2Message,
                R.string.Page3Message,
                R.string.Page5Message,
        };
        viewPager = (ViewPager) findViewById(R.id.intro_view_pager);
        TextView startMessagingButton = (TextView) findViewById(R.id.start_messaging_button);
        startMessagingButton.setText(R.string.StartTracking);

        topImage1 = (ImageView) findViewById(R.id.icon_image1);
        topImage2 = (ImageView) findViewById(R.id.icon_image2);
        bottomPages = (ViewGroup) findViewById(R.id.bottom_pages);
        topImage2.setVisibility(View.GONE);
        viewPager.setAdapter(new IntroAdapter());
        viewPager.setPageMargin(0);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {
                if (i == ViewPager.SCROLL_STATE_IDLE || i == ViewPager.SCROLL_STATE_SETTLING) {
                    if (lastPage != viewPager.getCurrentItem()) {
                        lastPage = viewPager.getCurrentItem();

                        final ImageView fadeoutImage;
                        final ImageView fadeinImage;
                        if (topImage1.getVisibility() == View.VISIBLE) {
                            fadeoutImage = topImage1;
                            fadeinImage = topImage2;

                        } else {
                            fadeoutImage = topImage2;
                            fadeinImage = topImage1;
                        }

                        fadeinImage.bringToFront();
                        fadeinImage.setImageResource(icons[lastPage]);
                        fadeinImage.clearAnimation();
                        fadeoutImage.clearAnimation();


                        Animation outAnimation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.icon_anim_fade_out);
                        outAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                fadeoutImage.setVisibility(View.GONE);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });

                        Animation inAnimation = AnimationUtils.loadAnimation(IntroActivity.this, R.anim.icon_anim_fade_in);
                        inAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                fadeinImage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {

                            }
                        });


                        fadeoutImage.startAnimation(outAnimation);
                        fadeinImage.startAnimation(inAnimation);
                    }
                }
            }
        });
        startMessagingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentLaunchActivity = new Intent(IntroActivity.this, LaunchActivity.class);
                startActivity(intentLaunchActivity);
                finish();
            }
        });
    }

    private void openVerifyScreen() {

        Cognalys.enableAnalytics(getApplicationContext(), true, true);
        //TODO: Remove this and create a drop down for country code-- that automatically populates
        setContentView(R.layout.sign_up_mobile);
        Spinner spinner = (Spinner) findViewById(R.id.country_code_input);

        List<String> countryList = new ArrayList<String>();
        countryList = getItemsInSpinner();
        Collections.sort(countryList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, countryList);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String cc = Cognalys.getCountryCode(this);
        for (int i = 0; i < countryList.size(); i++) {
            if (countryList.get(i).contains(cc)) {
                spinner.setSelection(i);
                break;
            }
        }

        final Button continueButton = (Button) findViewById(R.id.continue_button);

        continueButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                final Spinner tv_country_code = (Spinner) findViewById(R.id.country_code_input);
                final TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);

                String mobileNumber = tv_mobile_no.getText().toString();
                String countryCode = tv_country_code.getSelectedItem().toString();
                countryCode = countryCode.replaceAll("[-+.^:,]", "");
                countryCode = countryCode.replaceAll("[^\\d.]", "");

                String userName = countryCode + tv_mobile_no.getText().toString();

                if (mobileNumber == null || mobileNumber == "") {
                    showErrorDialog("Please enter a valid mobile number");
                } else {
                    showConfirmationDialog(userName, mobileNumber, countryCode);
                }
            }
        });
    }

    private List<String> getItemsInSpinner() {
        List<String> countryList = new ArrayList<String>();
        String[] locales = Locale.getISOCountries();
        countryCodeMap = new CountryCodeMap();
        country2phone = countryCodeMap.getCountry2phone();

        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            String key = obj.getCountry();
            String code = country2phone.get(key);
            countryList.add(obj.getDisplayCountry() + "(" + code + ")");
        }
        return countryList;
    }

    public void showConfirmationDialog(final String userName, final String mobileNumber, String countryCode) {
        final SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();

        new AlertDialog.Builder(this)
                .setMessage("Is this your correct number?\n" + countryCode + "-" + mobileNumber +
                        "\nA missed call will be send to verify this number.")
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        disableUIComponents();

                        //Save user name in shared preferences
                        editor.putString(Constants.USER_NAME, userName);
                        editor.apply();

                        //Save mobile no in shared preferences
                        editor.putString(Constants.MOBILE_NO, mobileNumber);
                        editor.apply();

                        verifyMobileNumber();
                    }
                })
                .setNegativeButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private String getCountryCode() {
        String countryCode = Cognalys.getCountryCode(this);
        countryCode = countryCode.replaceAll("[+]", "");
        return countryCode;
    }

    private void verifyMobileNumber() {
        final SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        final String mobileNumber = preferences.getString(Constants.MOBILE_NO, "");

        /**
         * Verify Mobile number using Cognalys
         */
        Cognalys.verifyMobileNumber(IntroActivity.this, Constants.COGNALYS_ACCESS_TOKEN,
                Constants.COGNALYS_APP_ID, mobileNumber, new VerificationListner() {
                    @Override
                    public void onVerificationStarted() {
                        Log.d(Constants.TAG, "Cognalys verification Started");
                    }

                    @Override
                    public void onVerificationSuccess() {
                        Log.d(Constants.TAG, "Cognalys verification successful; Now starting " +
                                "parse process");
                        startParseProcess();
                    }

                    @Override
                    public void onVerificationFailed(ArrayList<String> strings) {
                        Log.d(Constants.TAG, "Cognalys verification Failed");
                        findErrorMessage(strings);
                        enableUIComponents();
                    }
                });
    }

    private void findErrorMessage(ArrayList<String> strings) {

        for (String error : strings) {
            if (error.equals("551")) {
                Log.d(Constants.TAG, error);
                showErrorDialog(Constants.ERROR_551);
                break;
            } else if (error.equals("601") || error.equals("600")) {
                Log.d(Constants.TAG, error);
                showErrorDialog(Constants.ERROR_601);
                break;
            } else if (error.equals("504")) {
                Log.d(Constants.TAG, error);
                showErrorDialog(Constants.ERROR_504);
                break;
            } else if (error.equals("500")) {
                Log.d(Constants.TAG, error);
                showErrorDialog(Constants.ERROR_500);
                break;
            }
            Log.d(Constants.TAG, "Verification Failed: " + " " + error + '\n');
        }
    }

    private void showErrorDialog(final String error) {
        new AlertDialog.Builder(this)
                .setTitle("oops...")
                .setMessage(error)
                .setPositiveButton(Constants.OKAY, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (error.equalsIgnoreCase(Constants.ERROR_551)) {
                            Log.d(Constants.TAG, "Killing Viber now");
                            killViber();
                        }

                    }
                })
                .show();
    }

    private void killViber() {
    }

    private void disableUIComponents() {
        Spinner tv_countryCode = (Spinner) findViewById(R.id.country_code_input);
        TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        Button continueButton = (Button) findViewById(R.id.continue_button);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.welcome_screen_progress_bar);

        progressBar.setVisibility(View.VISIBLE);
        tv_mobile_no.setEnabled(false);
        tv_countryCode.setEnabled(false);
        continueButton.setEnabled(false);
    }

    private void enableUIComponents() {
        TextView tv_mobile_no = (TextView) findViewById(R.id.mobile_no_input);
        Button createAccountButton = (Button) findViewById(R.id.continue_button);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.welcome_screen_progress_bar);

        progressBar.setVisibility(View.GONE);
        tv_mobile_no.setEnabled(true);
        createAccountButton.setEnabled(true);
    }

    private void startParseProcess() {
        final SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        final String userName = preferences.getString(Constants.USER_NAME, "");

        if (userName != null && !userName.equalsIgnoreCase("")) {
            /**
             * Parse subscription starts here
             */
            ParseUser user = new ParseUser();
            user.setUsername(userName);
            user.setPassword(Constants.PASSWORD);
            user.signUpInBackground(new SignUpCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        //Congrats!
                        Log.d(Constants.TAG, "New user signed up");

                        startParseSubscription();
                    } else {
                        /**
                         * This is in cases when user uninstalls app
                         * and re-installs it
                         */
                        String error = e.toString();
                        if (error.contains("already taken")) {
                            Log.d(Constants.TAG, "User already exists in Database; Starting subscription process");
                            startParseSubscription();
                        } else {
                            //Shit!
                            Log.d(Constants.TAG, "Error: " + e.toString() + " \nNew user couldn't get signed up");
                            Toast.makeText(getBaseContext(), "Verification Failed, Please try later.", Toast.LENGTH_LONG).show();
                            enableUIComponents();
                        }
                    }
                    ParseInstallation.getCurrentInstallation().saveInBackground();
                }
            });
        }
    }

    private void startParseSubscription() {

        final SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        final String userName = preferences.getString(Constants.USER_NAME, "");

        ParsePush.subscribeInBackground("c" + userName, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d(Constants.TAG, "User Subscribed Successfully");
                    updateLoginDetails(userName);
                } else {
                    Log.d(Constants.TAG, "Error: " + e.toString() + "\n User didn't subscribe Successfully");
                    Toast.makeText(getBaseContext(), "Verification Failed, Please try later.", Toast.LENGTH_LONG).show();
                    enableUIComponents();
                }
            }
        });
    }

    /**
     * Method called only once- when the user is successfully subscribed for the first time.
     *
     * @param userName
     */

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void updateLoginDetails(String userName) {

//        Toast.makeText(getBaseContext(), "Successfully Verified! Please Wait...", Toast.LENGTH_LONG).show();

        SharedPreferences preferences = getSharedPreferences(Constants.TRIP_TRACKER_SHARED_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(Constants.LOGIN_STATUS, true);
        editor.putString(Constants.USER_NAME, userName);
        editor.apply();

        //Start the notification service
        Intent intent = new Intent(getApplicationContext(), NotificationService.class);
        getApplicationContext().startService(intent);

        boolean status = preferences.getBoolean(Constants.FIRST_LOGIN, true);
        if (status) {
            showVerificationSuccessfulDialog();
            status = false;
            editor.putBoolean(Constants.FIRST_LOGIN, status);
            editor.apply();
        }
    }

    private void showVerificationSuccessfulDialog() {
        //Start Main Activity
        final Intent intentMainActivity = new Intent(this, TabMainActivity.class);

        new AlertDialog.Builder(this)
                .setTitle("Success!")
                .setMessage("Your Mobile number has been Successfully Verified.")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(intentMainActivity);
                        finish();
                    }
                })
                .show();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private class IntroAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = View.inflate(container.getContext(), R.layout.intro_view_layout, null);
            TextView headerTextView = (TextView) view.findViewById(R.id.header_text);
            TextView messageTextView = (TextView) view.findViewById(R.id.message_text);
            container.addView(view, 0);

            headerTextView.setText(getString(titles[position]));
            messageTextView.setText((getString(messages[position])));

            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            int count = bottomPages.getChildCount();
            for (int a = 0; a < count; a++) {
                View child = bottomPages.getChildAt(a);
                if (a == position) {
                    child.setBackgroundColor(0xff2ca5e0);
                } else {
                    child.setBackgroundColor(0xffbbbbbb);
                }
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void finishUpdate(View arg0) {
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
            if (observer != null) {
                super.unregisterDataSetObserver(observer);
            }
        }
    }

}
