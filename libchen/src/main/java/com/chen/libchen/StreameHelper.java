package com.chen.libchen;

import java.io.InputStream;

/**
 * Created by hui on 2016/10/6.
 */

public class StreameHelper {
    public String inputStream2String(InputStream is){
        String res;
        try {
            byte[] buf = new byte[is.available()];
            is.read(buf);
            res = new String(buf);      //  必须将GBK码制转成Unicode
            is.close();
        } catch (Exception e) {
            res = "";
        }
        return (res);
    }
}
