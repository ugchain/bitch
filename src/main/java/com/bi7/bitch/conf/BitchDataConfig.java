package com.bi7.bitch.conf;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

import javax.sql.DataSource;

/**
 * Created by foxer on 2017/8/23.
 */
@Configuration
@MapperScan(basePackages = BitchDataConfig.PACKAGE, sqlSessionFactoryRef = "bitchDataSqlSessionFactory")
public class BitchDataConfig {
    static final String PACKAGE = "com.bi7.bitch.mapper.primary";

    @Bean(name = "bitchDataDataSource")
    @Primary
    @ConfigurationProperties(prefix = "datasource.primary")
    public DataSource DataSourceBitch() {
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "bitchDataTransactionManager")
    @Primary
    public DataSourceTransactionManager bitchDataTransactionManager(@Qualifier("bitchDataDataSource") DataSource bitchDataSource) {
        return new DataSourceTransactionManager(bitchDataSource);
    }

    @Bean(name = "bitchDataSqlSessionFactory")
    @Primary
    public SqlSessionFactory bitchDataSqlSessionFactory(@Qualifier("bitchDataDataSource") DataSource bitchDataSource) throws Exception {
        final SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(bitchDataSource);
        return sessionFactory.getObject();
    }
}
