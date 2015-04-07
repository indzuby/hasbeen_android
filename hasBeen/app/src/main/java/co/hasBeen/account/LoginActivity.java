package co.hasBeen.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.localytics.android.Localytics;

import java.util.Arrays;

import co.hasBeen.MainActivity;
import co.hasBeen.R;
import co.hasBeen.error.ErrorCheck;
import co.hasBeen.broadcast.GcmRegister;
import co.hasBeen.model.network.LoginTokenResponse;
import io.fabric.sdk.android.Fabric;

/**
 * Created by 주현 on 20150204.
 */
public class LoginActivity extends Activity {
    private String TAG = "MainActivity";
    final static String EMAIL = "1";
    final static String SOCIAL = "2";
    EditText mEmail;
    EditText mPassword;
    String mAccessToken;
    GcmRegister gcm;
    View mLoading;
    boolean isLoading;
    View mLoadingBg;
    InputMethodManager mImm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
    }

    protected void init() {
        setContentView(R.layout.login);
        gcm = new GcmRegister(this);

        mAccessToken = co.hasBeen.utils.Session.getString(getBaseContext(), "accessToken", null);
        if (mAccessToken != null) {
            if (!ErrorCheck.NetworkOnline(LoginActivity.this)) {
                Toast.makeText(getBaseContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
        mLoading = findViewById(R.id.refresh);
        mLoadingBg = findViewById(R.id.loadingBG);
        TextView goSignUp = (TextView) findViewById(R.id.goSignUp);
        goSignUp.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    if (!ErrorCheck.NetworkOnline(LoginActivity.this)) {
                        Toast.makeText(getBaseContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                        return;
                    }
                    flag = true;
                    Intent intent = new Intent(getBaseContext(), SignUpActivity.class);
                    startActivity(intent);
                    flag = false;
                    finish();
                }
            }
        });
        LoginButton authButton = (LoginButton) findViewById(R.id.facebookLogin);
        // set permission list, Don't foeget to add email
        authButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends"));
        // session state call back event
        authButton.setSessionStatusCallback(new Session.StatusCallback() {

            @Override
            public void call(final Session session, SessionState state, Exception exception) {
                if (!ErrorCheck.NetworkOnline(LoginActivity.this)) {
                    Toast.makeText(getBaseContext(), getString(R.string.internet_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (session.isOpened()) {
                    Log.i(TAG, "Access Token" + session.getAccessToken());
                    Request.executeMeRequestAsync(session,
                            new Request.GraphUserCallback() {
                                @Override
                                public void onCompleted(GraphUser user, Response response) {
                                    if (user != null) {
                                        Log.i(TAG, "User ID " + user.getId());
                                        Log.i(TAG, "Email " + user.asMap().get("email"));
                                        startLoading();
                                        new SignUpAsyncTask(socialHandler).execute(SOCIAL, session.getAccessToken());
                                    }
                                }
                            });
                }
            }
        });
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        TextView loginBtn = (TextView) findViewById(R.id.emailLoginBtn);
        String email = co.hasBeen.utils.Session.getString(this,"email","");
        mEmail.setText(email);
        loginBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                co.hasBeen.utils.Session.putString(getBaseContext(),"email",email);
                startLoading();
                new LogInAsyncTask(loginHandler).execute(email, password, "password", "read write delete");
            }
        });
        final View forgotPassword = findViewById(R.id.forgotPassword);
        mPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) forgotPassword.setVisibility(View.GONE);
                else forgotPassword.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), ForgotPassword.class);
                startActivity(intent);
            }
        });
    }

    protected void startLoading() {
        isLoading = true;
        mLoading.setVisibility(View.VISIBLE);
        mLoadingBg.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading.setVisibility(View.GONE);
        mLoadingBg.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    Handler socialHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String token = (String) msg.obj;
                new LogInAsyncTask(loginHandler).execute(token, "", "password", "read write delete");
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.common_error), Toast.LENGTH_LONG).show();
                stopLoading();
            }
        }
    };
    Handler loginHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                LoginTokenResponse loginToken = (LoginTokenResponse) msg.obj;
                co.hasBeen.utils.Session.putString(getBaseContext(), "accessToken", loginToken.getAccess_token());
                gcm.registerGcm(registHandler);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.login_error), Toast.LENGTH_LONG).show();
                stopLoading();
            }
        }
    };
    Handler registHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String regid = (String) msg.obj;
                String accessToken = co.hasBeen.utils.Session.getString(getBaseContext(), "accessToken", null);
                new DeviceAsyncTask(deviceHandler).execute(accessToken, regid);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.common_error), Toast.LENGTH_LONG).show();
                stopLoading();
            }
        }
    };
    Handler deviceHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                stopLoading();
                finish();
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.common_error), Toast.LENGTH_LONG).show();
                stopLoading();
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("LoginActivity");
        Localytics.upload();
    }
}
