package example.test.hasBeen.gallery;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import example.test.hasBeen.R;

/**
 * Created by zuby on 2015-01-16.
 */
public class GalleryFullPhoto extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_photo);
        String path = getIntent().getStringExtra("path");
        ImageView imageView = (ImageView) findViewById(R.id.full_size_photo);
        Glide.with(this).load(path)
                .into(imageView);
    }
}
