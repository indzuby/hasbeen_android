package co.hasBeen.gallery;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import co.hasBeen.R;
import co.hasBeen.database.DatabaseHelper;
import co.hasBeen.model.api.Photo;
import co.hasBeen.utils.HasBeenDate;

/**
 * Created by 주현 on 2015-03-05.
 */
public class GalleryPhotoEdit extends Activity {
    Long mPhotoId;
    Photo mPhoto;
    DatabaseHelper database;
    ImageView mEditButton;
    boolean isEdit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPhotoId = getIntent().getLongExtra("id",0);
        try{
            database = new DatabaseHelper(this);
            mPhoto = database.selectPhoto(mPhotoId);
            isEdit = false;
            init();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    protected void init() throws Exception{
        setContentView(R.layout.gallery_photo_edit);
        View back = findViewById(R.id.back);
        TextView date = (TextView) findViewById(R.id.date);
        TextView placeName = (TextView) findViewById(R.id.placeName);
        date.setText(HasBeenDate.convertDate(mPhoto.getTakenTime()));
        placeName.setText(database.selectPlaceNameByPlaceId(mPhoto.getPlaceId()));
        EditText description = (EditText) findViewById(R.id.description);
        description.setText(mPhoto.getDescription());
        description.setSelection(mPhoto.getDescription().length());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        mEditButton = (ImageView) findViewById(R.id.editButton);
        mEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEdit) {
                    TextView description = (TextView) findViewById(R.id.description);
                    String des = description.getText().toString();
                    if (des.length() >= 2 && des.length() <= 255) {
                        mPhoto.setDescription(des);
                        try {
                            database.updatePhoto(mPhoto);
                            Toast.makeText(getBaseContext(), getString(R.string.description_ok), Toast.LENGTH_LONG).show();
                            Intent intent = getIntent();
                            intent.putExtra("id",mPhotoId);
                            setResult(RESULT_OK,intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getBaseContext(), getString(R.string.description_size_error), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                isEdit = true;
                mEditButton.setImageResource(R.drawable.edit_done_green);
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
