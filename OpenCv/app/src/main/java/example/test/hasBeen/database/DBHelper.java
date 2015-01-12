package example.test.hasBeen.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import example.test.hasBeen.model.HasBeenDay;
import example.test.hasBeen.model.HasBeenPhoto;
import example.test.hasBeen.model.HasBeenPlace;

/**
 * Created by zuby on 2015-01-09.
 */
public class DBHelper extends SQLiteOpenHelper {

    private static final String TAG= "DATABASE";
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "hasBeen";
    Context mContext;
    SQLiteDatabase mDb;
    public DBHelper(Context context) {
        super(context,DB_NAME,null,DB_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDb = db;
        String placeQuery = CreateTable("place",
                "id INTEGER PRIMARY KEY AUTOINCREMENT",
                "foursquare_venue_id TEXT",
                "foursquare_category_id TEXT",
                "category_name TEXT",
                "country TEXT NOT NULL",
                "city TEXT NOT NULL",
                "name TEXT NOT NULL",
                "lat REAL NOT NULL",
                "lon REAL NOT NULL",
                "category_icon TEXT NOT NULL",
                "main_photo_id INTEGER");
        String dayQuery = CreateTable("day",
                "id INTEGER PRIMARY KEY AUTOINCREMENT",
                "title TEXT NOT NULL",
                "description TEXT NOT NULL",
                "photo_count INTEGER NOT NULL",
                "date TEXT NOT NULL",
                "country TEXT NOT NULL",
                "city TEXT NOT NULL",
                "main_photo_id INTEGER");
        String photoQuery = CreateTable("photo",
                "id INTEGER PRIMARY KEY AUTOINCREMENT",
                "title TEXT NOT NULL",
                "description TEXT NOT NULL",
                "country TEXT NOT NULL",
                "city TEXT NOT NULL",
                "place_name TEXT NOT NULL",
                "lat REAL NOT NULL",
                "lon REAL NOT NULL",
                "taken_date TEXT NOT NULL",
                "day_id INTEGER",
                "position_id INTEGER");
        String positionQuery = CreateTable("position",
                "id INTEGER PRIMARY KEY",
                "day_id INTEGER NOT NULL",
                "type TEXT NOT NULL",
                "start_date TEXT NOT NULL",
                "end_date TEXT NOT NULL",
                "place_id INTEGER");
        Log.i(TAG,"DB create success");
        db.execSQL(placeQuery);
        db.execSQL(dayQuery);
        db.execSQL(photoQuery);
        db.execSQL(positionQuery);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older books table if existed
        db.execSQL("DROP TABLE IF EXISTS place");
        db.execSQL("DROP TABLE IF EXISTS photo");
        db.execSQL("DROP TABLE IF EXISTS day");
        db.execSQL("DROP TABLE IF EXISTS position");
        // create fresh books table
        this.onCreate(db);
    }

    public String CreateTable(String tableName, String... attributes){

        String query = "CREATE TABLE "+tableName+" (";
        for(int i = 0; i<attributes.length;i++)
            query+=attributes[i]+",";
        query = query.substring(0,query.length()-1)+")";
        return query;
    }
    public long insertDay(HasBeenDay day){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("title",day.getTitle());
        values.put("description",day.getDescription());
        values.put("photo_count",day.getPhoto_count());
        values.put("date",day.getDate());
        values.put("country",day.getCountry());
        values.put("city",day.getCity());
        values.put("main_photo_id",day.getMain_photo_id());
        long id = db.insert("day",null,values);
        return id;
    }
    public long insertPhoto(HasBeenPhoto photo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",photo.getId());
        values.put("title",photo.getTitle());
        values.put("description",photo.getDescription());
        values.put("country",photo.getCountry());
        values.put("city",photo.getCity());
        values.put("place_name",photo.getPlace_name());
        values.put("lat",photo.getLat());
        values.put("lon",photo.getLon());
        values.put("taken_date",photo.getTaken_date());
        values.put("day_id",photo.getDay_id());
        values.put("position_id",photo.getPosition_id());
        long id = db.insert("photo",null,values);
        return id;
    }
    public void clearTable(){

        mContext.deleteDatabase("hasBeen");
    }
    public HasBeenPhoto selectLastPhoto(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM photo";
        Cursor c = db.rawQuery(query,null);
        if(c!=null){
            c.moveToLast();
            HasBeenPhoto photo = new HasBeenPhoto(
                    c.getInt(c.getColumnIndex("id")),
                    c.getString(c.getColumnIndex("title")),
                    c.getString(c.getColumnIndex("description")),
                    c.getString(c.getColumnIndex("country")),
                    c.getString(c.getColumnIndex("city")),
                    c.getString(c.getColumnIndex("place_name")),
                    c.getFloat(c.getColumnIndex("lat")),
                    c.getFloat(c.getColumnIndex("lon")),
                    c.getString(c.getColumnIndex("taken_date")),
                    c.getInt(c.getColumnIndex("day_id")),
                    c.getInt(c.getColumnIndex("position_id"))
                    );
            return photo;
        }else return null;
    }
    public boolean isEmty(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM photo";
        Cursor c = db.rawQuery(query,null);
        if(c==null) return true;
        return false;
    }
}
