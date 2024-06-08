package com.example.searchuser.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.Scroll;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.example.searchuser.model.UserIndex;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class UserElasticRepositoryImpl implements UserElasticRepository {
	
	private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());
	
	private static final String INDEX = "user";
	
	private static final String FIELD_NAME = "name";
	
    public final RestHighLevelClient elasticHighClient;
    
    public final ObjectMapper objectMapper;
    
	
	public UserElasticRepositoryImpl(RestHighLevelClient elasticHighClient, ObjectMapper objectMapper) {
		this.elasticHighClient = elasticHighClient;
		this.objectMapper = objectMapper;
	}
    
	@Override
	public void index(UserIndex user) {
		IndexRequest indexRequest = modelToRequest(user);

		try {
			elasticHighClient.index(indexRequest);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void indexBulk(Collection<UserIndex> users) {
		
		BulkRequest bulkRequest = new BulkRequest();
		
		users.stream().map(this::modelToRequest).filter(Objects::nonNull).forEach(bulkRequest::add);
		
		Thread thread = new Thread(() -> {

			long start = System.currentTimeMillis();
			try {
				BulkResponse result = elasticHighClient.bulk(bulkRequest);
				result.forEach(this::verifyBulk);
			} catch (Exception e) {
				log.error("Ha ocurrido un error al lanzar el bulk de users, motivo: ", e);
			}
			log.info("BULK Tiempo en indexar {} elementos en bulk de {}: {}", INDEX, bulkRequest.requests().size(), (System.currentTimeMillis() - start));
			
		});
		thread.start();
	}
	
	private void verifyBulk(BulkItemResponse res) {
		if (res.getFailure() != null) {
			log.error("Error al indexar en el bulk del indice {} con id: {} motivo: {}", INDEX, res.getIndex(), res.getFailureMessage());
		}
	}
	
	@Override
	public List<String> findUsersIdStartNameWith(String startName) {
		
		QueryBuilder query = QueryBuilders.prefixQuery(FIELD_NAME, startName.toLowerCase());		
		
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().query(query).size(500).fetchSource(false);
		
		SearchRequest searchRequest = new SearchRequest(INDEX).source(searchSourceBuilder);
		
		Scroll scroll = new Scroll(TimeValue.timeValueSeconds(20L));
		searchRequest.scroll(scroll);
		
		String scrollId = null;
		
		List<String> usersId = new ArrayList<>();
		
		try {
			SearchResponse searchResponse = elasticHighClient.search(searchRequest);
			
			scrollId = searchResponse.getScrollId();
			
			SearchHit[] searchHits = searchResponse.getHits().getHits();
			
			while (searchHits != null && searchHits.length > 0) { 
				
				List<String> resultAux = Arrays.stream(searchHits).parallel().map(SearchHit::getId).toList();
				
				usersId.addAll(resultAux);
				
				SearchScrollRequest scrollRequest = new SearchScrollRequest(scrollId); 
				scrollRequest.scroll(scroll);
				searchResponse = elasticHighClient.searchScroll(scrollRequest);
				
				scrollId = searchResponse.getScrollId();
				searchHits = searchResponse.getHits().getHits();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			ClearScrollRequest clearScrollRequest = new ClearScrollRequest(); 
			clearScrollRequest.addScrollId(scrollId);
			try {
				elasticHighClient.clearScroll(clearScrollRequest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return usersId;
	}
	
	private IndexRequest modelToRequest(UserIndex user) {
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
