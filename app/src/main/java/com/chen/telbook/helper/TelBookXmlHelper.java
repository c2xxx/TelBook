package com.chen.telbook.helper;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Xml;

import com.chen.libchen.Logger;
import com.chen.telbook.bean.TelNum;
import com.chen.telbook.constants.Constants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by hui on 2016/10/14.
 */

public class TelBookXmlHelper {
    public static String writeToString(List<TelNum> list) throws Exception {
        StringWriter writer = new StringWriter();
        // 获取XmlSerializer对象
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlSerializer serializer = factory.newSerializer();
        // 设置输出流对象
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);

        // 第一个参数为命名空间,如果不使用命名空间,可以设置为null
        serializer.startTag(null, "root");
        serializer.startTag(null, "tels");
        for (TelNum telNum : list) {
            serializer.startTag(null, "tel");

            serializer.startTag(null, "name");
            serializer.text(telNum.getName());
            serializer.endTag(null, "name");

            serializer.startTag(null, "phonenumber");
            serializer.text(telNum.getTel());
            serializer.endTag(null, "phonenumber");

            serializer.startTag(null, "photo");
            serializer.text(telNum.getImg());
            serializer.endTag(null, "photo");

            serializer.endTag(null, "tel");
        }

        serializer.endTag(null, "tels");
        serializer.endTag(null, "root");
        serializer.endDocument();

        return writer.toString();
    }

    /**
     * 解析XML
     *
     * @param xmlString
     * @return
     * @throws Exception
     */
    public static List<TelNum> parse(String xmlString) throws Exception {
        List<TelNum> list = null;
        TelNum telNum = null;
        XmlPullParser parser = Xml.newPullParser();    //由android.util.Xml创建一个XmlPullParser实例
        ByteArrayInputStream tInputStringStream = new ByteArrayInputStream(xmlString.getBytes());
        parser.setInput(tInputStringStream, "UTF-8");                //设置输入流 并指明编码方式

        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:
//                    LogController.d("START_DOCUMENT");
                    list = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
//                    LogController.d("START_TAG");
                    if (parser.getName().equals("tel")) {
                        telNum = new TelNum();
                    } else if (parser.getName().equals("name")) {
                        parser.next();
                        telNum.setName(parser.getText());
                    } else if (parser.getName().equals("phonenumber")) {
                        parser.next();
                        String phonenumber = parser.getText();
                        if (phonenumber != null && phonenumber.length() == 11) {
                            phonenumber = phonenumber.substring(0, 3) + " " + phonenumber.substring(3, 7) + " " + phonenumber.substring(7, 11);
                        }

                        telNum.setTel(phonenumber);
                    } else if (parser.getName().equals("photo")) {
                        parser.next();
                        telNum.setImg(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
//                    LogController.d("END_TAG");
                    if (parser.getName().equals("tel")) {
                        list.add(telNum);
                        telNum = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return list;
    }

    /**
     * 加载本地数据
     */
    public static Map<String, TelNum> loadLocolData() {
        Map<String, TelNum> hashMap = new Hashtable<>();
        String strBase64 = SharedPerferencesHelper.read(SharedPerferencesHelper.getPhoneBookKey());
        if (!TextUtils.isEmpty(strBase64)) {
            String strResult = new String(Base64.decode(strBase64, Base64.DEFAULT));
            try {
                List<TelNum> list = TelBookXmlHelper.parse(strResult);
                for (TelNum telNum : list) {
                    hashMap.put(telNum.getTel().replace(" ", "").replace("-", ""), telNum);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (TextUtils.isEmpty(strBase64)) {
                if ("telbook".equals(Constants.USER_NAME)) {
                    TelNum telNum = new TelNum();
                    telNum.setImg("");
                    telNum.setName("陈辉");
                    telNum.setTel("15659002326");
                    hashMap.put(telNum.getTel().replace(" ", "").replace("-", ""), telNum);
                }
            }
        }
        Logger.d("hashMap.size=" + hashMap.size());
        return hashMap;
    }
}
