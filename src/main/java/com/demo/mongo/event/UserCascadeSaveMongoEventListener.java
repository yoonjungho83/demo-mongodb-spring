package com.demo.mongo.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import com.demo.mongo.model.entity.UserMst;

public class UserCascadeSaveMongoEventListener extends AbstractMongoEventListener<Object>{

	@Autowired
    private MongoOperations mongoOperations;
	
	@Override
    public void onBeforeConvert(final BeforeConvertEvent<Object> event) {
        final Object source = event.getSource();
        if ((source instanceof UserMst) && (((UserMst) source).getAddress() != null)) {
            mongoOperations.save(((UserMst) source).getAddress());
        }
    }
}
