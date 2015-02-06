package example.test.hasBeen.account;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import example.test.hasBeen.R;

/**
 * Created by 주현 on 2015-02-04.
 */
public class SignUpActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        init();
        super.onCreate(savedInstanceState);
    }
    protected  void init()
    {
        setContentView(R.layout.signup);
        TextView goSignIn = (TextView) findViewById(R.id.goSignIn);
        goSignIn.setOnClickListener(new View.OnClickListener() {
            boolean flag = false;

            @Override
            public void onClick(View v) {
                if (!flag) {
                    flag = true;
                    Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                    startActivity(intent);
                    flag = false;
                }
            }
        });

    }
}
