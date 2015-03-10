package co.hasBeen.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by zuby on 2015-01-23.
 */
public class Session {
//    public final static String DOMAIN = "http://testapi.hasbeen.co/";
//    public final static String DOMAIN = "http://api.mango4.me/";
    public final static String DOMAIN = "http://192.168.0.5:8080/";
    public final static String WEP_DOMAIN = "http://www.hasbeen.co/#/";
    public static void putString(Context context,String key , String value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key,value);
        editor.commit();
    }
    public static void putBoolean(Context context,String key , boolean value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean(key,value);
        editor.commit();
    }
    public static void putLong(Context context,String key , long value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putLong(key,value);
        editor.commit();
    }
    public static void putInt(Context context,String key , int value){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(key,value);
        editor.commit();
    }
    public static void remove(Context context, String key){
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(key);
        editor.commit();
    }
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key,defValue);
    }
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(key,defValue);
    }
    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getLong(key,defValue);
    }
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getInt(key,defValue);
    }
}
