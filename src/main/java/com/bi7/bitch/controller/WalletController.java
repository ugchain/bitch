package com.bi7.bitch.controller;

import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.service.WalletService;
import com.bi7.bitch.util.SignUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by foxer on 2017/8/22.
 */
@RestController
@RequestMapping("/wallet")
public class WalletController {

    private final static Log log = LogFactory.getLog(WalletController.class);
    @Autowired
    private WalletService service;

    @Autowired
    private SignUtil signUtil;

    @RequestMapping("/applyaddress")
    public String applyAddress(@RequestParam("userid") int userId, @RequestParam("coinname") String coinname, @RequestParam("sign") String sign) {

        if (sign == null || "".equals(sign)) {
            log.warn("sign null or empty");
            return Msg.PARAM_ERROR.toString();
        }
        if (userId <= 0) {
            log.warn("userid <= 0 ");
            return Msg.PARAM_ERROR.toString();
        }

        CoinName cn = CoinName.get(coinname);
        if (cn == null) {
            log.warn("coinname not exist");
            return Msg.PARAM_ERROR.toString();
        }

        boolean checkResult = signUtil.checkSign(sign, new HashMap<String, Object>() {
            {
                put("userid", userId);
                put("coinname", coinname);
            }
        });
        if (!checkResult) {
            log.warn(String.format("signCheck error,userid: %d, coinname: %s, sign: %s", userId, coinname, sign));
            return Msg.PARAM_ERROR.toString();
        }
        return service.applyAddress(userId, cn).toString();
    }
}
