package com.bi7.bitch.dao;

import com.bi7.bitch.dao.model.BitchDistribute;
import com.bi7.bitch.dao.model.BitchWalletAddress;
import com.bi7.bitch.dao.model.DistributeWalletTypeEnum;
import com.bi7.bitch.mapper.primary.WalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Hash;

import java.util.Date;

/**
 * Created by fanjl on 2017/10/11.
 */
@Service
public class WalletDao {

    @Autowired
    WalletMapper walletMapper;

    @Transactional(value = "bitchDataTransactionManager")
    public synchronized BitchWalletAddress applyAddress(int userId, int status,String coinName) {
        boolean result = false;
        try {
            result = walletMapper.findExistsByPriKey(userId,coinName);
        } catch (Exception e) {
            throw new RuntimeException("find by prikey error");
        }
        if(result){
            throw new RuntimeException("userId:"+userId+"; coinName:"+coinName+" has already exists");
        }
        BitchDistribute bitchDistribute = null;
        try {
            bitchDistribute = walletMapper.findWaitForDistributeAddress(status,coinName);
        } catch (Exception e) {
            throw new RuntimeException("find wait for distribute address error");
        }
        if (bitchDistribute.getStatus() != DistributeWalletTypeEnum.NOT_USED.getId()) {
            throw new RuntimeException("other application is apply this");
        }
        try {
            walletMapper.updateAddressStatus(bitchDistribute.getId(),DistributeWalletTypeEnum.USED.getId());
        } catch (Exception e) {
            throw new RuntimeException("update distribute address status error");
        }
        BitchWalletAddress bitchWalletAddress = new BitchWalletAddress();
        try {
            bitchWalletAddress.setAddress(bitchDistribute.getAddress());
            bitchWalletAddress.setAddressCheck(Hash.sha3(bitchDistribute.getAddress()));
            bitchWalletAddress.setAddtime(new Date());
            bitchWalletAddress.setCoinname(coinName);
            bitchWalletAddress.setUserid(userId);
            walletMapper.insertNewBitchWallet(bitchWalletAddress);
        } catch (Exception e) {
            throw new RuntimeException("insert user wallet error");
        }
        return bitchWalletAddress;
    }
}
