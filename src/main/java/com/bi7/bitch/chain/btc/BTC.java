package com.bi7.bitch.chain.btc;

import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.conf.CoinName;
import com.bi7.web3j.tx.LocalTransaction;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Optional;

/**
 * Created by fanjl on 2017/10/12.
 */
public class BTC extends AbstractBtcCoin {

    public BTC(String chainFile, String walletFile, NetworkParameters parameters)  {
        super(chainFile, walletFile,parameters);
    }


    @Override
    public CoinName getCoinName() {
        return CoinName.BTC;
    }
}
