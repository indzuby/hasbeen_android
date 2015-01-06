package example.test.opencv;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button btnFirst, btnSecond, btnCalc,btnGallery;
    final static int REQUEST_CODE_IMAGE = 2001;
    Mat fromMat, toMat;
    Bitmap fromImg,toImg;
    int btnNumber;
    Mat hsvFrom;
    Mat hsvTo;

    MatOfInt histSize;
    MatOfFloat ranges ;
    MatOfInt[] channels ;
    Mat histFrom;
    Mat histTo;
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
        btnFirst = (Button) findViewById(R.id.btn_first);
        btnSecond = (Button) findViewById(R.id.btn_second);
        btnCalc = (Button) findViewById(R.id.btn_calc);
        btnGallery = (Button) findViewById(R.id.btn_gallery);
        btnFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnNumber = 1;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);
            }
        });
        btnSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnNumber = 2;
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_IMAGE);

            }
        });
        btnCalc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               //double similarity = compareHistogram(fromMat,toMat);
               int det1 = detectEdge(fromImg);
               int det2 = detectEdge(toImg);
               Toast.makeText(getBaseContext(),det1+" "+det2,Toast.LENGTH_LONG).show();
            }
        });
        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(),GalleryActivity.class);
                startActivity(intent);

            }
        });
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {



                } //break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    @Override
    public void onResume() {
        super.onResume();

        fromMat = new Mat();
        toMat = new Mat();
        hsvFrom = new Mat();
        hsvTo = new Mat();

        histSize = new MatOfInt(25);
        ranges = new MatOfFloat(0f,256f);
        channels = new MatOfInt[]{new MatOfInt(0),new MatOfInt(1)};
        histFrom=new Mat();
        histTo=new Mat();

        overridePendingTransition(0, 0);
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data!=null) {
            Uri selectUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor imageCursor = this.getContentResolver().query(selectUri,filePathColumn,null,null,null);
            imageCursor.moveToFirst();
            int columnIndex = imageCursor.getColumnIndex(filePathColumn[0]);
            String imgPath = imageCursor.getString(columnIndex);
            imageCursor.close();

            if(btnNumber == 1) {
                fromImg = BitmapFactory.decodeFile(imgPath);
                fromMat = new Mat();
                Utils.bitmapToMat(fromImg,fromMat);
                Log.i("bitmapToMat from",fromMat.toString());
            }
            else {
                toMat = new Mat();
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imgPath, options);
                options.inJustDecodeBounds = false;
                options.inSampleSize = 1;
                if( options.outWidth > 256 ) {

                    int ws = options.outWidth / 256 + 1;
                    if( ws > options.inSampleSize )
                        options.inSampleSize = ws;
                }
                if( options.outHeight > 256 ) {

                    int hs = options.outHeight / 256 + 1;
                    if( hs > options.inSampleSize )
                        options.inSampleSize = hs;
                }
                toImg = BitmapFactory.decodeFile(imgPath,options);
                Utils.bitmapToMat(toImg, toMat);
                Log.i("bitmapToMat to",toMat.toString());
            }
        }
    }
    protected double compareHistogram(Mat fromMat, Mat toMat) {
        hsvFrom = new Mat(fromImg.getWidth(),fromImg.getHeight(),CvType.CV_8UC1);
        Utils.bitmapToMat(fromImg,hsvFrom);
        hsvTo = new Mat(toImg.getWidth(),toImg.getHeight(),CvType.CV_8UC1);
        Utils.bitmapToMat(toImg,hsvTo);
//        if(!hsvFrom.empty())
            Imgproc.cvtColor(hsvFrom,hsvFrom,Imgproc.COLOR_BGR2HSV);
//        if(!hsvTo.empty())
            Imgproc.cvtColor(hsvTo,hsvTo,Imgproc.COLOR_BGR2HSV);

        Imgproc.calcHist(Arrays.asList(hsvFrom), new MatOfInt(0), new Mat(), histFrom, histSize, ranges);
//        Core.normalize(histFrom, histFrom, sizeFrom.height / 2, 0, Core.NORM_INF);
        Imgproc.calcHist(Arrays.asList(hsvTo),new MatOfInt(0),new Mat(),histTo,histSize,ranges);
//        Core.normalize(histTo, histTo, sizeTo.height / 2, 0, Core.NORM_INF);

        return Imgproc.compareHist(histFrom,histTo,Imgproc.CV_COMP_CORREL);
    }
    protected int detectEdge(Bitmap img) {
        int threshold = 100;
        Mat srcGray = new Mat();
        Mat hierarchy = new Mat();
        List<MatOfPoint> countours = new ArrayList<>();
        Mat src = new Mat();
        Utils.bitmapToMat(img, src);
        Imgproc.cvtColor(src,srcGray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(srcGray, srcGray, new Size(3, 3));
        Mat cannyOutput = new Mat();
        Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2, 3, true);
        Imgproc.findContours(cannyOutput, countours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int edgeCnt = countours.size();
        return edgeCnt;
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
