package com.bi7.bitch.dao;

import com.bi7.bitch.dao.model.ChargeStatusEnum;
import com.bi7.bitch.dao.model.MyzrModel;
import com.bi7.bitch.mapper.secondary.MyzrMapper;
import com.bi7.bitch.mapper.secondary.UserCoinMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by foxer on 2017/8/26.
 */
@Service
public class MyzrDao {
    @Autowired
    MyzrMapper myzrMapper;

    @Autowired
    UserCoinMapper userCoinMapper;

    @Transactional(value = "bqiDataTransactionManager")
    public int insert(MyzrModel zr) {
        if (zr.getStatus() != ChargeStatusEnum.PENDING.getId()) {
            throw new RuntimeException();
        }
        myzrMapper.insert(zr);
        return myzrMapper.findZrid(zr.getTxid());//get zrid
    }

    @Transactional(value = "bqiDataTransactionManager")
    public void updateStatus(int zrId, int status) {
        if (status != ChargeStatusEnum.FAILURE.getId()) {
            throw new RuntimeException();
        }
        myzrMapper.updateStatus(zrId, status);
    }

    @Transactional(value = "bqiDataTransactionManager")
    public void chargeSuccess(int userid, int zrId, int status, String coinname, String incVal) {
        if (status != ChargeStatusEnum.SUCCESS.getId()) {
            throw new RuntimeException();
        }
        myzrMapper.updateStatus(zrId, status);
        userCoinMapper.updateCoinBalance(userid, coinname, incVal);
    }

}
