package co.hasBeen.day;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.model.api.Category;
import co.hasBeen.model.api.Place;
import co.hasBeen.model.api.Position;
import co.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-22.
 */
public class DayPlaceAdapter extends BaseAdapter{
    List<Category> mCategories;
    Position mPosition;
    Context mContext;
    DataBaseHelper database;
    public int mIndex;
    String  mAccessToken;
    public DayPlaceAdapter(Context context, List<Category> categories, Position position) {
        mCategories = categories;
        mContext = context;
        mPosition = position;
        database = new DataBaseHelper(mContext);
        mAccessToken = Session.getString(mContext, "accessToken", null);
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Category getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public boolean getType(int position) {
        if(mCategories.get(position).getVenueId()!=null && mCategories.get(position).getVenueId().equals("first")) return true;
        return false;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        boolean type = getType(position);
        Category category = getItem(position);
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view==null) {
            if(type)
                view = inflater.inflate(R.layout.gallery_place_first_item, null);
            else
                view = inflater.inflate(R.layout.gallery_place_item, null);
        }

        if(type) {
            if(view!=null) {
                if(view.findViewById(R.id.placeIcon)!=null)
                    view = inflater.inflate(R.layout.gallery_place_first_item, null);
            }
        }else {
            if(view!=null) {
                if(view.findViewById(R.id.placeIcon)==null)
                view = inflater.inflate(R.layout.gallery_place_item, null);
            }
            TextView placeName = (TextView) view.findViewById(R.id.name);
            TextView placeCategory = (TextView) view.findViewById(R.id.placeIcon);
            ImageView placeIcon = (ImageView) view.findViewById(R.id.place_icon);
            placeName.setText(category.getPlaceName());
            placeCategory.setText(category.getCategryName());

            Glide.with(mContext).load(category.getCategoryIconPrefix() + "88" + category.getCategoryIconSuffix()).into(placeIcon);
            RelativeLayout placeBox = (RelativeLayout) view.findViewById(R.id.place_box);
            placeBox.setOnClickListener(new PlaceBoxListner(category));

        }
        return view;
    }
    class PlaceBoxListner implements View.OnClickListener {
        Category category;
        PlaceBoxListner(Category category) {
            this.category = category;
        }

        @Override
        public void onClick(View v) {
            Place place = mPosition.getPlace();
            updatePlace(place,category);
            new DayChangePlaceAsyncTask(new Handler(Looper.getMainLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if(msg.what==0) {
                        ((Activity) mContext).setResult(Activity.RESULT_OK);
                        ((Activity)mContext).finish();
                    }
                }
            }).execute(mAccessToken,mPosition.getId(),place);
        }
    }
    void updatePlace(Place place, Category category){
        place.setVenueId(category.getVenueId());
        place.setCategoryId(category.getCategoryId());
        place.setCategoryName(category.getCategryName());
        place.setCategoryIconSuffix(category.getCategoryIconSuffix());
        place.setCategoryIconPrefix(category.getCategoryIconPrefix());
        place.setCountry(category.getCountry());
        place.setCity(category.getCity());
        place.setName(category.getPlaceName());
        place.setLat(category.getLat());
        place.setLon(category.getLon());
    }
}
