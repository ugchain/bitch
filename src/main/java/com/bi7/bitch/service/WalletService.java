package com.bi7.bitch.service;

import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.Logs;
import com.bi7.bitch.dao.model.BitchWallet;
import com.bi7.bitch.mapper.primary.WalletMapper;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.response.MsgSignable;
import com.bi7.bitch.response.Msgs;
import com.bi7.bitch.response.Status;
import com.bi7.bitch.util.SignUtil;
import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by foxer on 2017/8/22.
 */
@Service
public class WalletService extends MsgSignable {

    private final static Log log = Logs.getLogger(WalletService.class);

    @Autowired
    Web3jService web3jService;

    @Autowired
    WalletMapper walletMapper;

    /**
     * 生成address
     *
     * @param userId
     * @param coinname
     * @return
     */
    public Msg applyAddress(int userId, CoinName coinname) {

        BitchWallet bitchWallet = null;
        try {
            bitchWallet = walletMapper.findOne(userId, coinname.getRealCoinName());
        } catch (Exception e) {
            log.error("", e);
            return Msg.ERROR;
        }

        if (bitchWallet != null) {
            return getMsg(Status.OK, getApplyAddressRes(bitchWallet.getUserid(), bitchWallet.getAddress(), coinname.getCoinName()));
        }

        bitchWallet = web3jService.createAddress(userId, coinname);

        if (bitchWallet == null) {
            return Msg.ERROR;
        }

        try {
            walletMapper.insert(bitchWallet);
        } catch (Exception e) {
            log.error("", e);
            return Msg.ERROR;
        }

        Map<String, Object> resMap = getApplyAddressRes(userId, bitchWallet.getAddress(), coinname.getCoinName());
        return getMsg(Status.OK, resMap);
    }

    public BitchWallet getBitchWalletByAddress(String address) throws Exception {
        return walletMapper.findOneByAddress(address);
    }

    private Map<String, Object> getApplyAddressRes(int userId, String address, String coinname) {
        return new HashMap<String, Object>() {{
            put("userid", userId);
            put("address", address);
            put("coinname", coinname);
        }};
    }

	public List<BitchWallet> getBitchTx() {
		return walletMapper.selectBitchTx();
	}
}
