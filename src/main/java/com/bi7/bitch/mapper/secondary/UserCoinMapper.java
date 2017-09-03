package com.bi7.bitch.mapper.secondary;

import com.bi7.bitch.mapper.secondary.provider.UserCoinProvider;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.UpdateProvider;

/**
 * Created by foxer on 2017/8/26.
 */
@Mapper
public interface UserCoinMapper {

    @UpdateProvider(type = UserCoinProvider.class, method = "updateCoinBalance")
    void updateCoinBalance(@Param(value = "userid") int userid, @Param(value = "coinname") String coinname, @Param(value = "incval") String incVal);

}
