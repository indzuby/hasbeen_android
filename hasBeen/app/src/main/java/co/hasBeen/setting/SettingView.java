package co.hasBeen.setting;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.localytics.android.Localytics;

import co.hasBeen.R;
import co.hasBeen.account.FacebookApi;
import co.hasBeen.account.LogOutAsyncTask;
import co.hasBeen.social.FbFriendsView;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-11.
 */
public class SettingView extends ActionBarActivity {
    String mAccessToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccessToken = Session.getString(this, "accessToken", null);
        init();
    }
    protected void init(){
        setContentView(R.layout.setting);
        initActionBar();
        View findFacebookFriends = findViewById(R.id.findFacebookFriends);
        View abouthasBeen = findViewById(R.id.abouthasBeen);
        View version = findViewById(R.id.version);
        View reportProblem = findViewById(R.id.reportProblem);
        View openSource = findViewById(R.id.openSource);
        View logOut = findViewById(R.id.logOut);
        View settingAccount = findViewById(R.id.settingAccount);
        findFacebookFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), FbFriendsView.class);
                startActivity(intent);
            }
        });
        abouthasBeen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), AbouthasBeen.class);
                startActivity(intent);
            }
        });
        version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), Version.class);
                startActivity(intent);
            }
        });
        reportProblem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport();
            }
        });
        openSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), License.class);
                startActivity(intent);
            }
        });
        settingAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SettingAccount.class);
                startActivity(intent);
            }
        });
        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();
            }
        });

        TextView currentVersion = (TextView) findViewById(R.id.currentVersion);

        try {
            String v = Util.getVersion(this);
            currentVersion.setText("v"+v);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
    protected void logOut(){
        new LogOutAsyncTask().execute(mAccessToken);
        FacebookApi.callFacebookLogout(this);
        Session.remove(this,"accessToken");
        setResult(RESULT_OK);
        finish();
    }
    protected void sendReport(){
        Uri uri = Uri.parse("mailto:"+getString(R.string.support_email));
        Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
        startActivity(intent.createChooser(intent,getString(R.string.report_problem)));
    }
    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_place, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.action_bar_setting_title));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        mCustomActionBar.findViewById(R.id.moreVert).setVisibility(View.GONE);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Setting View");
        Localytics.upload();
    }
}
