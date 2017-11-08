package com.bi7.bitch.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bi7.bitch.dao.model.BitchTx;
import com.bi7.bitch.dao.model.BitchWallet;
import com.bi7.bitch.mapper.primary.TxMapper;
import com.bi7.bitch.mapper.primary.WalletMapper;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TxDao {

    @Autowired
    private WalletMapper walletMapper;

    @Autowired
    private TxMapper txMapper;

    /**
     * 根据coinname查询table中的所有记录
     *
     * @param coinname
     * @return
     * @throws Exception
     */
    public List<BitchWallet> selectBitchWallet(String coinname) throws Exception {
        List<BitchWallet> scanBitchWalletTable = walletMapper.scanBitchWalletTable(coinname);
        if (scanBitchWalletTable == null) {
            return new ArrayList<BitchWallet>(0);
        }
        return scanBitchWalletTable;
    }


    public void saveTxToBitchTx(BitchTx bitchTx) throws Exception {
        txMapper.insert(bitchTx);
    }


    public List<BitchTx> txInfoByFromAddress(String address) throws Exception {
        List<BitchTx> txInfoList = txMapper.txInfoByFromAddress(address);
        if (txInfoList == null) {
            return new ArrayList<BitchTx>(0);
        }
        return txInfoList;
    }


    public void upTxStatus(BitchTx bitchTx) throws Exception {
        txMapper.update(bitchTx.getTxid(), bitchTx.getBlockNumber(), bitchTx.getFee(), bitchTx.getGasPrice(), bitchTx.getGasUsed());
    }

    /**
     * 查看未打包的交易
     *
     * @return
     * @throws Exception
     */
    public List<BitchTx> selectUnPackageTx() throws Exception {

        List<BitchTx> txInfoList = txMapper.findUnPackageTx();
        if (txInfoList == null) {
            return new ArrayList<BitchTx>(0);
        }
        return txInfoList;

    }

    /**
     * 查询所有的bitchTx统计fee
     *
     * @return
     */
    public List<BitchTx> selectBitchTx() throws Exception {
        List<BitchTx> txInfoList = txMapper.selectBitchTx();
        if (txInfoList == null) {
            return new ArrayList<BitchTx>(0);
        }
        return txInfoList;
    }


    /**
     * 在bitchWallet表查询所有记录
     *
     * @return
     */
    public List<BitchWallet> selectAllBitchWallet() {
        List<BitchWallet> bitchWalletList = walletMapper.selectAllBitchWallet();
        if (bitchWalletList == null) {
            return new ArrayList<BitchWallet>(0);
        }
        return bitchWalletList;
    }
}
