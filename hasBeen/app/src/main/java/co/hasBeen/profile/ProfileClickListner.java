package co.hasBeen.profile;

import android.content.Context;
import android.content.Intent;
import android.view.View;

/**
 * Created by 주현 on 2015-02-06.
 */
public class ProfileClickListner implements View.OnClickListener {
    Context mContext;
    Long mId;
    public ProfileClickListner(Context context, Long id) {
        mContext = context;
        mId = id;
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(mContext,ProfileView.class);
        intent.putExtra("userId",mId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}
