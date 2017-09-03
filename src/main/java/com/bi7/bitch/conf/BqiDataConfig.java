package com.bi7.bitch.conf;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by foxer on 2017/8/23.
 */
@Configuration
@MapperScan(basePackages = BqiDataConfig.PACKAGE, sqlSessionFactoryRef = "bqiDataSqlSessionFactory")
public class BqiDataConfig {
    static final String PACKAGE = "com.bi7.bitch.mapper.secondary";

    @Bean(name = "bqiDataDataSource")
    @ConfigurationProperties(prefix = "datasource.secondary")
    public DataSource DataSourceBqi() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "bqiDataTransactionManager")
    public DataSourceTransactionManager bqiDataTransactionManager(@Qualifier("bqiDataDataSource") DataSource bqiDataSource) {
        return new DataSourceTransactionManager(bqiDataSource);
    }

    @Bean(name = "bqiDataSqlSessionFactory")
    public SqlSessionFactory bqiDataSqlSessionFactory(@Qualifier("bqiDataDataSource") DataSource bqiDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(bqiDataSource);
        return sessionFactory.getObject();
    }


}
