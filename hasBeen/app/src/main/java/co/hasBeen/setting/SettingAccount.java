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
    final static long DEFAULT_BIRTHDAY = 1900;
    User mUser;
    String mAccessToken;
    EditText firstName,lastName,city,country,userName;
    TextView birthDay,gender;
    View changePassword;
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
        userName = (EditText) findViewById(R.id.userName);
        birthDay = (TextView) findViewById(R.id.birthday);
        gender = (TextView) findViewById(R.id.gender);
        city = (EditText) findViewById(R.id.city);
        country = (EditText) findViewById(R.id.country);
        changePassword = findViewById(R.id.changePassword);

        firstName.setText(mUser.getFirstName());
        firstName.setSelection(mUser.getFirstName().length());
        lastName.setText(mUser.getLastName());
        if(mUser.getUsername()!=null)
            userName.setText(mUser.getUsername());
        Calendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(mUser.getBirthDay());
        if(calendar.get(Calendar.YEAR)>DEFAULT_BIRTHDAY)
            birthDay.setText(HasBeenDate.convertDate(mUser.getBirthDay()));
        city.setText(mUser.getCity());
        country.setText(mUser.getCountry());
        if(mUser.getGender()!=null) {
            if(mUser.getGender()==User.Gender.MALE)
                gender.setText(getString(R.string.male));
            else
                gender.setText(getString(R.string.female));
        }
        if(mUser.getSignUpType() != User.SignUpType.FACEBOOK)
            changePassword.setVisibility(View.VISIBLE);
        gender.setOnClickListener(this);
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
        if(v.getId()==R.id.gender) {
            GenderDialog genderDialog = new GenderDialog(SettingAccount.this,mUser.getGender());
            genderDialog.setListner(maleListner,femaleListner);
            genderDialog.show();
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
        if(calendar.get(Calendar.YEAR)<=DEFAULT_BIRTHDAY)
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
                    if(isValidateData()) {
                        stopLoading();
                        return ;
                    }
                    new EditAccountAsyncTask(new Handler(Looper.getMainLooper()){
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            stopLoading();
                            if(msg.what==0) {
                                Toast.makeText(getBaseContext(),getString(R.string.account_update_success),Toast.LENGTH_LONG).show();
                                finish();
                            }else {
                                Toast.makeText(getBaseContext(),getString(R.string.username_dup_error),Toast.LENGTH_LONG).show();
                            }
                        }
                    }).execute(mAccessToken,mUser);
                }
            }
        });
    }
    protected boolean isValidateData(){
        if((mUser.getFirstName().length()<1 || mUser.getFirstName().length()>255)
                && (mUser.getLastName().length()<1 || mUser.getLastName().length()>255)
                && (mUser.getUsername().length()<2 || mUser.getUsername().length()>255)) {
            Toast.makeText(this,getString(R.string.name_size_error),Toast.LENGTH_LONG).show();
            return true;
        }
        int count = 0 ;
        for(int i =0 ; i <mUser.getUsername().length();i++) {
            char a = mUser.getUsername().charAt(i);
            if(Character.isLetter(a)) count++;
            if(!Character.isLetterOrDigit(a)) {
                Toast.makeText(this,getString(R.string.username_simbol_error),Toast.LENGTH_LONG).show();
                return true;
            }
        }
        if(count<=0) {
            Toast.makeText(this, getString(R.string.username_error), Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }
    protected void initData(){
        mUser.setFirstName(firstName.getText().toString());
        mUser.setLastName(lastName.getText().toString());
        mUser.setCity(city.getText().toString());
        mUser.setCountry(country.getText().toString());
        mUser.setUsername(userName.getText().toString());
        if(userName.getText().toString().length()<=0)
            mUser.setUsername(null);
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
    View.OnClickListener maleListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gender.setText(getString(R.string.male));
            mUser.setGender(User.Gender.MALE);
        }
    };
    View.OnClickListener femaleListner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            gender.setText(getString(R.string.female));
            mUser.setGender(User.Gender.FEMALE);
        }
    };

}
