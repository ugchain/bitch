package com.bi7.bitch.mapper.primary;

import com.bi7.bitch.dao.model.BitchCoin;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by foxer on 2017/8/24.
 */
@Mapper
@Component
public interface CoinMapper {

    @Insert(value = "insert into bitch_coin (rid,userid,coinname,`type`,`from`,`to`,`value`,fee,blockNumber,txid,gasUsed,gasPrice,`status`,addtime,updatetime) values (#{rid},#{userid},#{coinname},#{type},#{from},#{to},#{value},#{fee},#{blockNumber},#{txid},#{gasUsed},#{gasPrice},#{status},#{addtime},#{updatetime})")
    void insert(BitchCoin bitchCoin);

    @Update(value = "update bitch_coin c set c.blockNumber = #{blockNumber},c.status = #{status},c.gasUsed = #{gasUsed} where c.rid = #{rid} and c.type=#{type}")
    void updateBlockNumberAndStatus(@Param(value = "rid") int rid, @Param(value = "blockNumber") int blockNumber, @Param(value = "status") int status, @Param(value = "gasUsed") int gasUsed, @Param(value = "type") int type);

    @Update(value = "update bitch_coin c set c.status = #{#status} where c.rid = #{rid} and c.type = #{type}")
    void updateStatus(@Param(value = "rid") int rid, @Param(value = "status") int status, @Param(value = "type") int type);

    @Select(value = "select * from bitch_coin where txid = #{txid}")
    BitchCoin findOne(@Param(value = "txid") String txid);

    @Select(value = "select * from bitch_coin where status = #{status} and type=#{type}")
    List<BitchCoin> findAll(@Param(value = "type") int type, @Param(value = "status") int status);
}
