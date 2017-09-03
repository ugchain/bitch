package com.bi7.bitch.mapper.primary;

import com.bi7.bitch.dao.model.BitchWallet;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

/**
 * Created by foxer on 2017/8/24.
 */
@Mapper
@Component
public interface WalletMapper {

    @Insert(value = "insert into bitch_wallet (userid,coinname,address,sha3,addtime,keystore,filename) values (#{userid},#{coinname},#{address},#{sha3},#{addtime},#{keystore},#{filename})")
    void insert(BitchWallet wallet) throws Exception;

    @Select(value = "select * from bitch_wallet where userid = #{userid} and coinname = #{coinname}")
    BitchWallet findOne(@Param(value = "userid") int userid, @Param(value = "coinname") String coinname) throws Exception;

    @Select(value = "select * from bitch_wallet where address = #{address}")
    BitchWallet findOneByAddress(@Param(value = "address") String address) throws Exception;
}
