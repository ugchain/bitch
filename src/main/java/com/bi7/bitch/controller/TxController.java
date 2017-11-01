package com.bi7.bitch.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by foxer on 2017/10/31.
 */
@RestController
@RequestMapping("/tx")
public class TxController {
    private final static Log log = LogFactory.getLog(TxController.class);

    /**
     * 1、扫描充值表，汇总币，生成tx，并且 储存到 bitch_tx 中
     * 2、按照任意时间段，获取所有 bitch_tx 的记录，生成手续费汇总记录，java程序里进行加和
     * 3、对外提供对账服务，连带所有的 fee，以及充值进行求和,,,,,,
     */


    @RequestMapping("/applyaddress")
    public String
}
