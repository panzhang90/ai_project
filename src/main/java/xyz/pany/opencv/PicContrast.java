package xyz.pany.opencv;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;



/**
 * @Title: PicContrast</p>
 * @Description: TODO 
 * @author zhangpan 
 * @date 2018年8月22日 
 */
public class PicContrast {
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME );  
		
//		Mat img1 = Imgcodecs.imread("C:\\Users\\zhangpan\\Desktop\\cizhuan\\ok\\1.jpg");
//		Mat img2 = Imgcodecs.imread("C:\\Users\\zhangpan\\Desktop\\cizhuan\\xiaci\\1.jpg");
		Mat img1 = Imgcodecs.imread("C:\\Users\\zhangpan\\Desktop\\pic\\12.jpg");
		Mat img2 = Imgcodecs.imread("C:\\Users\\zhangpan\\Desktop\\pic\\2.jpg");
 
		Mat img = new Mat();
		//像素做差
        Core.absdiff(img1, img2, img);
        Imgcodecs.imwrite("C:\\Users\\zhangpan\\Desktop\\new_diff7.jpg", img);
        
        
        Mat erodeImg = new Mat();
        
        Mat kernel = Imgproc.getStructuringElement(1,new Size(4,6));
        //腐蚀
        Imgproc.erode(img, erodeImg, kernel,new Point(-1,-1),1);
        Imgcodecs.imwrite("C:\\Users\\zhangpan\\Desktop\\new_diff7.jpg", erodeImg);
        
        
        Mat dilateImg = new Mat();
        Mat kernel1 = Imgproc.getStructuringElement(1,new Size(2,3));
        //膨胀
        Imgproc.dilate(erodeImg, dilateImg, kernel1);
        Imgcodecs.imwrite("C:\\Users\\zhangpan\\Desktop\\new_diff7.jpg", dilateImg);
        
        
        Mat threshImg = new Mat();
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		
		Mat hierarchy = new Mat();
        //检测边缘
        Imgproc.threshold(dilateImg, threshImg, 20, 255, Imgproc.THRESH_BINARY);
        //转化成灰度
        Imgproc.cvtColor(threshImg, threshImg, Imgproc.COLOR_RGB2GRAY);
        //找到轮廓(3：CV_RETR_TREE，2：CV_CHAIN_APPROX_SIMPLE)
        Imgproc.findContours(threshImg, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0,0));
        
 
		List<Rect> boundRect = new ArrayList<Rect>(contours.size());
        for(int i=0;i<contours.size();i++){
//        	Mat conMat = (Mat)contours.get(i);
//        	Imgproc.approxPolyDP((MatOfPoint2f)conMat,contours_poly.get(i),3,true);
        	//根据轮廓生成外包络矩形
        	Rect rect = Imgproc.boundingRect(contours.get(i));
        	boundRect.add(rect);
        }
        
        for(int i=0;i<contours.size();i++){
        	Scalar color = new Scalar(0,0,255);
        	//绘制轮廓
        	Imgproc.drawContours(img1, contours, i, color, 1, Core.LINE_8, hierarchy, 0, new Point());
        	//绘制矩形
//        	Core.rectangle(img1, boundRect.get(i).tl(), boundRect.get(i).br(), color, 2, Core.LINE_8, 0);
        }
        
        Imgcodecs.imwrite("C:\\Users\\zhangpan\\Desktop\\new_diff7.jpg", img1);
        
        System.out.println("===end==");
	}


}
