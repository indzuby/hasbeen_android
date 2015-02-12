package example.test.hasBeen.gallery;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import example.test.hasBeen.R;
import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.model.database.Category;
import example.test.hasBeen.model.database.Photo;
import example.test.hasBeen.model.database.Place;
import example.test.hasBeen.model.database.Position;
import example.test.hasBeen.utils.Session;

/**
 * Created by zuby on 2015-01-22.
 */
public class GalleryPlaceAdapter extends BaseAdapter{
    static final int RESULT = 2;
    List<Category> mCategories;
    Position mPosition;
    Context mContext;
    DatabaseHelper database;
    public int mIndex;
    public GalleryPlaceAdapter(Context context ,List<Category> categories,Position position) {
        mCategories = categories;
        mContext = context;
        mPosition = position;
        database = new DatabaseHelper(mContext);
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
            TextView placeName = (TextView) view.findViewById(R.id.profileName);
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
            updatePlace(category);
            Session.putBoolean(mContext, "placeChange", true);
            Log.i("index", mIndex + "");
            ((Activity) mContext).setResult(RESULT);
            ((Activity)mContext).finish();

        }
    }
    void updatePlace(Category category){
        try {
            Long placeId;
            if (database.hasVenueId(category.getVenueId()))
                placeId = database.getPlaceIdByVenueId(category.getVenueId());
            else
                placeId = insertPlace(category);

            mPosition.setPlaceId(placeId);
            Log.i("place id ",placeId +"");
            database.updatePosition(mPosition);
            Log.i("place id changed",database.selectPosition(mPosition.getId()).getPlaceId()+"");
            database.updatePhotosPlaceId(mPosition.getId(),placeId);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    Long insertPlace(Category category) throws Exception{
        Place place = new Place();
        Photo photo = database.selectPhoto(mPosition.getMainPhotoId());
        place.setVenueId(category.getVenueId());
        place.setCategoryId(category.getCategoryId());
        place.setCategoryName(category.getCategryName());
        place.setCountry(photo.getCountry());
        place.setCity(photo.getCity());
        place.setName(category.getPlaceName());
        place.setLat(photo.getLat());
        place.setLon(photo.getLon());
        place.setCategoryIconPrefix(category.getCategoryIconPrefix());
        place.setCategoryIconSuffix(category.getCategoryIconSuffix());
        place.setMainPhotoId(photo.getId());
        return database.insertPlace(place);
    }

}
