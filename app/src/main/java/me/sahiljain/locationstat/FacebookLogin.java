package me.sahiljain.locationstat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

/**
 * Created by sahil on 9/2/15.
 */
public class FacebookLogin extends Fragment {

    private static final String TAG = "FacebookLogin";

    private UiLifecycleHelper uiLifecycleHelper;

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    private boolean isLoggedIn = false;

    public String getAccessTokenData() {
        return accessTokenData;
    }

    public void setAccessTokenData(String accessTokenData) {
        this.accessTokenData = accessTokenData;
    }

    private String accessTokenData = null;

    /**
     * To ensure that the sessions are set up correctly, your
     * fragment must override the fragment lifecycle methods:
     * onCreate(), onResume(), onPause(), onDestroy(), onActivityResult()
     * and onSaveInstanceState() and call the corresponding UiLifecycleHelper methods.
     * For example, calling the onCreate() method in the
     * UiLifecycleHelper object creates the Facebook session and opens it automatically
     * if a cached token is available.
     *
     * @param savedInstanceState
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiLifecycleHelper = new UiLifecycleHelper(getActivity(), callback);
        uiLifecycleHelper.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        /**
         * There are scenarios where the onSessionStateChange() notification
         * may not be triggered when the main activity is launched. This
         * may happen when your app is launched from the Facebook for
         * Android app through a bookmark link as an example. For these type
         * of scenarios, trigger the onSessionStateChange() method
         * whenever fragment is resumed. This will in turn properly set the
         * authenticated or non-authenticated UI.
         */
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed())) {
            onSessionStateChange(session, session.getState(), null);
        }
        uiLifecycleHelper.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiLifecycleHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiLifecycleHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiLifecycleHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiLifecycleHelper.onSaveInstanceState(outState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_login_screen, container, false);

        /**
         * During the authentication flow, the results are returned back
         * to the main activity by default. Your activity would need to
         * handle the results by overriding the onActivityResult() method.
         * To allow the fragment to receive the onActivityResult() call rather
         * than the activity, you can call the setFragment() method on the
         * LoginButton instance. To set this up, add the following code to
         * the onCreateView() method before you return a view:
         */
        LoginButton authButton = (LoginButton) view.findViewById(R.id.authButton);
        authButton.setFragment(this);
        authButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        return view;
    }

    private void onSessionStateChange(Session session, SessionState sessionState, Exception e) {
        if (sessionState.isOpened()) {
            Log.i(TAG, "Logged in. . . . .");
            this.isLoggedIn = true;
            setAccessTokenData(session.getAccessToken());
        } else {
            Log.i(TAG, "Logged out. . . . ");
            this.isLoggedIn = false;
        }

    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState sessionState, Exception e) {
            onSessionStateChange(session, sessionState, e);
        }
    };

}
