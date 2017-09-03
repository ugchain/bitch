package com.bi7.bitch.mapper.secondary;

import com.bi7.bitch.conf.AppConfig;
import com.bi7.bitch.dao.model.MyzrModel;
import com.bi7.bitch.mapper.secondary.provider.MyzrProvider;
import org.apache.ibatis.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by foxer on 2017/8/24.
 */
@Mapper
public interface MyzrMapper {

    @InsertProvider(type = MyzrProvider.class, method = "insert")
    void insert(MyzrModel model);

    @SelectProvider(type = MyzrProvider.class, method = "findZrid")
    int findZrid(@Param(value = "txid") String txid);

    @UpdateProvider(type = MyzrProvider.class, method = "updateStatus")
    void updateStatus(@Param(value = "id") int zcId, @Param(value = "status") int status);
}
