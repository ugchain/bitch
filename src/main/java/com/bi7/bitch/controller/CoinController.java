package com.bi7.bitch.controller;

import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.service.CoinService;
import com.bi7.bitch.util.SignUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

/**
 * Created by foxer on 2017/8/22.
 */
@RestController
@RequestMapping("/coin")
public class CoinController {

    private final static Log log = LogFactory.getLog(CoinController.class);

    @Autowired
    private SignUtil signUtil;

    @Autowired
    private CoinService coinService;

    @RequestMapping("/withdraw")
    public String withdraw(@RequestParam("zcid") int zcId, @RequestParam("userid") int userId, @RequestParam("address") String address,
                           @RequestParam("coinname") String coinname, @RequestParam("value") String value, @RequestParam("fee") String fee,
                           @RequestParam("sign") String sign) {
        /*
        step0: 检查 movesay_myzc.status  必须 等于  0
         */

        /*
        step1: 生成交易，并发送
         */

        /*
        step2: 根据 zcId ,修改 movesay_myzc 表的  txId 与 status = 1
         */

        /*
        step3: 本地数据库表 bitch_withdraw 插入提现记录;  bitch_withdraw 表结构在 README 文件中有;
         */
        if (sign == null || "".equals(sign)) {
            log.warn("sign null or empty");
            return Msg.PARAM_ERROR.toString();
        }

        if (zcId <= 0 || userId <= 0) {
            log.warn("param error");
            return Msg.PARAM_ERROR.toString();
        }

        CoinName cn = CoinName.get(coinname);
        if (cn == null) {
            log.warn("coinname not exist");
            return Msg.PARAM_ERROR.toString();
        }

        boolean checkResult = signUtil.checkSign(sign, new HashMap<String, Object>() {
            {
                put("zcid", zcId);
                put("userid", userId);
                put("address", address);
                put("coinname", coinname);
                put("value", value);
                put("fee", fee);
            }
        });
        if (!checkResult) {
            log.warn(String.format("signCheck error,zcid: %d,userid: %d,address: %s,coinname: %s, value: %s, fee: %s, sign: %s",
                    zcId, userId, address, coinname, value, fee, sign));
            return Msg.PARAM_ERROR.toString();
        }
        return coinService.withdraw(zcId, userId, address, cn, value, fee).toString();
    }

}
