package co.hasBeen.search;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.hasBeen.R;
import co.hasBeen.database.DataBaseHelper;
import co.hasBeen.model.api.RecentSearch;

/**
 * Created by 주현 on 2015-03-27.
 */
public class RecentAdapter extends BaseAdapter{
    Context mContext;
    List<RecentSearch> mRecentList;
    DataBaseHelper database;
    String mType;
    public RecentAdapter(Context mContext,String type) {
        this.mContext = mContext;
        database = new DataBaseHelper(mContext);
        mType = type;
        try {
            mRecentList = database.getRecentSearch(type);
            if(mRecentList==null) {
                mRecentList = new ArrayList<>();
                mRecentList.add(noRecent());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public RecentSearch noRecent(){
        RecentSearch noRecent = new RecentSearch();
        noRecent.setId(0L);
        return noRecent;
    }
    @Override
    public int getCount() {
        return mRecentList.size();
    }

    @Override
    public RecentSearch getItem(int position) {
        return mRecentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mRecentList.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.search_recent,null);
        }
        final TextView keyword = (TextView) view.findViewById(R.id.keyword);
        View cancel = view.findViewById(R.id.cancel);
        if(getItemId(position)==0) {
            keyword.setText("");
            cancel.setVisibility(View.GONE);
        }
        else {
            final RecentSearch recent = getItem(position);
            keyword.setText(recent.getKeyword());
            keyword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    keywordTouch(recent.getKeyword(),recent.getPlaceId());
                }
            });
            cancel.setVisibility(View.VISIBLE);
            cancel.setOnClickListener(new removeRecent(position));
        }
        return view;
    }
    protected void keywordTouch(String keyword, String placeId){
        SearchDetailView searchView = ((SearchDetailView)mContext);
        searchView.isKeyPress = true;
        searchView.doSearch(keyword,placeId);
    }
    class removeRecent implements View.OnClickListener {
        int index;

        removeRecent(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            try{
                database.removeRecent(getItem(index).getId());
                mRecentList.remove(index);
                if(mRecentList.size()==0)
                    mRecentList.add(noRecent());
                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
