package com.bi7.web3j.tx;

import com.bi7.bitch.util.TransactionUtil;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.web3j.utils.Async;

import java.io.File;

/**
 * Created by fanjl on 2017/10/13.
 */
public class AsyncTransferBtc {
    protected String walletFile;
    protected Wallet wallet;

    protected AsyncTransferBtc(String walletFile) {
        this.walletFile = walletFile;
        loadWallet(walletFile);
    }
    @Deprecated
    protected String buildRawTx(SendRequest request)  {
        try {
            wallet.completeTx(request);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
        String raw = TransactionUtil.bytesToHexString(request.tx.bitcoinSerialize());
        return raw;
    }


    protected void send(Transaction rawTx) {
        wallet.commitTx(rawTx);
    }


    protected void loadWallet(String walletFile){
        try {
            wallet = Wallet.loadFromFile(new File(walletFile));
        } catch (UnreadableWalletException e) {
            e.printStackTrace();
        }
    }


}
