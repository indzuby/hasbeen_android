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
    int mCheckedCount;
    ImageButton[] mCheckedButton;
    boolean mCheckedAll = true;
    ToggleButton mCheckedAllButton = null;
    Boolean[] isChecked;
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
        if (mCheckedAll) {
            mCheckedAll = false;
            mCheckedCount = 0;
            for (int i = 0; i < getCount(); i++) {
                isChecked[i] = false;
                mCheckedButton[i].setVisibility(View.GONE);
            }
        } else {
            mCheckedAll = true;
            mCheckedCount = getCount();
            for (int i = 0; i < getCount(); i++) {
                isChecked[i] = true;
                mCheckedButton[i].setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Photo photo = getItem(position);
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.gallery_share_view, null);

        }
        ImageView imageView = (ImageView) view.findViewById(R.id.view_gallery);
        int width = mContext.getResources().getDisplayMetrics().widthPixels;

        int px = Util.pxFromDp(mContext, 1);
        imageView.setPadding(px, px, px, px);
        imageView.getLayoutParams().height = width * 4 / 15 - px * 2;
        imageView.getLayoutParams().width = width * 4 / 15 - px * 2;

        ImageButton check = (ImageButton) view.findViewById(R.id.checked);
        if(mCheckedButton[position]==null)
            mCheckedButton[position] = check;
        imageView.setOnClickListener(new PhotoListener(position));
        Glide.with(mContext).load(photo.getPhotoPath())
                .centerCrop()
                .crossFade().placeholder(R.drawable.placeholder)
                .into(imageView);
        return view;
    }

    public GallerySharePhotoAdapter(Context context, List imagePath, Boolean[] isChecked) {
        super(context, imagePath);
        mCheckedButton = new ImageButton[imagePath.size()];
        mCheckedCount = imagePath.size();
        this.isChecked = isChecked;
    }
    class PhotoListener implements View.OnClickListener {
        int position;

        public PhotoListener(int position) {
            this.position = position;
        }
        @Override
        public void onClick(View v) {
            ImageButton btn = mCheckedButton[position];
            if (isChecked[position]) {
                btn.setVisibility(View.GONE);
                mCheckedCount--;
                if (mCheckedCount == 0) {
                    if (mCheckedAllButton != null) {
                        mCheckedAllButton.setChecked(false);
                        mCheckedAll = false;
                    }
                }
                isChecked[position] = false;
            } else {
                btn.setVisibility(View.VISIBLE);
                mCheckedCount++;
                if (mCheckedAllButton != null) {
                    mCheckedAllButton.setChecked(true);
                    mCheckedAll = true;
                }
                isChecked[position] = true;
            }
        }
    }
}
