package example.test.hasBeen.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import example.test.hasBeen.MainActivity;
import example.test.hasBeen.R;
import example.test.hasBeen.model.network.LoginTokenResponse;

/**
 * Created by 주현 on 20150204.
 */
public class LoginActivity extends Activity {
    private String TAG = "MainActivity";
    final static String EMAIL = "1";
    final static String SOCIAL = "2";
    String mAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }

    protected void init() {
        setContentView(R.layout.signin);
        mAccessToken = example.test.hasBeen.utils.Session.getString(getBaseContext(), "accessToken", null);
        if(mAccessToken!=null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
        TextView goSignUp = (TextView) findViewById(R.id.goSignUp);
        goSignUp.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (!flag) {
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
        authButton.setReadPermissions(Arrays.asList("user_likes", "email"));
        // session state call back event
        authButton.setSessionStatusCallback(new Session.StatusCallback() {

            @Override
            public void call(final Session session, SessionState state, Exception exception) {

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
//                                            new SignUpAsyncTask(socialHandler).execute(SOCIAL, session.getAccessToken());
                                        new LogInAsyncTask(loginHandler).execute(session.getAccessToken(), "", "password", "read write delete", LogInAsyncTask.BASIC_FB);

                                    }
                                }
                            });
                }
            }
        });
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
                new LogInAsyncTask(loginHandler).execute(token, "", "password", "read write delete", LogInAsyncTask.BASIC_FB);
            } else {

            }
        }
    };
    Handler loginHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                LoginTokenResponse loginToken = (LoginTokenResponse) msg.obj;
                example.test.hasBeen.utils.Session.putString(getBaseContext(), "accessToken", loginToken.getAccess_token());
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    };
}
