package xyz.pany.ai.test;
/**
 * @Title: FaceDetect</p>
 * @Description: TODO 
 * @author zhangpan 
 * @date 2018年8月15日 
 */

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.pany.common.utils.RestUtil;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.baidu.ai.aip.utils.AuthService;
import com.baidu.ai.aip.utils.Base64Util;
import com.baidu.ai.aip.utils.FileUtil;
import com.baidu.ai.aip.utils.GsonUtils;
import com.baidu.ai.aip.utils.HttpUtil;

/**
* 人脸探测
* @author liyingming
* @data 2017-11-15
*/
public class FaceDetect {

    public static String detect() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/detect";
        try {
        	String filePath = "F:\\c.jpg";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
//            String imgParam = URLEncoder.encode(imgStr, "UTF-8");
        	
            Map<String, Object> map = new HashMap<>();
            map.put("image", imgStr);
            map.put("face_field", "age,beauty,expression,face_shape,gender,glasses,landmark,race,quality,face_type");
            map.put("image_type", "BASE64");

            String param = GsonUtils.toJson(map);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            JSONObject jsonobject = JSONObject.fromObject(result);
            JSONObject strObject = jsonobject.getJSONObject("result");
	     	String str = strObject.getString("face_list");
	     	
	     	JSONArray json = JSONArray.fromObject(str); // 首先把字符串转成 JSONArray  对象
	     	 for(int i=0;i<json.size();i++){
			        JSONObject job = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
			        
			    	//获取年龄
			        Double ageOne = Double.valueOf(job.getString("age"));
			     	//处理年龄
			        String age =String.valueOf(new BigDecimal(ageOne).setScale(0, BigDecimal.ROUND_HALF_UP));
					map.put("age", age);
					
					//获取美丑打分
			        Double beautyOne = (Double) job.get("beauty");
					//处理美丑打分
			     	String beauty =String.valueOf(new BigDecimal(beautyOne).setScale(0, BigDecimal.ROUND_HALF_UP));
					map.put("beauty", beauty);
					
					//获取性别  male(男)、female(女)
					JSONObject genderObject = job.getJSONObject("gender");
					String gender = genderObject.getString("type");
					
					map.put("gender", gender);
					
					//获取是否带眼睛 0-无眼镜，1-普通眼镜，2-墨镜
					JSONObject glassesObject = job.getJSONObject("glasses");
					String glasses = glassesObject.getString("type");
					map.put("glasses", glasses);
					
					//获取是否微笑，0，不笑；1，微笑；2，大笑
					JSONObject  expressionObject = job.getJSONObject("expression");
					String expression = expressionObject.getString("type");
					map.put("expression", expression);
			 }
	     	
            System.out.println(map);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
    
    public static String match() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/match";
        try {

            byte[] bytes1 = FileUtil.readFileByBytes("C:\\Users\\zhangpan\\Desktop\\cizhuan\\ok\\1.jpg");
            byte[] bytes2 = FileUtil.readFileByBytes("C:\\Users\\zhangpan\\Desktop\\cizhuan\\ok\\01.jpg");
            String image1 = Base64Util.encode(bytes1);
            String image2 = Base64Util.encode(bytes2);

            List<Map<String, Object>> images = new ArrayList<>();

            Map<String, Object> map1 = new HashMap<>();
            map1.put("image", image1);
            map1.put("image_type", "BASE64");
            map1.put("face_type", "LIVE");
            map1.put("quality_control", "LOW");
            map1.put("liveness_control", "NORMAL");

            Map<String, Object> map2 = new HashMap<>();
            map2.put("image", image2);
            map2.put("image_type", "BASE64");
            map2.put("face_type", "LIVE");
            map2.put("quality_control", "LOW");
            map2.put("liveness_control", "NORMAL");

            images.add(map1);
            images.add(map2);

            String param = GsonUtils.toJson(images);

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
            String accessToken = AuthService.getAuth();

            String result = HttpUtil.post(url, accessToken, "application/json", param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static void main(String[] args) {
//        FaceDetect.detect();
        FaceDetect.match();
    }
}
