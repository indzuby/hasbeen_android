package co.hasBeen.utils;

import android.graphics.Bitmap;

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

/**
 * Created by zuby on 2015-01-15.
 */
public class HasBeenOpenCv {

    public static int detectEdge(Bitmap img) {
        int threshold = 100;
        Mat srcGray = new Mat();
        Mat hierarchy = new Mat();
        List<MatOfPoint> countours = new ArrayList<>();
        Mat src = new Mat();
        Utils.bitmapToMat(img, src);
        Imgproc.cvtColor(src, srcGray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.blur(srcGray, srcGray, new Size(3, 3));
        Mat cannyOutput = new Mat();
        Imgproc.Canny(srcGray, cannyOutput, threshold, threshold * 2, 3, true);
        Imgproc.findContours(cannyOutput, countours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        int edgeCnt = countours.size();
        return edgeCnt;

    }

    public static double compareHistogram(Bitmap fromImg, Bitmap toImg) {
        Mat hsvFrom = new Mat(fromImg.getWidth(), fromImg.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(fromImg, hsvFrom);
        Mat hsvTo = new Mat(toImg.getWidth(), toImg.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(toImg, hsvTo);

        Imgproc.cvtColor(hsvFrom, hsvFrom, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(hsvTo, hsvTo, Imgproc.COLOR_BGR2HSV);
        MatOfInt histSize = new MatOfInt(25);
        MatOfFloat ranges = new MatOfFloat(0f, 256f);

        Mat histFrom = new Mat();
        Mat histTo = new Mat();

        Imgproc.calcHist(Arrays.asList(hsvFrom), new MatOfInt(0), new Mat(), histFrom, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(hsvTo), new MatOfInt(0), new Mat(), histTo, histSize, ranges);
        return Imgproc.compareHist(histFrom, histTo, Imgproc.CV_COMP_CORREL);

    }
}
