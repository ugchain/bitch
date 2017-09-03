package com.bi7.bitch.dao;

import com.bi7.bitch.dao.model.WithdrawStatusEnum;
import com.bi7.bitch.mapper.secondary.MyzcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by foxer on 2017/8/26.
 */
@Service
public class MyzcDao {

    @Autowired
    MyzcMapper myzcMapper;

    @Transactional(value = "bqiDataTransactionManager")
    public void updateStatus(int rid, int status) {
        myzcMapper.updateStatus(rid, status);
    }


    @Transactional(value = "bqiDataTransactionManager")
    public synchronized void saveWithdraw(int rid, String txid, int status) {
        if (myzcMapper.findOne(rid).getStatus() != WithdrawStatusEnum.AUDITING.getId()) {
            throw new RuntimeException("status error");
        }
        if (status != WithdrawStatusEnum.PENDING.getId()) {
            throw new RuntimeException("status error");
        }

        myzcMapper.updateTxidAndStatus(rid, txid, status);
    }

}
