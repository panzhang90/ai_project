package xyz.pany.opencv;
/**
 * @Title: Pic</p>
 * @Description: TODO 
 * @author zhangpan 
 * @date 2018年8月1日 
 */
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber.Exception;
 
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
 
import static org.bytedeco.javacpp.opencv_core.cvReleaseImage;
 
/**
 * 作者：Haibo.Liu
 * 描述：
 * 日期： 2017/5/5
 * QQ：836915746
 */
public class Pic implements Runnable {
 
    public static void main(String[] args) throws FrameGrabber.Exception {
        Pic p = new Pic();
        p.startCamera();
    }
 
    private static Timer timer;
    private static int width, height;
    static BufferedImage bImage;
    public TimerAction timerAction;
 
 
    class TimerAction implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent arg0) {
            System.out.println("=====================================================================");
            try {
                ImageIO.write(bImage, "jpg", new File("d:/a.jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    public void startCamera() throws FrameGrabber.Exception {
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start(); // 开始获取摄像头数据
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();//转换器
        Frame frame = grabber.grab();
        opencv_core.IplImage grabbedImage = converter.convert(frame);//抓取一帧视频并将其转换为图像，至于用这个图像用来做什么？加水印，人脸识别等等自行添加
        width = grabbedImage.width();
        height = grabbedImage.height();
        bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        //显示器监控当前图像
        Graphics2D bGraphics = bImage.createGraphics();
        CanvasFrame canvas = new CanvasFrame("Camera", 1);
        canvas.setCanvasSize(width, height);
        
//        	File file1=new File("D:/a.jpg");             //用file1取得图片名字
//        	String name=file1.getName();
//
//            try {
//				ImageIO.write(bImage, "jpg", new File("D:/test/"+name));
//			}
//			catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
 
        timer = new Timer(1400, timerAction);// 设置每x秒保存一次照片。
        timer.start();
 
        while ((frame = grabber.grab()) != null) {
            //注释掉下行代码，及cavas.isVisibal，在Linux上可能不会出现警告
            canvas.showImage(frame);
            bGraphics.drawImage(iplToBufImgData(grabbedImage), null, 0, 0);
        }
        cvReleaseImage(grabbedImage);
        grabber.stop();
        canvas.dispose();
    }
 
 
    public static BufferedImage iplToBufImgData(opencv_core.IplImage mat) {
        if (mat.height() > 0 && mat.width() > 0) {
            BufferedImage image = new BufferedImage(mat.width(), mat.height(), BufferedImage.TYPE_3BYTE_BGR);
            WritableRaster raster = image.getRaster();
            DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
            byte[] data = dataBuffer.getData();
            mat.getByteBuffer().get(data);
            return image;
        }
        return null;
    }
 
 
    @Override
    public void run() {
        try {
            startCamera();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

