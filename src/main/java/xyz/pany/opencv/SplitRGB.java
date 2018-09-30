package xyz.pany.opencv;

/**
 * @Title: SplitRGB</p>
 * @Description: TODO
 * @author zhangpan
 * @date 2018年9月30日
 */
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class SplitRGB {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.err.println("start...");
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = Imgcodecs.imread("C:\\Users\\zhangpan\\Desktop\\1.jpg");
		Mat dst = src.clone();
		Imgproc.cvtColor(dst, dst, Imgproc.COLOR_BGRA2GRAY);
		Imgproc.adaptiveThreshold(dst, dst, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 3, 3);

		java.util.List<MatOfPoint> contours = new java.util.ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		Imgproc.findContours(dst, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,
				0));
		System.out.println(contours.size());
		for (int i = 0; i < contours.size(); i++) {
			Imgproc.drawContours(src, contours, i, new Scalar(0, 0, 0, 0), 1);
		}

		Imgcodecs.imwrite("C:\\Users\\zhangpan\\Desktop\\test.jpg", src);
		System.err.println("end...");
	}

}