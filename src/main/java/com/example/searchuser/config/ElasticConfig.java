package com.example.searchuser.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticConfig {

	@Value("${elasticsearch.host}")
	private String[] domains;

	@Value("${elasticsearch.port}")
	private Integer port;

	@Value("${elasticsearch.protocol}")
	private String protocol;

	@Bean
	public RestHighLevelClient elasticHighClient() {

		HttpHost[] hosts = new HttpHost[domains.length];

		for (int i = 0; i < domains.length; i++) {
			hosts[i] = new HttpHost(domains[i], port, protocol);
		}

		return new RestHighLevelClient(RestClient
				.builder(hosts).setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
						.setConnectTimeout(30000).setConnectionRequestTimeout(0).setSocketTimeout(120000))
				.setMaxRetryTimeoutMillis(120000));
	}
}
