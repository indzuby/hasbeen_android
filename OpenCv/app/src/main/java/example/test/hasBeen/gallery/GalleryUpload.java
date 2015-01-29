package example.test.hasBeen.gallery;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import example.test.hasBeen.R;

/**
 * Created by zuby on 2015-01-29.
 */
public class GalleryUpload extends ActionBarActivity{
    Long mDayId;
    TextView mTextDate;
    TextView mTextArea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }
    protected void init(){
        setContentView(R.layout.gallery_upload);
        initActionBar();
        mDayId = getIntent().getLongExtra("id",0);
        mTextDate = (TextView) findViewById(R.id.date);
        mTextDate.setText(getIntent().getStringExtra("date"));
        mTextArea = (TextView) findViewById(R.id.area);

        mTextArea.setText(getIntent().getStringExtra("area"));

    }
    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        TextView doneButton = (TextView) mCustomActionBar.findViewById(R.id.actionBarDone);
        titleView.setText("Upload");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);

    }
}
