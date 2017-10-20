package com.bi7.bitch.chain.btc;

import com.bi7.bitch.Logs;
import com.bi7.bitch.chain.ICoin;
import com.bi7.bitch.chain.InputData;
import com.bi7.bitch.util.TransactionUtil;
import com.bi7.web3j.tx.AsyncTransferBtc;
import com.bi7.web3j.tx.LocalTransaction;
import org.apache.commons.logging.Log;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.BlockChain;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionBroadcast;
import org.bitcoinj.core.TransactionConfidence;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.core.UTXO;
import org.bitcoinj.core.listeners.DownloadProgressTracker;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.bitcoinj.store.SPVBlockStore;
import org.bitcoinj.wallet.KeyChain;
import org.bitcoinj.wallet.SendRequest;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

/**
 * Created by fanjl on 2017/10/11.
 */
public abstract class AbstractBtcCoin extends AsyncTransferBtc implements ICoin {

    private final static Log log = Logs.getLogger(AbstractBtcCoin.class);
    protected NetworkParameters networkParameters;
    protected BlockChain blockChain;
    protected SPVBlockStore chainStore;
    protected PeerGroup peers;

    DownloadProgressTracker bListener = new DownloadProgressTracker() {
        @Override
        public void doneDownload() {
            System.out.println("blockchain downloaded");
        }
    };


    public AbstractBtcCoin(String chainFile,String walletFile, NetworkParameters parameters) {
        super(walletFile);
        this.networkParameters = parameters;
        File chainF = new File(chainFile);
        try {
            chainStore = new SPVBlockStore(networkParameters, chainF);
            blockChain = new BlockChain(networkParameters, chainStore);
            peers = new PeerGroup(networkParameters, blockChain);
            peers.addPeerDiscovery(new DnsDiscovery(networkParameters));
            this.blockChain = new BlockChain(networkParameters, wallet, chainStore);
        } catch (BlockStoreException e) {
            e.printStackTrace();
        }
        blockChain.addWallet(wallet);
        peers.addWallet(wallet);
        peers.startAsync();
        peers.startBlockChainDownload(bListener);
        try {
            bListener.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        peers.stop();
    }

    public BigInteger getBalance(String address) {
        Coin addressBalance = wallet.getBalance(new AddressBalance(Address.fromBase58(networkParameters, address)));
        return new BigInteger(String.valueOf(addressBalance.getValue()));
    }

    /**
     * This is a raw transaction build method which need to find utxos from bitcore api.
     * @param utxoList
     * @param toAddress
     * @param amount
     * @return hex raw transaction
     */
    public String buildTxWithUtxos(List<UTXO> utxoList,String toAddress,long amount){
        //String to a private key
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameters,
                wallet.getActiveKeyChain().getKey(KeyChain.KeyPurpose.AUTHENTICATION).serializePrivB58(networkParameters));
        ECKey key = dumpedPrivateKey.getKey();
        //String to an address
        Address address = Address.fromBase58(networkParameters, toAddress);
        Transaction tx = new Transaction(networkParameters);
        //value is a sum of all inputs, fee is 4013
        tx.addOutput(Coin.valueOf(amount-4013), address);
        //utxos is an array of inputs from my wallet
        for(UTXO utxo : utxoList) {
            TransactionOutPoint outPoint = new TransactionOutPoint(networkParameters, utxo.getIndex(), utxo.getHash());
            tx.addSignedInput(outPoint, utxo.getScript(), key, Transaction.SigHash.ALL, true);
        }

        tx.getConfidence().setSource(TransactionConfidence.Source.SELF);
        tx.setPurpose(Transaction.Purpose.USER_PAYMENT);

        System.out.println(tx.getHashAsString());
        String hex = TransactionUtil.bytesToHexString(tx.bitcoinSerialize());
        return hex;
        //broadcast
//        peers.broadcastTransaction(tx);
    }
    public TransactionBroadcast walletSyncSendBtc(String address,BigInteger value){
        SendRequest request = SendRequest.to(Address.fromBase58(TestNet3Params.get(),address),Coin.valueOf(value.longValue()));
        try {
            wallet.completeTx(request);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
        wallet.commitTx(request.tx);
        TransactionBroadcast transactionBroadcast =  peers.broadcastTransaction(request.tx,1);
        return transactionBroadcast;
    }
    @Deprecated
    public LocalTransaction buildTx(String toAddress, BigInteger value) throws IOException {
        SendRequest request = SendRequest.to(Address.fromBase58(networkParameters, toAddress), Coin.valueOf(value.longValue()));
        // The SendRequest object can be customized at this point to modify how the transaction will be created.
        Transaction tx = request.tx;
        String raw = buildRawTx(request);
        LocalTransaction localTransaction = new LocalTransaction(this, toAddress, value,tx, tx.getHashAsString(), raw);
        return localTransaction;
    }


    public Optional<InputData> getTransactionById(String transactionHash) {
        return null;
    }

}
