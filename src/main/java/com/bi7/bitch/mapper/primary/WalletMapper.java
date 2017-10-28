package com.bi7.bitch.mapper.primary;

import com.bi7.bitch.dao.model.BitchDistribute;
import com.bi7.bitch.dao.model.BitchWallet;
import com.bi7.bitch.dao.model.BitchWalletAddress;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

/**
 * Created by foxer on 2017/8/24.
 */
@Mapper
@Component
public interface WalletMapper {

    @Insert(value = "insert into bitch_wallet (userid,coinname,address,sha3,addtime,keystore,filename) values (#{userid},#{coinname},#{address},#{sha3},#{addtime},#{keystore},#{filename})")
    void insert(BitchWallet wallet) throws Exception;

    @Insert(value = "insert into bitch_wallet_address (userid,coinname,address,address_check,addtime) values (#{userid},#{coinname},#{address},#{addressCheck},#{addtime})")
    void insertNewBitchWallet(BitchWalletAddress wallet) throws Exception;

    @Select(value = "select * from bitch_wallet where userid = #{userid} and coinname = #{coinname}")
    BitchWallet findOne(@Param(value = "userid") int userid, @Param(value = "coinname") String coinname) throws Exception;

    @Select(value = "select * from bitch_distribute where status = #{status} and coinname = #{coinname} order by id limit 1 for update ")
    @Results(id = "bitchDistribute", value = {
            @Result(property = "addressCheck", column = "address_check"),
            @Result(property = "addtime", column = "addtime")
    })
    BitchDistribute findWaitForDistributeAddress(@Param(value="status") int status,@Param(value = "coinname") String coinname) throws  Exception ;

    @Update(value = "update bitch_distribute set status=#{status} where id = #{id}")
    void updateAddressStatus(@Param(value="id") int id,@Param(value = "status") int status) throws Exception;

    @Delete(value = "delete from bitch_distribute where id = #{id}")
    void deleteUsedAddress(@Param(value="id") int id) ;

    @Select(value = "select * from bitch_wallet where address = #{address}")
    BitchWallet findOneByAddress(@Param(value = "address") String address) throws Exception;

    @Select(value = "select exists(select * from bitch_wallet_address where userid = #{userid} and coinname=#{coinname})")
    boolean findExistsByPriKey(@Param(value = "userid") int userid,@Param(value = "coinname") String coinname) throws Exception;

}
