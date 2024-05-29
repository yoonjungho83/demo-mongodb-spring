package com.demo.mongo.support.mongoDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

import com.demo.mongo.common.DataUtil;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class AggregateBuilder {
	
	private MongoTemplate mongoTemplate;
	
	private List<AggregationOperation> operationList;
	private DataUtil dataUtil;

	
	public AggregationOperation aggregateLookup() {
		
		AggregationOperation lookupOperation = 
		(context) -> new Document
		("$lookup",
		 new Document("from", "RoleMst")
		 	.append("let", new Document("roleName1", "$roleName1"))
		    .append( "pipeline"
		    	   , List.of( new Document("$match",
		    			  		new Document("$expr",
		                           new Document( "$and"
		                        		       , List.of(new Document( "$eq"
		                            		                         , List.of("$roleName", "$$roleName1")
		                            		                         )
		                                                )
		                        		       )
		                        )
		                      )
		                   )
		    	   )
		    .append("as", "roleInfo")
		);
		
		return lookupOperation;
	}
	
	public AggregationOperation aggregateUnwind() {
		
		AggregationOperation unwindOperation = 
		(context) -> new Document
		("$unwind",
		 new Document("path", "$roleInfo")
		    .append("preserveNullAndEmptyArrays", true)
		);
				
		return unwindOperation;
	}
	

	public AggregationOperation aggregateMatch() {

		AggregationOperation matchOperation = 
		(context) -> new Document
		("$match",
		 new Document("$expr"
					 ,new Document( "$and"
								  , List.of( new Document("$regexMatch",
										                   new Document("input", "$userId")
										                   		.append("regex", "user0")
										                   		.append("options","i")	
								                          )
								           , new Document("$eq", Arrays.asList("$roleList.roleName", "ADMIN"))
								            )
								   )
		             )
		);

		return matchOperation;
	}
	
	public AggregationOperation aggregateGroup() {

		AggregationOperation matchOperation =null;

		return matchOperation;
	}
	
	
	
	
	public MatchOperation mongoCriteria() {
		List<Criteria> criteriaList = new ArrayList<>();
		criteriaList.add(Criteria.where("userId").regex("1000", "i"));
		criteriaList.add(Criteria.where("userId").is("user1"));
		MatchOperation matchOperation = Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		
		return matchOperation;
	}
	
	
	public SortOperation aggregateSort(String ...sort) {
		
		SortOperation sortOperation =
			Aggregation.sort(Sort.Direction.DESC , "user_id")
                       .and (Sort.Direction.ASC  , "RoleMst.roleName");
				
		return sortOperation;
	}
	
	//key 값이 테이블 컬럼명 ex)userMst.roleList.roleName
	//val 값이 마지막으로 표현될 컬럼명 roleName
	public ProjectionOperation aggregateProjection(HashMap<String,String> colMap) {
		
		ProjectionOperation projectionOperation = Aggregation.project();
		
		for (Map.Entry<String,String> entry : colMap.entrySet()) {
			String key = entry.getKey();
			String val = entry.getValue();
			projectionOperation.and(key).as(val);
		}
		
//		ProjectionOperation projectionOperation = Aggregation.project()
//		        .and("userId").as("userId")
//		        .and("userMst.roleList.roleName").as("roleName");
		
		return projectionOperation;
	}
	
	
	
	//mongoDB aggregate join code 
	public void execAggregate() {
		List<AggregationOperation> operationList = new ArrayList<>();
		
		//조인
		AggregationOperation lookupOperation = 
		(context) -> new Document
		("$lookup",
		 new Document("from", "RoleMst")
		 	.append("let", new Document("roleName1", "$roleName1"))
		    .append( "pipeline"
		    	   , List.of( new Document("$match",
		    			  		new Document("$expr",
		                           new Document( "$and"
		                        		       , List.of(new Document( "$eq"
		                            		                         , List.of("$roleName", "$$roleName1")
		                            		                         )
		                                                )
		                        		       )
		                        )
		                      )
		                   )
		    	   )
		    .append("as", "roleInfo")
		);
		operationList.add(lookupOperation);
		
		//filtering
		Criteria where1 = dataUtil.getCriteria("ne","roleList", "[]"); //where roleList is null
		Criteria where2 = dataUtil.getCriteria("eq","roleList.roleName", "USER");
		MatchOperation matchOperation = Aggregation
				.match(new Criteria().andOperator(Arrays.asList(where1,where2).toArray(new Criteria[0])));
		operationList.add(matchOperation);
		
		//전개
		UnwindOperation unwindOperation = Aggregation.unwind("RoleMst", true);
		operationList.add(unwindOperation);
		
		//정렬
		SortOperation sortOperation = Aggregation.sort(Sort.by("userId").ascending());
		operationList.add(sortOperation);
		
		Aggregation aggregation = 
			Aggregation.newAggregation(operationList.toArray(new AggregationOperation[0]));

		log.info("aggregate = {}", aggregation);
		 
		
		List<Object> resultList =  
			mongoTemplate.aggregate(aggregation, "UserMst", Object.class).getMappedResults();
		
		
		System.out.println("resultList.size = " + resultList.size());
		System.out.println("resultList= " + resultList);
	}
	
	
	
}
