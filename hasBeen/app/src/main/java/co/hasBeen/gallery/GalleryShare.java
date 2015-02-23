package co.hasBeen.gallery;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.model.database.Day;
import co.hasBeen.model.database.Position;

/**
 * Created by zuby on 2015-01-20.
 */
public class GalleryShare extends ActionBarActivity{
    Long mDayId;
    List<Position> mPositions;
    Day mDay;
    Boolean[] isCheckedPosition;
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
        mTextDate = (TextView) findViewById(R.id.date);
        mTextDate.setText(getIntent().getStringExtra("date"));
        mTextArea = (TextView) findViewById(R.id.placeName);
        mTextArea.setText(getIntent().getStringExtra("area"));
        database = new DatabaseHelper(this);
        mListView = (ListView) findViewById(R.id.listView);
        try {
            mDay = database.selectDay(mDayId);
            mDay.setPositionList(database.selectPositionByDayId(mDayId));
            isCheckedPosition = new Boolean[mDay.getPositionList().size()];
            mDay.setIsCheckedPosition(isCheckedPosition);
            Arrays.fill(isCheckedPosition,true);
            mShareAdpater = new GalleryShareAdapter(this, mDay.getPositionList(), mDay.getIsCheckedPosition());
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
        TextView done = (TextView) mCustomActionBar.findViewById(R.id.actionBarDone);
        done.setOnClickListener(new View.OnClickListener() {
            boolean flag=false;
            @Override
            public void onClick(View v) {
                if(!flag) {
                    flag = true;
                    for(int i = 0 ; i<isCheckedPosition.length;i++) {
                        Boolean isPositionCheck = isCheckedPosition[i];
                        if(isPositionCheck) {
                            Position position = mDay.getPositionList().get(i);
                            Long placeId = position.getPlaceId();
                            try {
                                mDay.setMainPlace(database.selectPlace(placeId));
                                mDay.setMainPlace(position.getPlace());
                                database.updateDayMainPlaceId(mDayId, placeId);
                                for(int j = 0 ;j<position.getIsCheckedPhoto().length;j++) {
                                    Boolean isPhotoCheck = position.getIsCheckedPhoto()[j];
                                    if(isPhotoCheck) {
                                        Long photoId = position.getPhotoList().get(i).getPhotoId();
                                        mDay.setMainPhoto(database.selectPhoto(photoId));
                                        mDay.setMainPhoto(position.getPhotoList().get(i));
                                        database.updateDayMainPhotoId(mDayId,photoId);
                                        break;
                                    }
                                }
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        }
                    }

                    Intent intent = new Intent(getBaseContext(), GalleryUpload.class);
                    String json = convertObjectToJson();
                    intent.putExtra("data",json);
                    intent.putExtras(getIntent().getExtras());
                    startActivity(intent);
                    flag = false;
                }
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
    protected String convertObjectToJson(){
        Gson gson = new Gson();
        return gson.toJson(mDay);
    }
}
