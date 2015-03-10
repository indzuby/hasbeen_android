package co.hasBeen.map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;

import co.hasBeen.model.api.Day;

/**
 * Created by 주현 on 2015-03-06.
 */
public class EnterMapLisnter implements View.OnClickListener{
    Context context;
    public final static int REQUEST_CODE = 2002;
    Day mDay;
    Long positionId;
    public EnterMapLisnter(Context context, Day mDay,Long positionId) {
        this.context = context;
        this.mDay = mDay;
        this.positionId = positionId;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(context, MapActivity.class);
        Gson gson = new Gson();
        String data = gson.toJson(mDay);
        intent.putExtra("day",data);
        intent.putExtra("positionId",positionId);
        ((Activity)context).startActivityForResult(intent, REQUEST_CODE);
    }
}
