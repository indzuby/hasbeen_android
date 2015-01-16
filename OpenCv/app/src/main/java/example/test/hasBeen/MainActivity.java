package example.test.hasBeen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.opencv.android.OpenCVLoader;

import java.util.Arrays;

import example.test.hasBeen.database.DatabaseHelper;
import example.test.hasBeen.gallery.GalleryActivity;
import example.test.hasBeen.gallery.GalleryDayListActivity;


public class MainActivity extends ActionBarActivity {

    private String TAG = "MainActivity";
    Button btnGallery,btnMap,btnDB,btnDBclear;
    LoginButton authButton;

    static {
        if (!OpenCVLoader.initDebug()) {
            // Handle initialization error
        } else {
            System.loadLibrary("opencv_java");
            System.loadLibrary("opencv_info");
            //System.loadLibrary("opencv_core");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnGallery = (Button) findViewById(R.id.btn_gallery);
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GalleryActivity.class);
                startActivity(intent);

            }
        });
        btnMap = (Button) findViewById(R.id.btn_map);
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MapActivity.class);
                startActivity(intent);
            }
        });
        authButton = (LoginButton) findViewById(R.id.btn_facebookLogin);
        // set permission list, Don't foeget to add email
        authButton.setReadPermissions(Arrays.asList("user_likes", "email"));
        // session state call back event
        authButton.setSessionStatusCallback(new Session.StatusCallback() {

            @Override
            public void call(Session session, SessionState state, Exception exception) {

                if (session.isOpened()) {
                    Log.i(TAG, "Access Token" + session.getAccessToken());
                    Request.executeMeRequestAsync(session,
                            new Request.GraphUserCallback() {
                                @Override
                                public void onCompleted(GraphUser user, Response response) {
                                    if (user != null) {
                                        Log.i(TAG, "User ID " + user.getId());
                                        Log.i(TAG, "Email " + user.asMap().get("email"));
                                        Toast.makeText(getBaseContext(), user.asMap().get("email").toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
//        final DBHelper db = new DBHelper(getBaseContext());
        final DatabaseHelper dbHelper = new DatabaseHelper(this);
        btnDB = (Button) findViewById(R.id.btn_db);
        btnDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), GalleryDayListActivity.class);
                startActivity(intent);


            }
        });
        btnDBclear = (Button) findViewById(R.id.btn_db_clear);
        btnDBclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.clearTable();
            }
        });

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
