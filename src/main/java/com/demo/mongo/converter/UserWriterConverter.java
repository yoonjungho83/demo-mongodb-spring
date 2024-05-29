package com.demo.mongo.converter;

import org.springframework.core.convert.converter.Converter;

import com.demo.mongo.model.entity.mongo.UserMst;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class UserWriterConverter implements Converter<UserMst, DBObject> {

    @Override
    public DBObject convert(final UserMst user) {
        
    	System.out.println("UserWriterConverter > convert" );
    	final DBObject dbObject = new BasicDBObject();
//        dbObject.put("userId", user.getId());
//        dbObject.put("age", user.getAge());
//        if (user.getAddress() != null) {
//            final DBObject addrDbObject = new BasicDBObject();
//            addrDbObject.put("value", user.getAddress().getId());
//            dbObject.put("address", addrDbObject);
//        }
//        dbObject.removeField("_class");
        return dbObject;
    }

}
