package com.bi7.bitch.service;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.request.RawTransaction;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import com.bi7.bitch.Logs;
import com.bi7.bitch.conf.CoinName;
import com.bi7.bitch.dao.CoinDao;
import com.bi7.bitch.dao.TxDao;
import com.bi7.bitch.dao.model.BitchCoin;
import com.bi7.bitch.dao.model.BitchTx;
import com.bi7.bitch.dao.model.BitchWallet;
import com.bi7.bitch.dao.model.CoinTypeEnum;
import com.bi7.bitch.dao.model.WithdrawStatusEnum;
import com.bi7.bitch.response.Msg;
import com.bi7.bitch.response.Status;
import com.bi7.bitch.util.DecimalsUtil;
import com.bi7.web3j.tx.LocalTransaction;

/**
 * Created by foxer on 2017/10/31.
 */

@Service
public class TxService {

    private final static Log log = Logs.getLogger(TxService.class);
    
    //TODO eth系列的汇总地址应该由配置文件决定
    private final static String ETHS_TOTAL_ADDRESS = "";
    
    //TODO 合约币账户上最少的eth余额
    private final static BigInteger ETH_MIN_USABLE_BALANCE = new BigInteger("1");

    //TODO eth系列的充值地址
	private static final String ETHS_ADD_ACCOUNT_ADDRESS = "";
	//TODO MIN_TRANS_ETH
	private static final String MIN_TRANS_ETH = null;
	
	//TODO min confirm block count
	private static final int MIN_CONFRIM_BLOCK_COUNT = 12;
    //TODO FEETOTALPATH save feeTotal
	private String feeTotalPath;
    
    @Autowired
    private TxDao txDao;
    
    @Autowired
    private Web3j web3;
    
    @Autowired
    private DecimalsUtil decimalsUtil;
    
    @Autowired
    private CoinDao coinDao;

    
    /**
     * 扫描bitchWallet
     */
    
    @Transactional(value = "bqiDataTransactionManager")
    public void scanBitchWallet(String coinname) {
    	List<BitchWallet> bitchWallets = null;
    	try {
    		bitchWallets = txDao.selectBitchWallet(coinname);
		} catch (Exception e) {
			log.error("selectBitchWallet error " + e);
			throw  new RuntimeException();
		}
		
    	bitchWallets.forEach(bitchWallet -> {
			//每个记录的处理
    		//扫描数据-汇总-打包成tx
    		//对合约币进行 
    		CoinName.itor(coinName -> {
    			
    			if(coinName == CoinName.ETH) {
    				return;
    			}
    			
    			BigInteger balance = coinName.getCoin().getBalance(bitchWallet.getAddress());
    			
    			//如果balance > 设置的最小值
    			if(balance.compareTo(coinName.getMinTransValue()) > 0) {
        			//汇总-
    				//检查eth数量-够/-------不够（转eth- bitchtx 检查to和address匹配，检查blockNumber=0  和 12 验证是否到证）
    				//如果某个地址和汇总地址有未确认的交易，直接跳过。
    				//不够跳
    				//检查eth数量够不够
    				if(!hasEthUsableBalances(bitchWallet.getAddress())) {
    					return;
    				}
    				
    				boolean hasUnconfirmTrans;
    				try {
    					hasUnconfirmTrans = hasUnconfirmTrans(bitchWallet.getAddress());
					} catch (Exception e) {
						log.error("unconfirmTrans error" + e);
						throw new RuntimeException();
					}
    				
    				if(hasUnconfirmTrans) {
    					return;
    				}
    				
    				//将代币转入汇总地址
    				//生成Trans所用的参数
    				//TODO privatekey 生成签名 gasPrice gasLimit(交易--转账)
    				String privateKey = "";
    				int gasPrice = 0;
    				int gasLimit = 0;
    				Credentials credentials = Credentials.create(privateKey);
    				
    				try {
						innerTrans(bitchWallet.getAddress(), ETHS_TOTAL_ADDRESS, balance.toString(), gasPrice, gasLimit, coinName, credentials);
					} catch (Exception e) {
						log.error(e);
						throw new RuntimeException();
					}
        		}
    		});
    		
    		//处理eth
    		BigInteger balance = CoinName.ETH.getCoin().getBalance(bitchWallet.getAddress());
    		if(hasEthUsableBalances(bitchWallet.getAddress())) {
				//将代币转入汇总地址
				//生成Trans所用的参数
				//TODO privatekey 生成签名 gasPrice gasLimit
				String privateKey = "";
				int gasPrice = 0;
				int gasLimit = 0;
				Credentials credentials = Credentials.create(privateKey);
				
				try {
					innerTrans(bitchWallet.getAddress(), ETHS_TOTAL_ADDRESS, balance.toString(), gasPrice, gasLimit, CoinName.ETH, credentials);
				} catch (Exception e) {
					log.error(e);
					throw new RuntimeException();
				}
			}
		});
		
//		BitchTx bTx = new BitchTx();
//		//将此tx保存到bitch-tx
//		txDao.saveTxToBitchTx(bTx);
    }
    
    /**
     * 判断和汇总地址是否有未确认的交易
     * @param address
     * @return
     * @throws Exception 
     */
    private boolean hasUnconfirmTrans(String address) throws Exception {
    	
    	List<BitchTx> txInfoList = null;
		//判断是否和汇总地址有未确认的交易
		txInfoList = txDao.txInfoByAddress(address);
		
		Iterator<BitchTx> it = txInfoList.iterator();
		
		while(it.hasNext()) {
			BitchTx txInfo = it.next();
			if(txInfo.getTo() == ETHS_TOTAL_ADDRESS) {
				if(txInfo.getBlockNumber() == 0 || (getCurrentBlockNumber() - txInfo.getBlockNumber()) < 12) {
					return true;
				}
			}
		}
		
		return false;
	}

	/**
     * 查看合约币账户上是否有可用的eth
     * @param address
     * @return true:usable false:unusable
     */
    private boolean hasEthUsableBalances(String address) {
    	
    	BigInteger balance = CoinName.ETH.getCoin().getBalance(address);
    	
    	if(balance.compareTo(TxService.ETH_MIN_USABLE_BALANCE) > 0) {
    		return true;
    	}
    	return false;
    }
    
//    //检测账户是否有eth（有合约币，无eth， 检查to和address匹配，检查blockNumber=0  和 12 验证是否到证  从交易所提现账户转小笔eth，转完写入bitchtx ）
//    public void scanEthHased() {
//    	
//    }
    
    
//    //签名+发送 
//    private void innerTrans(String from, String to, String value, int gasPrice, int gasUsed, CoinName coinName, Credentials SY) {
//    	
//    }
    
    //签名+发送 
    private void innerTrans(String from, String to, String value, int gasPrice, int gasLimit, CoinName coinname, Credentials credentials) throws Exception {
    	//TODO chainId, AsyncTransfer protected how used?
//    	AsyncTransfer coin = new AsyncTransfer(web3, credentials, (byte)1);
//		LocalTransaction localTransaction = new LocalTransaction(null , gasPrice, gasLimit, from, to, "", value);
    	
    	BigInteger val = null;
        try {
            val = decimalsUtil.decode(value, coinname.getDecimals());
            if (val.compareTo(coinname.getWithdrawLimit()) > 0) {
                throw new Exception(String.format("withdraw value > limit,value: %s, fee: %s, limit: %d", val,  coinname.getWithdrawLimit()));
            }
        } catch (Exception e) {
            log.error("", e);
        }
        
        BigInteger gsP = decimalsUtil.decode(gasPrice+"", coinname.getDecimals());
        BigInteger gsL = decimalsUtil.decode(gasLimit+"", coinname.getDecimals());
        
        // get the next available nonce
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(
                     from, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        // create our transaction
        RawTransaction rawTransaction  = RawTransaction.createEtherTransaction(
                     nonce, gsP, gsL, to, val);

        // sign & send our transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();
        
        //获取transactionId
        String txid = ethSendTransaction.getTransactionHash();
        
        
        BitchTx tx = new BitchTx();
        tx.setBlockNumber(0);
        tx.setCoinname(coinname.getCoinName());
        tx.setFrom(from);
        tx.setTo(to);
        tx.setGasPrice(gasPrice);
        tx.setTxid(txid);
        tx.setValue(val.toString());
        tx.setTime(new Date());
        
        //记录到bitch-tx表中
        saveTxToBitchTx(tx);
        
    }
    
    private void saveTxToBitchTx(BitchTx tx) throws Exception {
    	txDao.saveTxToBitchTx(tx);
	}

	@Transactional(value = "bqiDataTransactionManager")
	public void scanTableToUpdate() {
		List<BitchTx> selectBitchTx = null;
		try {
			selectBitchTx = txDao.selectUnPackageTx();
		} catch (Exception e) {
			log.error(e);
			throw new RuntimeException();
		}
		
		selectBitchTx.forEach(tx -> {
			
			//在链上查询blockNumber（当前）
			int currentBlockNumber = getCurrentBlockNumber();
			//根据txid查询该交易信息
			TransactionReceipt transReceipt = getBlockByTxid(tx.getTxid());
			if(transReceipt.getBlockHash() != null || transReceipt.getBlockHash() != "") {
				//上链后设置值
				tx.setBlockNumber(transReceipt.getBlockNumber().intValue());
				tx.setGasUsed(transReceipt.getGasUsed().intValue());
			}
			
			if(currentBlockNumber != 0) {
				//如果超过12个确认块，updateblockNumber
				if((currentBlockNumber - tx.getBlockNumber()) > MIN_CONFRIM_BLOCK_COUNT) {
					
					//此时的tx已经重新设置
					try {
						txDao.upTxStatus(tx);
					} catch (Exception e) {
						log.error("upTxStatus error " + e);
						throw new RuntimeException();
					}
				}
				
			}
		});
		
	}
    
    
    
//    //扫表，检查bitchtx 表 blockNumber=0,在链上查，若查到，更新状态，查不到啥也不干(做了一个定时任务)
//    public void scanTableUpdate() {
//    	
//    }
    
    /**
     * 扫描bitch-wallet，给eth不足的账户充值eth，用于转移合约币
     */
    public void scanTableToAddEth() {
    	List<BitchWallet> bitchWallets = null;
    	try {
//    		bitchWallets = txDao.selectBitchWallet(coinname);
    		bitchWallets = txDao.selectAllBitchWallet();
		} catch (Exception e) {
			log.error(e);
			throw  new RuntimeException();
		}
		
    	bitchWallets.forEach(bitchWallet -> {
    		CoinName.itor(coinName -> {
    			if(coinName == CoinName.ETH) {
    				return;
    			}
    			BigInteger balance = coinName.getCoin().getBalance(bitchWallet.getAddress());
    			
    			//如果balance > 设置的最小值
    			if(balance.compareTo(coinName.getMinTransValue()) > 0) {
    				//检查eth数量够不够
    				if(!hasEthUsableBalances(bitchWallet.getAddress())) {
        				boolean hasUnconfirmTrans;
        				try {
        					hasUnconfirmTrans = hasUnconfirmTransPreAdd(bitchWallet.getAddress());
    					} catch (Exception e) {
    						log.error(e);
    						throw new RuntimeException();
    					}
        				
        				if(hasUnconfirmTrans) {
        					return;
        				}
        				
        				//执行转入eth操作
    					String privateKey = "";
    					int gasPrice = 0;
    					int gasLimit = 0;
    					Credentials credentials = Credentials.create(privateKey);
    					
    					try {
    						innerTrans(ETHS_ADD_ACCOUNT_ADDRESS, bitchWallet.getAddress(), MIN_TRANS_ETH, gasPrice, gasLimit, coinName, credentials);
    					} catch (Exception e) {
    						log.error(e);
    						throw new RuntimeException();
    					}
    				}
        		}
    		});
    	});
    }
    
    /**
     * 转入eth前，判断和交易所地址是否有未确认的交易
     * @param address
     * @return
     * @throws Exception
     */
    private boolean hasUnconfirmTransPreAdd(String address) throws Exception {
    	
    	List<BitchTx> txInfoList = null;
		//判断是否和汇总地址有未确认的交易
		txInfoList = txDao.txInfoByAddress(address);
		
		Iterator<BitchTx> it = txInfoList.iterator();
		
		while(it.hasNext()) {
			BitchTx txInfo = it.next();
			if(txInfo.getFrom() == ETHS_ADD_ACCOUNT_ADDRESS) {
				if(txInfo.getBlockNumber() == 0 || (getCurrentBlockNumber() - txInfo.getBlockNumber()) < MIN_CONFRIM_BLOCK_COUNT) {
					return true;
				}
			}
		}
		return false;
	}
    
    
	/**
	 * 获取当前的块号
	 * @return
	 */
	private int getCurrentBlockNumber() {
		Request<?, EthBlockNumber> ethBlockNumberRequest = web3.ethBlockNumber();
        int blockNumber = 0;
        try {
            EthBlockNumber ethBlockNumber = ethBlockNumberRequest.send();
            blockNumber = ethBlockNumber.getBlockNumber().intValue();
        } catch (IOException e) {
            e.printStackTrace();
            log.error("ethBlockNumberRequest error ", e);
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("", e);
            return 0;
        }
        return blockNumber;
	}
	
	/**
	 * 根据txId查询交易信息
	 * @param txId
	 */
	private TransactionReceipt getBlockByTxid(String txId) {
		try {
            EthGetTransactionReceipt transactionReceipt = web3.ethGetTransactionReceipt(txId).send();
            if (transactionReceipt.hasError()) {
                log.error(transactionReceipt.getError().getMessage());
                return new TransactionReceipt();
            } else {
               return transactionReceipt.getResult();
            }
        } catch (IOException e) {
            log.error("", e);
            return new TransactionReceipt();
        }
	}
	
	/**
	 * 扫描bitch-tx表统计所有fee，记录到文件中
	 */
	public void statisFees() {
		List<BitchTx> selectBitchTx = null;
		try {
			selectBitchTx = txDao.selectBitchTx();
		} catch (Exception e) {
			log.error(e);
		}
		
		BigInteger feeTotal = new BigInteger("0");
		
		selectBitchTx.forEach(bitTx -> {
			
			if(bitTx.getFee() != null) {
				feeTotal.add(new BigInteger(bitTx.getFee()));
			}
		});
		
		//将feeTotal记录到文件中
		try {
            FileWriter fw = new FileWriter(feeTotalPath, true);
            fw.write(String.format("%s\t%d\n", new Date().toString(), feeTotal));
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
}
