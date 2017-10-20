package com.bi7.bitch.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by fanjl on 2017/10/13.
 */
@Configuration
@ConfigurationProperties(prefix = "btc")
public class BTCConfig {
    private String walletFilePath;
    private String chainFilePath;
    private String walletFilesDirectory;
    private String walletPassPhrase;
    private String walletFilePrefix;

    public String getChainFilePath() {
        return this.chainFilePath;
    }

    public void setChainFilePath(String chainFilePath) {
        this.chainFilePath = chainFilePath;
    }

    public String getWalletFilePrefix() {
        return this.walletFilePrefix;
    }

    public void setWalletFilePrefix(String walletFilePrefix) {
        this.walletFilePrefix = walletFilePrefix;
    }

    public String getWalletFilePath() {
        return this.walletFilePath;
    }

    public void setWalletFilePath(String walletFilePath) {
        this.walletFilePath = walletFilePath;
    }

    public String getWalletFilesDirectory() {
        return this.walletFilesDirectory;
    }

    public void setWalletFilesDirectory(String walletFilesDirectory) {
        this.walletFilesDirectory = walletFilesDirectory;
    }

    public String getWalletPassPhrase() {
        return this.walletPassPhrase;
    }

    public void setWalletPassPhrase(String walletPassPhrase) {
        this.walletPassPhrase = walletPassPhrase;
    }
}
