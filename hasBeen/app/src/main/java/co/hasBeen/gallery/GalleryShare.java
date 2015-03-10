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
import co.hasBeen.model.api.Day;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-20.
 */
public class GalleryShare extends ActionBarActivity{
    final static int REQUEST_EXIT = 2001;
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
        database = new DatabaseHelper(this);
        mListView = (ListView) findViewById(R.id.listView);
        mTextDate = (TextView) findViewById(R.id.date);
        mTextArea = (TextView) findViewById(R.id.placeName);
        try {
            mDay = database.selectDay(mDayId);
            mTextDate.setText(HasBeenDate.convertDate(mDay.getDate()));
            mDay.setPositionList(database.selectPositionByDayId(mDayId));
            initPositionItem(mDay.getPositionList());

            mTextArea.setText(Util.convertPlaceName(mDay.getPositionList()));
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
        TextView title = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        title.setText("Share Day");
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
                                mDay.setMainPlace(position.getPlace());
                                mDay.setMainPlaceId(placeId);
                                database.updateDayMainPlaceId(mDayId, placeId);
                                for(int j = 0 ;j<position.getIsCheckedPhoto().length;j++) {
                                    Boolean isPhotoCheck = position.getIsCheckedPhoto()[j];
                                    if(isPhotoCheck) {
                                        Long photoId = position.getPhotoList().get(j).getId();
                                        mDay.setMainPhoto(position.getPhotoList().get(j));
                                        mDay.setMainPhotoId(photoId);
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
                    startActivityForResult(intent,REQUEST_EXIT);
                    flag = false;
                }
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
    }
    protected void initPositionItem(List<Position> positions) throws Exception{
        for(Position position : positions) {
            position.setPlace(database.selectPlace(position.getPlaceId()));
            position.setPhotoList(database.selectPhotoByPositionId(position.getId()));
            Boolean[] isCheckedPhoto = new Boolean[position.getPhotoList().size()];
            Arrays.fill(isCheckedPhoto,true);
            position.setIsCheckedPhoto(isCheckedPhoto);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_EXIT) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
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
