package co.hasBeen.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import co.hasBeen.R;
import co.hasBeen.model.network.PlaceAutocomplete;

/**
 * Created by 주현 on 2015-04-08.
 */
public class RecommandSearchAdapter extends BaseAdapter {
    List<PlaceAutocomplete.Predictions> mPredictions;
    Context mContext;

    public RecommandSearchAdapter(List<PlaceAutocomplete.Predictions> mPredictions, Context mContext) {
        this.mPredictions = mPredictions;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mPredictions.size();
    }

    @Override
    public PlaceAutocomplete.Predictions getItem(int position) {
        return mPredictions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.search_recent,null);
        }
        TextView keyword = (TextView) view.findViewById(R.id.keyword);
        View cancel = view.findViewById(R.id.cancel);
        cancel.setVisibility(View.GONE);
        final PlaceAutocomplete.Predictions predictions = getItem(position);
        keyword.setText(predictions.getDescription());
        keyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keywordTouch(predictions.getDescription(),predictions.getPlace_id());
            }
        });
        return view;
    }
    protected void keywordTouch(String keyword,String placeId){
        SearchDetailView searchView = ((SearchDetailView)mContext);
        searchView.isKeyPress = true;
        searchView.doSearch(keyword,placeId);
    }
}
