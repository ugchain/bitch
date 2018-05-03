package com.bi7.bitch.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bi7.bitch.Logs;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.response.Msgs;
import com.bi7.bitch.service.InsideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by foxer on 2017/12/2.
 */
@RestController
@RequestMapping("/insidetx")
public class InsideTxController {

    @Autowired
    private InsideService insideService;

    @RequestMapping("/walletbalance")
    public String getWalletBalance() {
        return JSONObject.toJSONString(insideService.getWalletBalance());
    }

    @RequestMapping("/insidebatch")
    public String setInsideBatch(@RequestParam("address") String address) {
        List<String> addresses;
        try {
            addresses = JSON.parseArray(address, String.class);
        } catch (Exception e) {
            Logs.getLogger(InsideTxController.class).error("", e);
            return Msg.PARAM_ERROR.toString();
        }
        return JSONObject.toJSONString(insideService.setInsideBatch(addresses));
    }
}
