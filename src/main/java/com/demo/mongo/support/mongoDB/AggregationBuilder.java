package com.demo.mongo.support.mongoDB;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.Document;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation.AddFieldsOperationBuilder;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AggregationBuilder {

	private MongoTemplate mongoTemplate;
	private List<AggregationOperation> operationList;

	public AggregationBuilder(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
		operationList = new ArrayList<>();
	}
	
	public AggregationBuilder join(boolean unwind , String from, DocumentBuilder letBuilder, DocumentBuilder onBuilder) {
		Document let     = letBuilder.getDocument();
		Document letByOn = onBuilder.getDocument();
		letByOn.forEach(let::append);
		List<Document> match = onBuilder.getDocumentList();

		lookup(from, let, match);
		unwind(from, unwind);//unwind :true => left Join  / false => inner Join
		
		return this;
	}
	
//	public AggregationBuilder addField() {
//		AddFieldsOperationBuilder af= Aggregation.addFields();
//		operationList.add(af);
//		return this;
//;	}

	public AggregationBuilder group(GroupOperation groupOperation) {
		
		this.operationList.add(groupOperation);
		return this;
	}

	public void lookup(String from, Document let, List<Document> match) {
		AggregationOperation lookupOperation = makeLookupOperation(from, let, match);
		this.operationList.add(lookupOperation);
	}

	
	
	public AggregationBuilder unwind(String field, boolean preserveNullAndEmptyArrays) {
		UnwindOperation unwindOperation = Aggregation.unwind(field, preserveNullAndEmptyArrays);
		this.operationList.add(unwindOperation);
		return this;
	}

	public AggregationOperation makeLookupOperation(String from, Document let, List<Document> match) {
		return context -> new Document("$lookup",
				new Document("from", from).append("let", let)
						.append("pipeline",
								Arrays.<Object>asList(
										new Document("$match", new Document("$expr", new Document("$and", match)))))
						.append("as", from));
	}

	

	public AggregationBuilder project(ProjectionOperation projectionOperation) {
		this.operationList.add(projectionOperation);
		return this;
	}

	public AggregationBuilder match(List<Criteria> criteriaList) {
		MatchOperation matchOperation = Aggregation
				.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
		operationList.add(matchOperation);
		return this;
	}

	public AggregationBuilder sort(SortOperation sortOperation) {
		operationList.add(sortOperation);
		return this;
	}

	
	/** mongodb aggregation 실행 
	 * @param entityClass : 리턴받을 dto
	 * @param colectionName : 기준 table명
	 * @return 조인 및 집계 결과 리스트.
	 */
	public <T> List<T> aggregate(String collectionName, Class<T> entityClass) {

		List<AggregationOperation> operations = new ArrayList<>();

		for (AggregationOperation operation : operationList) {
			operations.add(operation);
		}

		Aggregation aggregation = Aggregation.newAggregation(operations.toArray(new AggregationOperation[0]));

		log.info("AggregationBuilder > aggregate = {}", aggregation);
		return mongoTemplate.aggregate(aggregation, collectionName, entityClass).getMappedResults();
	}

	public <T> PageImpl<T> aggregate(Pageable pageable, String collectionName, Class<T> entityClass) {

		List<T>  result = aggregate(collectionName, entityClass);
		final int start = (int) pageable.getOffset();
		final int   end = Math.min((start + pageable.getPageSize()), result.size());
		
		return new PageImpl<>(result.subList(start, end), pageable, result.size());
	}
}
