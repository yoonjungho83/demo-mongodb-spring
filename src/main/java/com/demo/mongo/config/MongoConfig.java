package com.demo.mongo.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.demo.mongo.converter.OffsetDateTimeReadConverter;
import com.demo.mongo.converter.OffsetDateTimeWriteConverter;
import com.demo.mongo.converter.UserWriterConverter;
import com.demo.mongo.event.CascadeSaveMongoEventListener;
import com.demo.mongo.event.UserCascadeSaveMongoEventListener;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(basePackages = "com.demo.mongo.repository")
public class MongoConfig  extends AbstractMongoClientConfiguration{//

	private final List<Converter<?, ?>> converters = new ArrayList<Converter<?, ?>>();

	
	@Value("${spring.data.mongodb.database}")
	private String database;
	@Value("${spring.data.mongodb.username}")
	private String username;
	@Value("${spring.data.mongodb.password}")
	private String password;
	@Value("${spring.data.mongodb.host}")
	private String host;
	@Value("${spring.data.mongodb.port}")
	private String port;
	@Value("${spring.data.mongodb.authentication-database}")
	private String authenticationDB;
	
	@Override
	protected String getDatabaseName() {
		return database;
	}
	
	@Override
	public MongoClient mongoClient() {
		String mongoCon = "mongodb://"+username+":"+password+"@"+host+":"+port+"/dbName?authSource="+authenticationDB;
		ConnectionString connectionString = new ConnectionString(mongoCon);
        MongoClientSettings mongoClientSettings = 
        			MongoClientSettings.builder()
        			                   .applyConnectionString(connectionString)
        			                   .build();
        return MongoClients.create(mongoClientSettings);
	}
	
	@Override
	protected Collection<String> getMappingBasePackages() {
		// TODO Auto-generated method stub
		return Collections.singleton("com.demo.mongo");
	}
	
	@Override
    protected boolean autoIndexCreation() {
        return true;
    }
	
	
	
	@Override
	public MongoCustomConversions customConversions() {
		
		/* OffsetDateTime converter : mongodb는 OffsetDateTime을 저장할 수 없으므로 컨버터를 이용해 저장하고 가져올때 다시 바꿔줘야함 */
		converters.add(new OffsetDateTimeReadConverter());
		converters.add(new OffsetDateTimeWriteConverter());
		converters.add(new UserWriterConverter());
		return new MongoCustomConversions(converters);
	}
	
	
	@Bean
	public MappingMongoConverter mappingMongoConverter(MongoDatabaseFactory mongoDatabaseFactory, MongoMappingContext mongoMappingContext) 
	{
		DbRefResolver     dbRefResolver = new DefaultDbRefResolver(mongoDatabaseFactory);
		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext);
		
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		return converter;
	}
	
	
	
	@Bean
    public UserCascadeSaveMongoEventListener userCascadingMongoEventListener() {
        return new UserCascadeSaveMongoEventListener();
    }

    @Bean
    public CascadeSaveMongoEventListener cascadingMongoEventListener() {
        return new CascadeSaveMongoEventListener();
    }
    
    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }
	
	
	
	
	
	
	

	   
	

}
