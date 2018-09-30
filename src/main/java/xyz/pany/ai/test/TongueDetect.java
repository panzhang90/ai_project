package xyz.pany.ai.test;
/**
 * @Title: FaceDetect</p>
 * @Description: TODO 
 * @author zhangpan 
 * @date 2018年8月15日 
 */

import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.baidu.ai.aip.utils.AuthService;
import com.baidu.ai.aip.utils.Base64Util;
import com.baidu.ai.aip.utils.FileUtil;
import com.baidu.ai.aip.utils.GsonUtils;
import com.baidu.ai.aip.utils.HttpUtil;

import common.utils.ImgTools;

/**
* 舌头识别
* @author liyingming
* @data 2017-11-15
*/
public class TongueDetect {

    public static String detect() {
        // 请求url
        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/detection/hastongue";
        try {
			File file = new File("C:\\Users\\zhangpan\\Desktop\\舌诊\\ww");
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				File filei = files[i];
				String filePath = filei.getAbsolutePath(); // 根据后缀判断
				
				String result = getImgAIResult(url, filePath);
	            getTongueResultHandler(filei, result);
			}
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

	private static String getImgAIResult(String url, String filePath) {
		String result;
		try {
			byte[] imgData = FileUtil.readFileByBytes(filePath);
			String imgStr = Base64Util.encode(imgData);
			// String imgParam = URLEncoder.encode(imgStr, "UTF-8");
			
			Map<String, Object> map = new HashMap<>();
			map.put("image", imgStr);
			map.put("threshold", 0.5);

			String param = GsonUtils.toJson(map);
			
			// 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
			String accessToken = AuthService.getAuth("IEPA1IeW74ZLrSZWXuf48WeV", "GA3UqZmcMXDOu3uUwLYGBEMKMdWqcdDr");

			result = HttpUtil.post(url, accessToken, "application/json", param);
			System.out.println(result);
			return result;
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private static void getTongueResultHandler(File file, String result) {
		JSONObject jsonobject = JSONObject.fromObject(result);
		String str = jsonobject.getString("results");
		
		JSONArray json = JSONArray.fromObject(str); 
		 for(int i=0;i<json.size();i++){
		        JSONObject tongue = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
		        JSONObject location = tongue.getJSONObject("location");  
		        
		    	//获取舌头起点上边距
		        int top = Integer.valueOf(location.getString("top"));
		        //获取舌头起点左边距
		        int left = Integer.valueOf(location.getString("left"));
		        //获取舌头宽度
		        int width = Integer.valueOf(location.getString("width"));
		        //获取舌头宽度
		        int height = Integer.valueOf(location.getString("height"));
		        
		        Rectangle rect = new Rectangle(left, top, width, height); 
		        String filePath = ImgTools.cutImage(file, "C:\\Users\\zhangpan\\Desktop\\舌诊\\ww", rect);
		        
		        //进一步分析舌部疾病特征
		        String url = "https://aip.baidubce.com/rpc/2.0/ai_custom/v1/classification/tongue_type";
		        getImgAIResult(url, filePath);
		        //可信度
//				        Double score = Double.valueOf(tongue.getString("score"));
				
//				        System.out.println(map);
		 }
	}
    
    public static void main(String[] args) {
        TongueDetect.detect();
    }
}
