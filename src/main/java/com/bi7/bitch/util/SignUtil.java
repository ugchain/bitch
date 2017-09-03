package com.bi7.bitch.util;

import com.bi7.bitch.conf.AppConfig;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by foxer on 2017/8/26.
 */
@Service
public class SignUtil {

    @Autowired
    private AppConfig config;

    private final static String PRIKEY = "prikey";

    public String buildSign(Map<String, Object> map) {
        List<NameValuePair> list = new ArrayList<>(map.size());

        List<String> keys = new ArrayList<>(map.keySet());
        keys.add(PRIKEY);
        Collections.sort(keys);

        keys.forEach(key -> {

            list.add(new NameValuePair() {
                @Override
                public String getName() {
                    return key;
                }

                @Override
                public String getValue() {
                    if (PRIKEY.equals(key)) {
                        return config.getPrikey();
                    }
                    return map.get(key).toString();
                }
            });
        });

        String prestr = URLEncodedUtils.format(list, "utf-8");
        return DigestUtils.md5Hex(prestr);
    }

    public boolean checkSign(String sign, Map<String, Object> map) {
        return config.isTesting() || buildSign(map).equals(sign);
    }
}
