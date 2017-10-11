CREATE TABLE `bitch_coin` (
  `rid` int(11) NOT NULL,
  `userid` int(11) NOT NULL,
  `coinname` varchar(45) NOT NULL,
  `type` int(11) NOT NULL COMMENT '0 withdraw\n1 charge',
  `from` varchar(100) NOT NULL,
  `to` varchar(100) NOT NULL,
  `value` varchar(100) NOT NULL,
  `fee` varchar(100) NOT NULL,
  `blockNumber` int(11) NOT NULL,
  `txid` varchar(100) NOT NULL,
  `gasUsed` int(11) NOT NULL,
  `gasPrice` int(11) NOT NULL COMMENT 'gwei',
  `status` int(11) NOT NULL COMMENT ' 1:åŒºå—ç¡®è®¤ä¸­   2:å……å€¼æˆåŠŸ   3:å……å€¼å¤±è´¥(txidåŽç»­ä¸å­˜åœ¨)',
  `addtime` datetime NOT NULL,
  `updatetime` datetime DEFAULT NULL,
  PRIMARY KEY (`rid`,`type`),
  KEY `bn` (`blockNumber`),
  KEY `sta` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
CREATE TABLE `bitch_wallet` (
  `userid` int(11) NOT NULL,
  `coinname` varchar(45) NOT NULL,
  `address` varchar(45) NOT NULL,
  `sha3` varchar(100) NOT NULL,
  `addtime` datetime NOT NULL,
  `keystore` varchar(1000) NOT NULL,
  `filename` varchar(100) NOT NULL,
  PRIMARY KEY (`userid`,`coinname`),
  KEY `PKEY` (`address`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
