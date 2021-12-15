package com.edw.config;

import com.edw.mapper.KodeposMapper;
import com.edw.mapper.RegionsMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * <pre>
 *     com.edw.config.MyBatisSqlSessionFactory
 * </pre>
 *
 * @author Muhammad Edwin < edwin at redhat dot com >
 * 13 Des 2021 15:17
 */
public class MyBatisSqlSessionFactory {
    private static final SqlSessionFactory FACTORY;

    static {
        try {
            Reader reader = Resources.getResourceAsReader("configuration.xml");
            FACTORY = new SqlSessionFactoryBuilder().build(reader);
            FACTORY.getConfiguration().addMapper(KodeposMapper.class);
            FACTORY.getConfiguration().addMapper(RegionsMapper.class);
        } catch (IOException e) {
            throw new RuntimeException("Fatal Error. Cause: " + e, e);
        }
    }

    public static SqlSessionFactory getSqlSessionFactory() {
        return FACTORY;
    }
}
