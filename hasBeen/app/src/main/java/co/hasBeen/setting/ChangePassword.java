package co.hasBeen.setting;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.localytics.android.Localytics;

import co.hasBeen.R;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-25.
 */
public class ChangePassword extends ActionBarActivity{
    String mAccessToken;
    EditText currentPassword,newPassword,newPasswordConfirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password);
        mAccessToken = Session.getString(this, "accessToken", null);
        mLoading = findViewById(R.id.refresh);
        initActionBar();
        init();
    }
    protected void init(){
        currentPassword = (EditText) findViewById(R.id.currentPassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        newPasswordConfirm = (EditText) findViewById(R.id.newPasswordConfirm);
    }
    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.change_password));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        View done = mCustomActionBar.findViewById(R.id.actionBarDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPwd,newPwdConfirm,currPwd;
                currPwd = currentPassword.getText().toString();
                newPwd = newPassword.getText().toString();
                newPwdConfirm = newPasswordConfirm.getText().toString();
                if(isBoudary(newPwd,newPwdConfirm) || isSamePassword(newPwd,newPwdConfirm))
                    return ;
                startLoading();
                new ChangePasswordAsyncTask(changeHandler).execute(mAccessToken,currPwd,newPwd,newPwdConfirm);
            }
        });
    }
    Handler changeHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            stopLoading();
            if(msg.what==0) {
                Toast.makeText(getBaseContext(),getString(R.string.password_update_success),Toast.LENGTH_LONG).show();
                finish();
            }else {
                Toast.makeText(getBaseContext(),getString(R.string.password_error),Toast.LENGTH_LONG).show();
                currentPassword.requestFocus();
            }
        }
    };
    protected boolean isBoudary(String a, String b){
        if(a.length()<8 || b.length()<8) {
            Toast.makeText(this,getString(R.string.password_size_error),Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
    protected boolean isSamePassword(String a, String b){
        if(!a.equals(b)) {
            Toast.makeText(this,getString(R.string.password_confirm_error),Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Setting Account View");
        Localytics.upload();
    }
    View mLoading;
    boolean isLoading;
    protected void startLoading() {
        isLoading = true;
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }
}
