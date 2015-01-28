package example.test.hasBeen.gallery;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.database.Position;

/**
 * Created by zuby on 2015-01-20.
 */
public class GalleryShare extends ActionBarActivity{
    Long mDayId;
    List<Position> mPositions;
    DatabaseHelper database ;
    TextView mTextDate;
    TextView mTextArea;
    ListView mListView;
    GalleryShareAdapter mShareAdpater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gallery_share_photos);
        mDayId = getIntent().getLongExtra("id",0);
        mTextDate = (TextView) findViewById(R.id.galleryShareTextDate);
        mTextDate.setText(getIntent().getStringExtra("date"));
        mTextArea = (TextView) findViewById(R.id.galleryShareTextArea);
        mTextArea.setText(getIntent().getStringExtra("area"));
        database = new DatabaseHelper(this);
        mListView = (ListView) findViewById(R.id.galleryShareDayView);
        try {
            mPositions = database.selectPositionByDayId(mDayId);
            mShareAdpater = new GalleryShareAdapter(this,mPositions);
            mListView.setAdapter(mShareAdpater);
        }catch (Exception e) {
            e.printStackTrace();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);

        actionBar.setDisplayHomeAsUpEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default,null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("back button clicked", "");
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
    }

}
