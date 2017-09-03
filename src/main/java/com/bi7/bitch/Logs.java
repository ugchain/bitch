package com.bi7.bitch;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Created by foxer on 2017/8/22.
 */
public final class Logs {

    public final static Log addressCreateLogger = LogFactory.getLog("bitch.createAddress");
    public final static Log scheduledLogger = LogFactory.getLog("bitch.scheduled");

    public static Log getLogger(Class claz) {
        return LogFactory.getLog(claz);
    }
}
