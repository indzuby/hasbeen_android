package co.hasBeen.utils;

import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import co.hasBeen.R;

/**
 * Created by 주현 on 2015-03-18.
 */
public class HasBeenFragment extends Fragment {
    public View mView;
    boolean showTab = false;
    public void setShowTab(){
        showTab = true;
    }
    public boolean isShowTab(){
        return showTab;
    }
    public void showTab(){
    }
    public boolean isLoading;
    protected void startLoading() {
        isLoading = true;
        try {
            View loading = mView.findViewById(R.id.refresh);
            loading.setVisibility(View.VISIBLE);
            Animation rotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);
            loading.startAnimation(rotate);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(0);
        }
    }

    protected void stopLoading() {
        isLoading = false;
        View loading = mView.findViewById(R.id.refresh);
        loading.setVisibility(View.GONE);
        loading.clearAnimation();
    }
}
