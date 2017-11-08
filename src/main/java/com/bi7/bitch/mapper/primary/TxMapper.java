package com.bi7.bitch.mapper.primary;

import com.bi7.bitch.dao.model.BitchTx;
import com.bi7.bitch.dao.model.BitchWallet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * Created by foxer on 2017/10/31.
 */
@Mapper
@Component
public interface TxMapper {
    @Insert(value = "insert into bitch_tx (`id`,coinname,`from`,`to`, blockNumber, txid, `value`, fee, gasPrice, gasUsed, `time`) values (#{id},#{coinname},#{from},#{to},#{blockNumber},#{txid},#{value},#{fee},#{gasPrice},#{gasUsed},#{time})")
    void insert(BitchTx tx) throws Exception;

    @Select(value = "select * from bitch_tx where blockNumber = 0")
    List<BitchTx> findUnPackageTx() throws Exception;

    @Select(value = "select * from bitch_tx where `time` > #{startTime} and `time` < #{endTime}")
    List<BitchTx> findAllTxByDateRange(@Param(value = "startTime") Date startTime, @Param(value = "endTime") Date endTime);

    @Select(value = "update bitch_tx set blockNumber = #{blockNumber},fee = #{fee}, gasPrice  = #{gasPrice},gasUsed= #{gasUsed} where txid = #{txid}")
    void update(@Param(value = "txid") String txid, @Param(value = "blockNumber") int blockNumber, @Param(value = "fee") String fee, @Param(value = "gasPrice") int gasPrice, @Param(value = "gasUsed") int gasUsed) throws Exception;

    @Select(value = "select * from bitch_tx where `from` = #{from}")
    List<BitchTx> txInfoByFromAddress(@Param(value = "from") String from) throws Exception;

    @Select(value = "select * from bitch_tx where blockNumber <> 0")
    List<BitchTx> selectBitchTx();

}
