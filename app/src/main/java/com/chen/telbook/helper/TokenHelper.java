package com.chen.telbook.helper;

import android.util.Base64;

import com.chen.libchen.Logger;
import com.chen.telbook.utils.SHA1Util;

/**
 * Created by hui on 2016/10/14.
 */

public class TokenHelper {
    private static final String spaceName = "telbook";
    private static final String accressKey = "VZtEbyKjgZSANKSfObSqXMeaRocby1zf5wseyF_V";
    private static final String secretKey = "_TaNRS6TEOhrSWV_tq00s1JJ_HlkhOfhB9gRb70Z";

    public static String getToken(String key) {
        return getToken(spaceName, key, accressKey, secretKey);
    }


    /**
     * @param spaceName 空间名
     * @param key       存放到空间的文件名
     * @return
     */
    public static String getToken(String spaceName, String key, String accressKey, String secretKey) {
         /*
        第一步:确定上传策略
        第二步:将上传策略序列化成为json格式:
        第三步:对json序列化后的上传策略进行URL安全的Base64编码,得到如下encoded:
        第四步:用SecretKey对编码后的上传策略进行HMAC-SHA1加密，并且做URL安全的Base64编码,得到如下的encoded_signed:
        第五步:将 AccessKey、encode_signed 和 encoded 用 “:” 连接起来,得到如下的UploadToken:
        */


//        第一步:确定上传策略
        String deadline = (System.currentTimeMillis() / 1000 + 60 * 60 * 24 * 7) + "";

//        第二步:将上传策略序列化成为json格式:
        String json = String.format("{\"scope\":\"%s:%s\",\"deadline\":%s}", spaceName, key, deadline);
        Logger.d("step2=" + json);


//        第三步:对json序列化后的上传策略进行URL安全的Base64编码,得到如下encoded:
        String token_step3 = Base64.encodeToString(json.getBytes(), Base64.DEFAULT).trim();
        Logger.d("step3=" + token_step3);


//        第四步:用SecretKey对编码后的上传策略进行HMAC-SHA1加密，并且做URL安全的Base64编码,得到如下的encoded_signed:
        String token_step4 = null;
        try {
            byte[] sha1 = SHA1Util.HmacSHA1(token_step3, secretKey);
            token_step4 = Base64.encodeToString(sha1, Base64.DEFAULT).trim();

            //URL安全的字符串base64编码和解码(这里=号不用替换)
            token_step4 = token_step4.replace("+", "-").replace("/", "_");
        } catch (Exception e) {
            Logger.d("签名错误" + e.getMessage());
            return null;
        }
        Logger.d("step4=" + token_step4);

//        第五步:将 AccessKey、encode_signed 和 encoded 用 “:” 连接起来,得到如下的UploadToken:
        String token_step5 = String.format("%s:%s:%s", accressKey, token_step4, token_step3);
        Logger.d("step5=" + token_step5);
        return token_step5;
    }
}
