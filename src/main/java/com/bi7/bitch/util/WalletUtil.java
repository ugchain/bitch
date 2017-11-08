package com.bi7.bitch.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.ObjectMapperFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by foxer on 2017/11/7.
 */
public class WalletUtil {

    public static Credentials loadCredentials(String password, byte[] data) throws IOException, CipherException {
        ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();
        WalletFile walletFile = objectMapper.readValue(data, WalletFile.class);
        return Credentials.create(Wallet.decrypt(password, walletFile));
    }
}
