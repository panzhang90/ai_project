package xyz.pany.ai.test;
/**
 * @Title: BodySegSample</p>
 * @Description: TODO 
 * @author zhangpan 
 * @date 2018年9月28日 
 */
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import sun.misc.BASE64Decoder;

import com.alibaba.fastjson.JSONObject;
import com.baidu.ai.aip.utils.Base64Util;
import com.baidu.ai.aip.utils.FileUtil;
import com.baidu.ai.aip.utils.HttpUtil;
//Base64Util  FileUtil  HttpUtil百度提供 自行下载即可 小帅整理的下载地址 
//http://aixiaoshuai.mydoc.io/?t=234826
/**
 * 人像分割Java-API示例代码
 * @author 小帅丶
 *
 */
public class BodySegSample {
	//人像分割接口地址
	public static String BODYSEG_URL = "https://aip.baidubce.com/rest/2.0/image-classify/v1/body_seg";
 
	public static void main(String[] args) throws Exception {
		String accessToken = "自己应用获取的AccessToken";
		//返回字符串
		String filePath = "G:/bodyseg.jpg";
		String result = getBodySegResult(filePath, accessToken);
		JSONObject jsonObject = JSONObject.parseObject(result);
		//图片转BufferedImage对象
		BufferedImage image = ImageIO.read(new File(filePath));
		//对接口返回的labelmapbase64进行处理  并拿图片的原始宽高
		convert(jsonObject.getString("labelmap"),image.getWidth(),image.getHeight());
	}
	/**
	 * @param imagePath 图片本地路径
	 * @param accessToken 应用AccessToken
	 * @return String
	 * @throws Exception
	 */
	private static String getBodySegResult(String imagePath, String accessToken)
			throws Exception {
		byte[] imgData = FileUtil.readFileByBytes(imagePath);
		String imgStr = Base64Util.encode(imgData);
		String param = "image=" + URLEncoder.encode(imgStr, "UTF-8");
		// 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间，
		// 客户端可自行缓存，过期后重新获取。
		String result = HttpUtil.post(BODYSEG_URL, accessToken, param);
		return result;
	}
	/**
	 * 图像转换
	 * @param labelmapBase64 分割结果图片，检测出的二值图像，base64编码之后
	 * @param realWidth 图片原始高度px
	 * @param realHeight 图片原始宽度px
	 */
	public static void convert(String labelmapBase64, int realWidth, int realHeight) {
	    try {
	    	BASE64Decoder base64Decoder = new BASE64Decoder();
	        byte[] bytes = base64Decoder.decodeBuffer(labelmapBase64);
	        InputStream is = new ByteArrayInputStream(bytes);
	        BufferedImage image = ImageIO.read(is);
	        BufferedImage newImage = resize(image, realWidth, realHeight);
	        BufferedImage grayImage = new BufferedImage(realWidth, realHeight, BufferedImage.TYPE_BYTE_GRAY);//灰度图
	        for(int i= 0 ; i < realWidth ; i++){
	            for(int j = 0 ; j < realHeight; j++){
	                int rgb = newImage.getRGB(i, j);
	                grayImage.setRGB(i, j, rgb * 255);  //将像素存入缓冲区 这一步很重要哦
	            }
	        }
	        File newFile = new File("G:/gray001.jpg");
	        ImageIO.write(grayImage, "jpg", newFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	/**
	 * 重置图片大小
	 * @param img 图片数据
	 * @param newW 图片宽度
	 * @param newH 图片高度
	 * @return
	 */
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
	    Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
	    BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
	    Graphics2D g2d = dimg.createGraphics();
	    g2d.drawImage(tmp, 0, 0, null);
	    g2d.dispose();
	    return dimg;
	}
}
