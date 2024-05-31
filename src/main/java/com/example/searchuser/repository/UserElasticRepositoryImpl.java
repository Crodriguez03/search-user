package com.example.searchuser.repository;

import java.io.IOException;

import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.example.searchuser.dto.User;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class UserElasticRepositoryImpl implements UserElasticRepository {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	private static final String INDEX = "user";
	
    public final RestHighLevelClient elasticHighClient;
    
    public final ObjectMapper objectMapper;
    
	
	public UserElasticRepositoryImpl(RestHighLevelClient elasticHighClient, ObjectMapper objectMapper) {
		this.elasticHighClient = elasticHighClient;
		this.objectMapper = new ObjectMapper();
	}
    
	@Override
	public User index(User user) {
		IndexRequest indexRequest = modelToRequest(user);

		try {
			elasticHighClient.index(indexRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return user;
	}

	@Override
	public User findById(String id) {

		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(QueryBuilders.termQuery("_id", id)).size(1);
		
		SearchResponse searchResponse = searchResponse(searchSourceBuilder);
		if (searchResponse != null && searchResponse.getHits() != null && searchResponse.getHits().getHits() != null && searchResponse.getHits().getHits().length > 0) {
			return hitToModel(searchResponse.getHits().getHits()[0]);
		}	
		return null;

	}
	
	protected SearchResponse searchResponse(SearchSourceBuilder searchSourceBuilder) {
		SearchRequest searchRequest = new SearchRequest(INDEX).source(searchSourceBuilder);
		
		SearchResponse searchResponse;
		try {
			return elasticHighClient.search(searchRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	private User hitToModel(SearchHit hit) {
		try {
			return objectMapper.readValue(hit.getSourceAsString(), User.class);
		} catch (IOException e) {
			log.error("Error al mapear de SearchHit a user. Motivo: ", e);
		}

		return null;
	}
	
	private IndexRequest modelToRequest(User user) {
		IndexRequest indexRequest = null;
		
		if (user != null) {
			try {
				indexRequest = new IndexRequest(INDEX, INDEX, user.getId());
				indexRequest.source(objectMapper.writeValueAsString(user), XContentType.JSON);
				
			} catch (Exception e) {
				return null;
			}
		}
		return indexRequest;
	}
}
