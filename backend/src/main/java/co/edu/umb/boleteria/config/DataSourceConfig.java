package co.edu.umb.boleteria.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class DataSourceConfig {
    @Bean @ConfigurationProperties("spring.datasource.write") HikariDataSource writeDataSource(){return new HikariDataSource();}
    @Bean @ConfigurationProperties("spring.datasource.read") HikariDataSource readDataSource(){return new HikariDataSource();}
    @Bean RoutingDataSource routingDataSource(@Qualifier("writeDataSource") DataSource w,@Qualifier("readDataSource") DataSource r){var ds=new RoutingDataSource();ds.setTargetDataSources(Map.of("write",w,"read",r));ds.setDefaultTargetDataSource(w);ds.afterPropertiesSet();return ds;}
    @Bean @Primary DataSource dataSource(@Qualifier("routingDataSource") DataSource r){return new LazyConnectionDataSourceProxy(r);}
}
