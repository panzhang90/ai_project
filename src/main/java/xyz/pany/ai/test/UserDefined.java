package xyz.pany.ai.test;

import java.net.URLEncoder;

import net.sf.json.JSONObject;
import xyz.pany.common.utils.RestUtil;

import com.baidu.ai.aip.utils.Base64Util;
import com.baidu.ai.aip.utils.FileUtil;
import com.baidu.ai.aip.utils.HttpUtil;

/**
* 图像审核接口
*/
public class UserDefined {

    /**
    * 重要提示代码中所需工具类
    * FileUtil,Base64Util,HttpUtil,GsonUtils请从
    * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
    * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
    * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
    * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
    * 下载
    */
    public static String userDefined() {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/solution/v1/img_censor/user_defined";
        try {
            // 本地文件路径
            String filePath = "C:\\Users\\zhangpan\\Desktop\\demo-pic1.jpg";
            byte[] imgData = FileUtil.readFileByBytes(filePath);
            String imgStr = Base64Util.encode(imgData);
            String imgParam = URLEncoder.encode(imgStr, "UTF-8");

            String param = "image=" + imgParam;

            // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
//            String accessToken = "24.3632dedaf5266006652606ed6c82c335.2592000.1535956528.282335-11632919";
            String responseString = RestUtil.load("https://aip.baidubce.com/oauth/2.0/token", "grant_type=client_credentials&client_id=EtyXAHaZGFt9jNVEOmia242W&client_secret=3Gfquy5kdwHvbBoU7mB5qLroDLj2rrpH");
            JSONObject jsonObject = JSONObject.fromObject(responseString);
            String accessToken = jsonObject.getString("access_token");
            String result = HttpUtil.post(url, accessToken, param);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        UserDefined.userDefined();
    }
}

