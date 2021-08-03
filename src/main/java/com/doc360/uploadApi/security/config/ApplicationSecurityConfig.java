/**
 * 
 */
package com.doc360.uploadApi.security.config;

import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotBlank;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.doc360.uploadApi.security.ApplicationJwtFilter;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration class for the application security.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "security.jwt")
@NoArgsConstructor
@Getter
@Setter
public class ApplicationSecurityConfig {

	@NotBlank
	private String securityCred;

	@NotBlank
	private long tokenExpirationTime;

	@NotBlank
	private String tokenIssuerName;

	@NotBlank
	private String tokenHeaderName;

	@NotBlank
	private String filterUrlPatterns;

	@NotBlank
	private boolean filterAsyncSupported;

	@NotBlank
	private Integer filterLoadOrder;

	/**
	 * Set the JWT filter bean.
	 * 
	 * @return FilterRegistrationBean.
	 * 
	 * @throws IOException
	 */
	@Bean
	@ConditionalOnMissingBean(ApplicationJwtFilter.class)
	public FilterRegistrationBean<ApplicationJwtFilter> applicationJwtFilter() throws IOException {
		// setup the filter using JWT token validator
		FilterRegistrationBean<ApplicationJwtFilter> filterRegistrationBean = new FilterRegistrationBean<>(
				new ApplicationJwtFilter());
		// valid for these uris
		filterRegistrationBean.setUrlPatterns(Stream.of(filterUrlPatterns).collect(Collectors.toList()));
		// load at this order
		filterRegistrationBean.setOrder(filterLoadOrder);
		filterRegistrationBean.setAsyncSupported(filterAsyncSupported);
		log.info("Set up the JWT token validator filter. Uri: {}, Order: {}, Async: {}", filterUrlPatterns,
				filterLoadOrder, filterAsyncSupported);
		return filterRegistrationBean;
	}

}
