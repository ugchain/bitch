package com.bi7.bitch.mapper.secondary;

import com.bi7.bitch.dao.model.MyzcModel;
import com.bi7.bitch.dao.model.MyzrModel;
import com.bi7.bitch.mapper.secondary.provider.MyzcProvider;
import com.bi7.bitch.mapper.secondary.provider.MyzrProvider;
import org.apache.ibatis.annotations.*;

/**
 * Created by foxer on 2017/8/24.
 */
@Mapper
public interface MyzcMapper {
    @UpdateProvider(type = MyzcProvider.class, method = "updateStatus")
    void updateStatus(@Param(value = "id") int zcId, @Param(value = "status") int status);

    @UpdateProvider(type = MyzcProvider.class, method = "updateTxidAndStatus")
    void updateTxidAndStatus(@Param(value = "id") int zcId, @Param(value = "txid") String txId, @Param(value = "status") int status);

    @SelectProvider(type = MyzcProvider.class, method = "findOne")
    MyzcModel findOne(@Param(value = "id") int zcId);
}
