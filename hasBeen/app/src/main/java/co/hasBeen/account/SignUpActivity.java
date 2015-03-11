package co.hasBeen.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import co.hasBeen.MainActivity;
import co.hasBeen.R;
import co.hasBeen.error.ErrorCheck;
import co.hasBeen.model.network.LoginTokenResponse;

/**
 * Created by 주현 on 2015-02-04.
 */
public class SignUpActivity extends Activity {
    private String TAG = "SignUpActivity";
    EditText mFirstName;
    EditText mLastName;
    EditText mEmail;
    EditText mPassword;
    String mAccessToken;
    final static String EMAIL = "1";
    final static String SOCIAL = "2";
    String mRegId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccessToken = co.hasBeen.utils.Session.getString(getBaseContext(), "accessToken", null);

        init();
    }
    protected  void init()
    {
        setContentView(R.layout.signup);
        TextView goSignIn = (TextView) findViewById(R.id.goSignIn);
        goSignIn.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                    flag = false;
                }
            }
        });
        mFirstName = (EditText) findViewById(R.id.firstName);
        mLastName = (EditText) findViewById(R.id.lastName);
        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.password);
        TextView signUpBtn = (TextView) findViewById(R.id.emailSignUpBtn);
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;
            @Override
            public void onClick(View v) {
                if(!flag) {
                    if(!ErrorCheck.NetworkOnline(SignUpActivity.this)) {
                        Toast.makeText(getBaseContext(),getString(R.string.internet_error),Toast.LENGTH_LONG).show();
                        return;
                    }
                    flag=true;
                    String firstName = mFirstName.getText().toString();
                    String lastName = mLastName.getText().toString();
                    String email = mEmail.getText().toString();
                    String password = mPassword.getText().toString();
                    if(firstName.length()<=0 || lastName.length()<=0 || email.length()<=0 || password.length()<=0) {
                        flag = false;
                        return;
                    }
                    if(password.length()<8 || password.length()>50) {
                        Toast.makeText(getBaseContext(),getString(R.string.password_size_error),Toast.LENGTH_LONG).show();
                        flag = false;
                        return ;
                    }
                    new SignUpAsyncTask(new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            if(msg.what==0) {
                                flag = false;
                                Toast.makeText(getBaseContext(),getString(R.string.sign_up_ok),Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                flag = false;
                            }
                        }
                    }).execute(EMAIL, email, firstName, lastName, password);

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
                if(!ErrorCheck.NetworkOnline(SignUpActivity.this)) {
                    Toast.makeText(getBaseContext(),getString(R.string.internet_error),Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(getBaseContext(), user.asMap().get("email").toString(), Toast.LENGTH_LONG).show();
                                        new SignUpAsyncTask(socialHandler).execute(SOCIAL, session.getAccessToken());

                                    }
                                }
                            });
                }
            }
        });
    }
    Handler socialHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                String token = (String) msg.obj;
                new LogInAsyncTask(loginHandler).execute(token, "", "password", "read write delete", LogInAsyncTask.BASIC);
            } else {
                Toast.makeText(getBaseContext(),getString(R.string.common_error),Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }else
                Toast.makeText(getBaseContext(),getString(R.string.common_error),Toast.LENGTH_LONG).show();
        }
    };
}
