package com.demo.mongo.support.mongoDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;

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
										                   new Document("input", "$tenant_code")
										                   		.append("regex", "T")
										                   		.append("options","i")	
								                          )
								           , new Document("$eq", Arrays.asList("$company.company_code", "COM_1"))
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
		criteriaList.add(Criteria.where("tenant_code").regex("T", "i"));
		criteriaList.add(Criteria.where("company.company_code").is("COM_1"));
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
	
	/** mongodb aggregation 실행 : 실행 후 aggregatePropList는 초기화 함.
	 * @param entityClass : 리턴받을 dto
	 * @param colectionName : 기준 table명
	 * @return 조인 및 집계 결과 리스트.
	 */
	public <T> List<T> aggregate(String collectionName, Class<T> entityClass) {
		
		List<AggregationOperation> operations = new ArrayList<>();
		ProjectionOperation projectionOperation = null;

	    // ProjectionOperation 맨 뒤로 이동
	    for (AggregationOperation operation : operationList) {
	        if (operation instanceof ProjectionOperation) projectionOperation = (ProjectionOperation)operation;
	        else operations.add(operation);
	    }
	    operations.add(projectionOperation);

	    Aggregation aggregation = Aggregation.newAggregation(operations.toArray(new AggregationOperation[0]));

	    log.info("aggregation ====> {}", aggregation);
	    
	    return mongoTemplate.aggregate(aggregation, collectionName, entityClass).getMappedResults();
	}
	
	
	public <T> PageImpl<T> aggregate(Pageable pageable, String collectionName, Class<T> entityClass) {

	    List<T> result = aggregate(collectionName, entityClass);
	    final int start = (int) pageable.getOffset();
	    final int end = Math.min((start + pageable.getPageSize()), result.size());
	    return new PageImpl<>(result.subList(start, end), pageable, result.size());
	}
	
}
