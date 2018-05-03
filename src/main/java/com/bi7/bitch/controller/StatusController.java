package com.bi7.bitch.controller;

import com.bi7.bitch.conf.GethConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by foxer on 2017/11/1.
 */
@RestController
@RequestMapping("/status")
public class StatusController {

    @Autowired
    private GethConfig gethConfig;

    @RequestMapping("/eth-scan-blocknumber")
    public String ethBlockStatus() {
        return String.valueOf(gethConfig.getStartBlockNumber());
    }
}
