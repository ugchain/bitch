

### bitch 项目java服务

* 地址申请
    代码上有示例
* 充值监听
	step1: 监听区块链
	step2: 解析区块数据，识别出所有已上线币种的合约，解析交易双方
	step3: 根据转入地址 在 表 bitch_wallet 中查找userid
	step4: 给 bitch_charge 与 movesay_myzr 均插入数据

* 提币广播服务
    代码上有示例

### 离线任务-充值   负责
crontab 1 min exec:
检查区块确认数超过12 的 bitch_charge.status == 1 && movesay_myzr.status == 1 的记录，调用
数据库事务：movesay_myzr.status = 2 && movesay_user_coin.coin += val；
并且设 bitch_charge.status = 2；
若发现 txid 不存在，设 status = 3；

### 离线任务-提现   负责
crontab 1min exec:
检查 bitch_withdraw.status == 1 &&  movesay_myzc.status == 1 的记录 
根据txid，检查，若确认数 超过12 ，那么将 2个数据库的 status 设为2 
若其他情况，设 status 3，php程序会新创建 一个 withdraw请求，不同的 zcid


###本地数据库 dbName : bitch
table : bitch_withdraw
colmn:  
	id 自增
	userid
	coinname
	txid
	value
	addtime
	updatetime
	status    0:审核中   1：区块确认中   2:提现成功    3:提现失败    

table : bitch_wallet
colmn:
	userId 
	coinname //realcoinname 例如 eths
	address
	addtime

table : bitch_charge
colmn :
	id 自增
	userId
	from
	to
	value
	blockNumber
	txId
	addtime
	status（movesay_myzr.status含义相同）   1:区块确认中   2:充值成功   3:充值失败(txid后续不存在)


###认证安全
所有的请求接口都必须使用
sign字段如何生成
	双方协商秘钥 prikey
	有效参数按照字母序排列，message  =  "key1=val1&key2=val2&prikey=AAASSDFADFASDFAS"
	sign=MD5(message)

