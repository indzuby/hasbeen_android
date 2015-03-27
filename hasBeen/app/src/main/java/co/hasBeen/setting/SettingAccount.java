package co.hasBeen.setting;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.localytics.android.Localytics;

import org.joda.time.DateTime;

import java.util.Calendar;
import java.util.GregorianCalendar;

import co.hasBeen.R;
import co.hasBeen.model.api.User;
import co.hasBeen.utils.HasBeenDate;
import co.hasBeen.utils.Session;

/**
 * Created by 주현 on 2015-03-25.
 */
public class SettingAccount extends ActionBarActivity implements View.OnClickListener {
    final static long DEFAULT_BIRTHDAY = 1000;
    User mUser;
    String mAccessToken;
    EditText firstName,lastName,city,country;
    TextView birthDay;
    View male,female,changePassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_account);
        mAccessToken = Session.getString(this,"accessToken",null);
        mLoading = findViewById(R.id.refresh);
        startLoading();
        initActionBar();
        new AccountAsyncTask(userHandler).execute(mAccessToken);
    }

    Handler userHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==0) {
                mUser = (User) msg.obj;
                init();
            }else {

            }
        }
    };

    protected void init(){
        firstName = (EditText) findViewById(R.id.firstName);
        lastName = (EditText) findViewById(R.id.lastName);
        birthDay = (TextView) findViewById(R.id.birthday);
        city = (EditText) findViewById(R.id.city);
        country = (EditText) findViewById(R.id.country);
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        changePassword = findViewById(R.id.changePassword);

        firstName.setText(mUser.getFirstName());
        firstName.setSelection(mUser.getFirstName().length());
        lastName.setText(mUser.getLastName());
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(mUser.getBirthDay());
        if(calendar.get(Calendar.YEAR)!=DEFAULT_BIRTHDAY)
            birthDay.setText(HasBeenDate.convertDate(mUser.getBirthDay()));
        city.setText(mUser.getCity());
        country.setText(mUser.getCountry());
        if(mUser.getGender()!=null) {
            if(mUser.getGender()==User.Gender.MALE)
                male.setSelected(true);
            else
                female.setSelected(true);
        }
        if(mUser.getSignUpType() != User.SignUpType.FACEBOOK)
            changePassword.setVisibility(View.VISIBLE);
        male.setOnClickListener(this);
        female.setOnClickListener(this);
        changePassword.setOnClickListener(this);
        birthDay.setOnClickListener(this);
        stopLoading();
    }
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            Calendar calendar = new GregorianCalendar();
            calendar.set(year,monthOfYear,dayOfMonth);
            mUser.setBirthDay(calendar.getTimeInMillis());
            birthDay.setText(new DateTime(calendar.getTime()).toString("yyyy.MM.dd"));
        }
    };
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.male) {
            male.setSelected(true);
            female.setSelected(false);
        }else if(v.getId()==R.id.female) {
            female.setSelected(true);
            male.setSelected(false);
        }else if(v.getId()==R.id.changePassword) {
            Intent intent = new Intent(getBaseContext(), ChangePassword.class);
            startActivity(intent);
        }else if(v.getId()==R.id.birthday) {
            showDatePicker();
        }
    }
    public void showDatePicker(){
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(mUser.getBirthDay());
        if(calendar.get(Calendar.YEAR)==DEFAULT_BIRTHDAY)
            calendar = new GregorianCalendar();
        new DatePickerDialog(SettingAccount.this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    protected void initActionBar(){
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.theme_color));
        actionBar.setBackgroundDrawable(colorDrawable);

        View mCustomActionBar = mInflater.inflate(R.layout.action_bar_default, null);
        ImageButton back = (ImageButton) mCustomActionBar.findViewById(R.id.actionBarBack);
        TextView titleView = (TextView) mCustomActionBar.findViewById(R.id.actionBarTitle);
        titleView.setText(getString(R.string.action_bar_account_title));
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        actionBar.setCustomView(mCustomActionBar);
        actionBar.setDisplayShowCustomEnabled(true);
        View done = mCustomActionBar.findViewById(R.id.actionBarDone);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUser!=null) {
                    startLoading();
                    initData();
                    new EditAccountAsyncTask(new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            stopLoading();
                            if(msg.what==0) {
                                Toast.makeText(getBaseContext(),getString(R.string.account_update_success),Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(getBaseContext(),getString(R.string.common_error),Toast.LENGTH_LONG).show();
                            }
                        }
                    }).execute(mAccessToken,mUser);
                }
            }
        });
    }
    protected void initData(){
        mUser.setFirstName(firstName.getText().toString());
        mUser.setLastName(lastName.getText().toString());
        mUser.setCity(city.getText().toString());
        mUser.setCountry(country.getText().toString());
        if(male.isSelected())
            mUser.setGender(User.Gender.MALE);
        else
            mUser.setGender(User.Gender.FEMALE);
    }
    @Override
    public void onResume()
    {
        super.onResume();
        Localytics.openSession();
        Localytics.tagScreen("Setting Account View");
        Localytics.upload();
    }
    View mLoading;
    boolean isLoading;
    protected void startLoading() {
        isLoading = true;
        mLoading.setVisibility(View.VISIBLE);
        Animation rotate = AnimationUtils.loadAnimation(getBaseContext(), R.anim.rotate);
        mLoading.startAnimation(rotate);
    }

    protected void stopLoading() {
        isLoading = false;
        mLoading.setVisibility(View.GONE);
        mLoading.clearAnimation();
    }


}
