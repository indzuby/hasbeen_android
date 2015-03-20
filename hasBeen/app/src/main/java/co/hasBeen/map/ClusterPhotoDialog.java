package co.hasBeen.map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.map.pin.PhotoPin;
import co.hasBeen.model.api.Photo;
import co.hasBeen.photo.EnterPhotoListner;
import co.hasBeen.utils.Util;

/**
 * Created by 주현 on 2015-03-12.
 */
public class ClusterPhotoDialog extends Dialog {
    List<Photo> photos;
    GridView gridView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.6f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.pin_photo_dialog);

        setLayout();
        PhotoAdapter adapter= new PhotoAdapter();
        gridView.setAdapter(adapter);
        findViewById(R.id.box).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public ClusterPhotoDialog(Context context, Collection<PhotoPin> photoPins) {
        // Dialog 배경을 투명 처리 해준다.
        super(context, android.R.style.Theme_Translucent_NoTitleBar);
        photos = new ArrayList<>();
        for(PhotoPin photo : photoPins)
            photos.add(photo.getPhoto());
    }
    private void setLayout() {
//        View uploading = findViewById(R.id.refresh);
//        uploading.setVisibility(View.VISIBLE);
//        Animation rotate = AnimationUtils.loadAnimation(getContext(), R.anim.rotate);
//        uploading.startAnimation(rotate);
        gridView = (GridView) findViewById(R.id.gridView);
    }
    class PhotoAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public Photo getItem(int position) {
            return photos.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView view = (ImageView)convertView;
            if(view==null) {
                view = new ImageView(getContext());
                int margin = Util.convertDpToPixel(2,getContext());
                int height = getWidth()-margin;
                int width = getWidth()-margin;
                view.setLayoutParams(new AbsListView.LayoutParams(height,width));

            }
            Photo photo = getItem(position);
            Glide.with(getContext()).load(photo.getMediumUrl()).centerCrop().placeholder(Util.getPlaceHolder(position)).into(view);
            view.setOnClickListener(new EnterPhotoListner(photo.getId(),getContext()));
            return view;
        }
        public int getWidth(){
            return Util.getPhotoHeight(getContext());
        }
    }
}
