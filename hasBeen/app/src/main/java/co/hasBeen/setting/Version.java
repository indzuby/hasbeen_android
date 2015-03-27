package co.hasBeen.setting;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.localytics.android.Localytics;

import co.hasBeen.R;
import co.hasBeen.utils.Session;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-25.
 */
public class Version extends ActionBarActivity {
    String cVersion,lVersion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.version_information);
        initActionBar();
        initVersion();
    }
    protected void initVersion(){
        TextView currentVersion = (TextView) findViewById(R.id.currentVersion);
        final TextView latestVersion = (TextView) findViewById(R.id.latestVersion);
        try {
            cVersion = Util.getVersion(this);
            currentVersion.setText(getString(R.string.current_version,cVersion));
            latestVersion.setText(getString(R.string.latest_version,cVersion));
        } catch(Exception e) {
            e.printStackTrace();
        }
        String accessToken = Session.getString(this, "accessToken", null);
        new LatestAsyncTask(new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if(msg.what==0) {
                    lVersion = (String)msg.obj;
                    latestVersion.setText(getString(R.string.latest_version,lVersion));
                    updateVersion();
                }
            }
        }).execute(accessToken);
    }
    protected void updateVersion(){
        View updateVersion = findViewById(R.id.updateVersion);
        View isLatestVersion = findViewById(R.id.isLatestVersion);
        if(lVersion.compareTo(cVersion)>0) {
            updateVersion.setVisibility(View.VISIBLE);
            isLatestVersion.setVisibility(View.GONE);
            updateVersion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.store_domain)));
                    startActivity(intent);
                }
            });
        }else {
            updateVersion.setVisibility(View.GONE);
            isLatestVersion.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Version View");
        Localytics.upload();
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
        titleView.setText(getString(R.string.version));
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
}
