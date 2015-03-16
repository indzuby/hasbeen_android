package co.hasBeen.setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-03-16.
 */
public class AbouthasBeen extends  ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_hasbeen);
        View back = findViewById(R.id.actionBarBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        View email = findViewById(R.id.email);
        View web = findViewById(R.id.web);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReport();
            }
        });
        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.web_domain)));
                startActivity(intent);
            }
        });
    }
    protected void sendReport(){
        Uri uri = Uri.parse("mailto:"+getString(R.string.support_email));
        Intent intent = new Intent(Intent.ACTION_SENDTO,uri);
        startActivity(intent.createChooser(intent, getString(R.string.report_problem)));
    }
}