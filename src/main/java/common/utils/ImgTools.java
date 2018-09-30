package common.utils;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Title: ImgTools</p>
 * @Description: TODO
 * @author zhangpan
 * @date 2018年9月11日
 */
public class ImgTools {

    private static Logger log = LoggerFactory.getLogger(ImgTools.class);
    
    private static String DEFAULT_THUMB_PREVFIX = "thumb_";
    private static String DEFAULT_CUT_PREVFIX = "cut_";
    private static Boolean DEFAULT_FORCE = false;

//	public static void main(String[] args) {
//		ImgTools.byte2File(ImgTools.compressUnderSize(File2byte("F:\\c.jpg"), 90000), "F:/ceshi", "ceshi.jpg");
//	    new ImgTools().thumbnailImage("F:\\c.jpg", 150, 100);
//	    new ImgTools().cutImage("F:\\c.jpg","F:\\aaa", 250, 250, 600, 800);
//	}
    /**
     * <p>Title: cutImage</p>
     * <p>Description:  根据原图与裁切size截取局部图片</p>
     * @param srcImg    源图片
     * @param output    图片输出流
     * @param rect      需要截取部分的坐标和大小
     */
    public static void cutImage(File srcImg, OutputStream output, java.awt.Rectangle rect){
        if(srcImg.exists()){
            java.io.FileInputStream fis = null;
            ImageInputStream iis = null;
            try {
                fis = new FileInputStream(srcImg);
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
                String suffix = null;
                // 获取图片后缀
                if(srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if(suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()+",") < 0){
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return ;
                }
                // 将FileInputStream 转换为ImageInputStream
                iis = ImageIO.createImageInputStream(fis);
                // 根据图片类型获取该种类型的ImageReader
                ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
                reader.setInput(iis,true);
                ImageReadParam param = reader.getDefaultReadParam();
                param.setSourceRegion(rect);
                BufferedImage bi = reader.read(0, param);
                ImageIO.write(bi, suffix, output);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if(fis != null) fis.close();
                    if(iis != null) iis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }else {
            log.warn("the src image is not exist.");
        }
    }
    
    public void cutImage(File srcImg, OutputStream output, int x, int y, int width, int height){
        cutImage(srcImg, output, new java.awt.Rectangle(x, y, width, height));
    }
    
    /**  
     * @Title: cutImage  
     * @Description: TODO 
     * @param srcImg		
     * @param destImgPath 	图片保存路径
     * @param rect 
     */
    public static String cutImage(File srcImg, String destImgPath, Rectangle rect){
        File destImg = new File(destImgPath);
        if(!destImg.exists()){
        	log.warn("the dest image folder create now.");
        	destImg.mkdirs();
        }

        String p = destImg.getPath();
        String cutPicSrc = null;
        try {
            if(!destImg.isDirectory()) p = destImg.getParent();
            if(!p.endsWith(File.separator)) p = p + File.separator;
            cutPicSrc = p + DEFAULT_CUT_PREVFIX + "_" + new java.util.Date().getTime() + "_" + srcImg.getName();
            cutImage(srcImg, new java.io.FileOutputStream(cutPicSrc), rect);
        } catch (FileNotFoundException e) {
            log.warn("the dest image is not exist.");
        }
        return cutPicSrc;
    }
    
    /**  
     * @Title: cutImage  
     * @Description: TODO 
     * @param srcImg	源图片
     * @param destImg	剪裁图片保存路径
     * @param x
     * @param y
     * @param width
     * @param height 
     */
    public static void cutImage(File srcImg, String destPath, int x, int y, int width, int height){
        cutImage(srcImg, destPath, new java.awt.Rectangle(x, y, width, height));
    }
    
    /**  
     * @Title: cutImage  
     * @Description: TODO 
     * @param srcImg 	源图片路径
     * @param destPath	剪裁图片保存路径
     * @param x 
     * @param y
     * @param width
     * @param height 
     */
    public static void cutImage(String srcImg, String destPath, int x, int y, int width, int height){
        cutImage(new File(srcImg), destPath, new java.awt.Rectangle(x, y, width, height));
    }
    
    /**  
     * @Title: cutImage  
     * @Description: TODO 
     * @param srcImg 	源图片路径
     * @param destPath	剪裁图片保存路径
     * @param x 
     * @param y
     * @param width
     * @param height 
     */
    public static void cutImage(String srcImg, String destPath, Rectangle rect){
    	cutImage(new File(srcImg), destPath, rect);
    }
    
    /**
     * <p>Title: thumbnailImage</p>
     * <p>Description: 根据图片路径生成缩略图 </p>
     * @param imagePath    原图片路径
     * @param w            缩略图宽
     * @param h            缩略图高
     * @param prevfix    生成缩略图的前缀
     * @param force        是否强制按照宽高生成缩略图(如果为false，则生成最佳比例缩略图)
     */
    public void thumbnailImage(File srcImg, OutputStream output, int w, int h, String prevfix, boolean force){
        if(srcImg.exists()){
            try {
                // ImageIO 支持的图片类型 : [BMP, bmp, jpg, JPG, wbmp, jpeg, png, PNG, JPEG, WBMP, GIF, gif]
                String types = Arrays.toString(ImageIO.getReaderFormatNames()).replace("]", ",");
                String suffix = null;
                // 获取图片后缀
                if(srcImg.getName().indexOf(".") > -1) {
                    suffix = srcImg.getName().substring(srcImg.getName().lastIndexOf(".") + 1);
                }// 类型和图片后缀全部小写，然后判断后缀是否合法
                if(suffix == null || types.toLowerCase().indexOf(suffix.toLowerCase()+",") < 0){
                    log.error("Sorry, the image suffix is illegal. the standard image suffix is {}." + types);
                    return ;
                }
                log.debug("target image's size, width:{}, height:{}.",w,h);
                Image img = ImageIO.read(srcImg);
                // 根据原图与要求的缩略图比例，找到最合适的缩略图比例
                if(!force){
                    int width = img.getWidth(null);
                    int height = img.getHeight(null);
                    if((width*1.0)/w < (height*1.0)/h){
                        if(width > w){
                            h = Integer.parseInt(new java.text.DecimalFormat("0").format(height * w/(width*1.0)));
                            log.debug("change image's height, width:{}, height:{}.",w,h);
                        }
                    } else {
                        if(height > h){
                            w = Integer.parseInt(new java.text.DecimalFormat("0").format(width * h/(height*1.0)));
                            log.debug("change image's width, width:{}, height:{}.",w,h);
                        }
                    }
                }
                BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
                Graphics g = bi.getGraphics();
                g.drawImage(img, 0, 0, w, h, Color.LIGHT_GRAY, null);
                g.dispose();
                // 将图片保存在原目录并加上前缀
                ImageIO.write(bi, suffix, output);
                output.close();
            } catch (IOException e) {
               log.error("generate thumbnail image failed.",e);
            }
        }else{
            log.warn("the src image is not exist.");
        }
    }
    public void thumbnailImage(File srcImg, int w, int h, String prevfix, boolean force){
        String p = srcImg.getAbsolutePath();
        try {
            if(!srcImg.isDirectory()) p = srcImg.getParent();
            if(!p.endsWith(File.separator)) p = p + File.separator;
            thumbnailImage(srcImg, new java.io.FileOutputStream(p + prevfix +srcImg.getName()), w, h, prevfix, force);
        } catch (FileNotFoundException e) {
            log.error("the dest image is not exist.",e);
        }
    }
    
    public void thumbnailImage(String imagePath, int w, int h, String prevfix, boolean force){
        File srcImg = new File(imagePath);
        thumbnailImage(srcImg, w, h, prevfix, force);
    }
    
    public void thumbnailImage(String imagePath, int w, int h, boolean force){
        thumbnailImage(imagePath, w, h, DEFAULT_THUMB_PREVFIX, DEFAULT_FORCE);
    }
    
    public void thumbnailImage(String imagePath, int w, int h){
        thumbnailImage(imagePath, w, h, DEFAULT_FORCE);
    }

	/**
	 * 将图片压缩到指定大小以内
	 * 
	 * @param srcImgData
	 *            源图片数据
	 * @param maxSize
	 *            目的图片大小
	 * @return 压缩后的图片数据
	 */
	public static byte[] compressUnderSize(byte[] srcImgData, long maxSize) {
		double scale = 0.9;
		byte[] imgData = Arrays.copyOf(srcImgData, srcImgData.length);

		if (imgData.length > maxSize) {
			do {
				try {
					imgData = compress(imgData, scale);
				}
				catch (IOException e) {
					throw new IllegalStateException("压缩图片过程中出错，请及时联系管理员！", e);
				}
			}
			while (imgData.length > maxSize);
		}
		return imgData;
	}

	/**
	 * 按照 宽高 比例压缩
	 * 
	 * @param imgIs
	 *            待压缩图片输入流
	 * @param scale
	 *            压缩刻度
	 * @param out
	 *            输出
	 * @return 压缩后图片数据
	 * @throws IOException
	 *             压缩图片过程中出错
	 */
	public static byte[] compress(byte[] srcImgData, double scale) throws IOException {
		BufferedImage bi = ImageIO.read(new ByteArrayInputStream(srcImgData));
		int width = (int) (bi.getWidth() * scale); // 源图宽度
		int height = (int) (bi.getHeight() * scale); // 源图高度

		Image image = bi.getScaledInstance(width, height, Image.SCALE_SMOOTH);
		BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics g = tag.getGraphics();
		g.setColor(Color.RED);
		g.drawImage(image, 0, 0, null); // 绘制处理后的图
		g.dispose();

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		ImageIO.write(tag, "JPEG", bOut);

		return bOut.toByteArray();
	}

	/**  
	 * @Title: File2byte  
	 * @Description: 文件转byte字符数组
	 * @param filePath 文件路径
	 * @return 文件byte字符数组
	 */
	public static byte[] File2byte(String filePath) {
		byte[] buffer = null;
		try {
			File file = new File(filePath);
			FileInputStream fis = new FileInputStream(file);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			byte[] b = new byte[1024];
			int n;
			while ((n = fis.read(b)) != -1) {
				bos.write(b, 0, n);
			}
			closeCloseableObject(fis, bos);//关闭
			buffer = bos.toByteArray();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return buffer;
	}

	/**  
	 * @Title: byte2File  
	 * @Description: byte数组转文件
	 * @param buf 文件byte字符数组
	 * @param filePath  文件路径
	 * @param fileName  文件名称
	 */
	public static void byte2File(byte[] buf, String filePath, String fileName) {
		BufferedOutputStream bos = null;
		FileOutputStream fos = null;
		File file = null;
		try {
			File dir = new File(filePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}
			file = new File(filePath + File.separator + fileName);
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(buf);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			closeCloseableObject(bos, fos);//关闭流
		}
	}

	/**  
	 * @Title: closeCloseableObject  
	 * @Description: 关闭已经打开的流
	 * @param objs 
	 */
	private static void closeCloseableObject(Closeable... objs) {
		if (objs != null) {
			for (Closeable fileOutputStream : objs) {
				try {
					fileOutputStream.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
