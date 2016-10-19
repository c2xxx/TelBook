package com.chen.telbook.helper;

import android.util.Xml;

import com.chen.telbook.bean.TelNum;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

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
}
