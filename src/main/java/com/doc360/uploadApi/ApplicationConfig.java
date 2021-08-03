package com.doc360.uploadApi;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.net.ssl.SSLContext;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.doc360.apibridge.utility.IConstants;

@Configuration
public class ApplicationConfig {

	@Value("${rio.elastic.search.username}")
	private String rioESUserName;

	@Value("${rio.elastic.search.pwd}")
	private String rioESPwd;

	@Value("${rio.elastic.search.host}")
	private String rioESHost;

	@Value("${rio.elastic.search.port}")
	private String rioESPort;

	@Value("${rio.elastic.search.protocol}")
	private String rioESProtocol;

	@Value("#{new Integer('${rio.elastic.search.max.retry.timeout}')}")
	private int rioMaxRetryTimeout;

	@Value("#{new Integer('${rio.elastic.search.connect.timeout}')}")
	private int rioConnectTimeout;

	@Value("#{new Integer('${rio.elastic.search.socket.timeout}')}")
	private int rioSocketTimeout;

	@Value("#{new Integer('${rio.elastic.search.connection.request.timeout}')}")
	private int rioConnectionRequestTimeout;

	@Value("${rio.alt.two.elastic.search.host}")
	private String rioAltTwoESHost;

	@Value("${rio.alt.two.elastic.search.port}")
	private String rioAltTwoESPort;

	@Value("${rio.alt.two.elastic.search.protocol}")
	private String rioAltTwoESProtocol;

	@Value("#{new Integer('${rio.alt.two.elastic.search.max.retry.timeout}')}")
	private int rioAltTwoMaxRetryTimeout;

	@Value("#{new Integer('${rio.alt.two.elastic.search.connect.timeout}')}")
	private int rioAltTwoConnectTimeout;

	@Value("#{new Integer('${rio.alt.two.elastic.search.socket.timeout}')}")
	private int rioAltTwoSocketTimeout;

	@Value("#{new Integer('${rio.alt.two.elastic.search.connection.request.timeout}')}")
	private int rioAltTwoConnectionRequestTimeout;

	@Value("${rio.elastic.elr.search.host:unknown}")
	private String rioElrESHost;
	
	@Value("${rio.elastic.elr2.search.host:unknown}")
	private String rioElr2ESHost;
	
	private SSLContext sslContext;
	private RestClient rioRestClient;
	private RestClient rioAltTwoRestClient;
	private RestHighLevelClient rioRestHighLevelClient;
	private RestHighLevelClient rioAltTwoRestHighLevelClient;
	private RestHighLevelClient rioRestElrHighLevelClient;
	private RestHighLevelClient rioRestElr2HighLevelClient;

	/**
	 * Initialize the components and beans once application is started.
	 */
	@PostConstruct
	public void init() {
		buildRioRestClient();
		buildRioAltTwoRestClient();
		buildRioElrRestClient();
		buildRioElr2RestClient();
	}

	/**
	 * Before application is destroyed, release the rest client resources.
	 * 
	 * @throws IOException
	 */
	@PreDestroy
	public void destroy() throws IOException {
		this.rioRestHighLevelClient.close();
		this.rioAltTwoRestHighLevelClient.close();
	}

	/**
	 * Prepare the Farm Rest client using configuration details.
	 * 
	 */

	/**
	 * Build the Rio rest client based on configuration details.
	 */
	private void buildRioRestClient() {

		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(rioESUserName, rioESPwd));

		// prepare the rest client for Rio
		RestClientBuilder builder = RestClient
				.builder(new HttpHost(rioESHost, Integer.valueOf(rioESPort), rioESProtocol))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(rioConnectTimeout)
								.setSocketTimeout(rioSocketTimeout)
								.setConnectionRequestTimeout(rioConnectionRequestTimeout);
					}
				}).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (IConstants.HTTPS_PROTOCOL.equals(rioESProtocol)) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setMaxRetryTimeoutMillis(rioMaxRetryTimeout);
		this.rioRestHighLevelClient = new RestHighLevelClient(builder);
		this.rioRestClient = this.rioRestHighLevelClient.getLowLevelClient();
	}


	/**
	 * Provide the Rio Alt Two Rest client bean.
	 * 
	 * @return RestClient
	 */
	@Bean
	public RestClient rioAltTwoRestClient() {
		return this.rioAltTwoRestClient;
	}

	/**
	 * Provide the Rio Rest client bean.
	 * 
	 * @return RestClient
	 */
	@Bean
	public RestClient rioRestClient() {
		return this.rioRestClient;
	}

	/**
	 * Provide the Rio Rest high level client bean.
	 * 
	 * @return RestHighLevelClient
	 */
	@Bean
	public RestHighLevelClient rioRestHighLevelClient() {
		return this.rioRestHighLevelClient;
	}

	/**
	 * Provide the Rio Alt Two Rest high level client bean.
	 * 
	 * @return RestHighLevelClient
	 */
	@Bean
	public RestHighLevelClient rioAltTwoRestHighLevelClient() {
		return this.rioAltTwoRestHighLevelClient;
	}


	@Bean
	public RestHighLevelClient rioRestElrHighLevelClient() {
		return this.rioRestElrHighLevelClient;
	}
	
	@Bean
	public RestHighLevelClient rioRestElr2HighLevelClient() {
		return this.rioRestElr2HighLevelClient;
	}
	/**
	 * Provide the SSLContext for this application.
	 * 
	 * @return SSLContext
	 */
	@Bean
	public SSLContext sslContext() {
		return this.sslContext;
	}

	/**
	 * Build the Rio Alt Two rest client based on configuration details.
	 */
	private void buildRioAltTwoRestClient() {

		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(rioESUserName, rioESPwd));

		// prepare the rest client for Rio
		RestClientBuilder builder = RestClient
				.builder(new HttpHost(rioAltTwoESHost, Integer.valueOf(rioAltTwoESPort), rioAltTwoESProtocol))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(rioAltTwoConnectTimeout)
								.setSocketTimeout(rioAltTwoSocketTimeout)
								.setConnectionRequestTimeout(rioAltTwoConnectionRequestTimeout);
					}
				}).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (IConstants.HTTPS_PROTOCOL.equals(rioAltTwoESProtocol)) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setMaxRetryTimeoutMillis(rioAltTwoMaxRetryTimeout);
		this.rioAltTwoRestHighLevelClient = new RestHighLevelClient(builder);
		this.rioAltTwoRestClient = this.rioAltTwoRestHighLevelClient.getLowLevelClient();
		// this.rioAltTwoRestClient = builder.build();
		// this.rioAltTwoRestHighLevelClient = new
		// RestHighLevelClient(this.rioAltTwoRestClient);
	}
	private void buildRioElrRestClient() {

		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(rioESUserName, rioESPwd));

		// prepare the rest client for Rio
		RestClientBuilder builder = RestClient
				.builder(new HttpHost(rioElrESHost, Integer.valueOf(rioAltTwoESPort), rioAltTwoESProtocol))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(rioAltTwoConnectTimeout)
								.setSocketTimeout(rioAltTwoSocketTimeout)
								.setConnectionRequestTimeout(rioAltTwoConnectionRequestTimeout);
					}
				}).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (IConstants.HTTPS_PROTOCOL.equals(rioAltTwoESProtocol)) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setMaxRetryTimeoutMillis(rioAltTwoMaxRetryTimeout);
		this.rioRestElrHighLevelClient = new RestHighLevelClient(builder);
	}
	private void buildRioElr2RestClient() {

		// SET AUTHENTICATION
		final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
		credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(rioESUserName, rioESPwd));

		// prepare the rest client for Rio
		RestClientBuilder builder = RestClient
				.builder(new HttpHost(rioElr2ESHost, Integer.valueOf(rioAltTwoESPort), rioAltTwoESProtocol))
				.setRequestConfigCallback(new RestClientBuilder.RequestConfigCallback() {

					@Override
					public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
						return requestConfigBuilder.setConnectTimeout(rioAltTwoConnectTimeout)
								.setSocketTimeout(rioAltTwoSocketTimeout)
								.setConnectionRequestTimeout(rioAltTwoConnectionRequestTimeout);
					}
				}).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {

					@Override
					public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
						if (IConstants.HTTPS_PROTOCOL.equals(rioAltTwoESProtocol)) {
							return httpClientBuilder.setSSLContext(sslContext)
									.setDefaultCredentialsProvider(credentialsProvider);
						} else {
							return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
						}
					}
				}).setMaxRetryTimeoutMillis(rioAltTwoMaxRetryTimeout);
		this.rioRestElr2HighLevelClient = new RestHighLevelClient(builder);
	}
}
