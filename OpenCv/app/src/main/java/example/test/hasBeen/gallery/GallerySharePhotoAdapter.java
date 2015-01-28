package example.test.hasBeen.gallery;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.utils.Util;

/**
 * Created by zuby on 2015-01-21.
 */
public class GallerySharePhotoAdapter extends GalleryAdapter {
    public boolean[] mChecked;
    int mCheckedCount;
    public ImageButton[] mCheckedButton;
    boolean mCheckedAll = true;
    ToggleButton mCheckedAllButton = null;
    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Photo getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void setChecked() {
        if(mCheckedAll) {
            mCheckedAll = false;
            mCheckedCount = 0 ;
            for(int i = 0 ; i<getCount();i++) {
                mChecked[i] = false;
                mCheckedButton[i].setVisibility(View.INVISIBLE);
            }
        }else {
            mCheckedAll = true;
            mCheckedCount = getCount() ;
            for(int i = 0 ; i<getCount();i++) {
                mChecked[i] = true;
                mCheckedButton[i].setVisibility(View.VISIBLE);
            }
        }

    }
    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        final Photo photo = getItem(position);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_share_view, null);

        }
        ImageView imageView = (ImageView) view.findViewById(R.id.view_gallery);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;

        int px = Util.pxFromDp(mContext, 2);
        imageView.setPadding(px, px, px, px);
        imageView.getLayoutParams().height = width * 4 / 15 - 12;
        imageView.getLayoutParams().width = width * 4 / 15 - 12;
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        final ImageButton check = (ImageButton) view.findViewById(R.id.checked);
        mCheckedButton[position] = check;
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChecked[position]) {
                    check.setVisibility(View.INVISIBLE);
                    mCheckedCount-- ;
                    if(mCheckedCount==0) {
                        if(mCheckedAllButton!=null)
                            mCheckedAllButton.setChecked(true);
                    }
                }
                else {
                    check.setVisibility(View.VISIBLE);
                    mCheckedCount++ ;
                    if(mCheckedAllButton!=null)
                        mCheckedAllButton.setChecked(false);
                }
                mChecked[position] = !mChecked[position];
            }
        });
        Glide.with(mContext).load(photo.getPhotoPath())
                .centerCrop()
                .crossFade().placeholder(R.drawable.loading)
                .into(imageView);
        return view;
    }

    public GallerySharePhotoAdapter(Context context, List imagePath) {
        super(context, imagePath);
        mChecked = new boolean[imagePath.size()];
        mCheckedButton = new ImageButton[imagePath.size()];
        for(int i = 0; i < mChecked.length;i++)
            mChecked[i]= true;
        mCheckedCount = imagePath.size();
    }
}
