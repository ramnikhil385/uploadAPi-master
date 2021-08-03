/**
 * 
 */
package com.doc360.uploadApi.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import com.doc360.apibridge.utility.ApplicationContextProvider;
import com.doc360.apibridge.utility.IConstants;
import com.doc360.elastic.resource.RestError;
import com.doc360.uploadApi.security.config.ApplicationSecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * This filter will be used to extract JWT token, and authorize the application.
 * 
 * @author Tarun Verma
 *
 */
@Slf4j
public class ApplicationJwtFilter extends OncePerRequestFilter {

	/**
	 * Perform the JWT token validation, and parse the values if present.
	 * 
	 * @see org.springframework.web.filter.OncePerRequestFilter#doFilterInternal(javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		try {
			ApplicationSecurityConfig applicationSecurityConfig = (ApplicationSecurityConfig) ApplicationContextProvider
					.getApplicationContext().getBean(IConstants.BEAN_ID_APPLICATION_SECURITY_CONFIG);
			String jwtToken = request.getHeader(applicationSecurityConfig.getTokenHeaderName());
			if(!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
				// If JWT Header is not present
				if (StringUtils.isBlank(jwtToken)) {
					throw new SecurityException("Unauthorized access. " + applicationSecurityConfig.getTokenHeaderName()
							+ " Header Not Found in HTTP request.");
				}

				// validate the token here, and gather the application claims
				JwtValidator jwtValidator = (JwtValidator) ApplicationContextProvider.getApplicationContext()
						.getBean(IConstants.BEAN_ID_JWT_VALIDATOR);
				ApplicationClaims applicationClaims = jwtValidator.getApplicationClaims(jwtToken);
				log.info("Retrieved application claims from JWT token: {}", applicationClaims);
				request.setAttribute(IConstants.REQUEST_ATTRIBUTE_DOC360_JWT_APPLICATION_CLAIMS, applicationClaims);
			}
		} catch (Exception e) {
			// Catches configuration exceptions
			log.error("Error while validating the JWT token.", e);

			// send the error response as JSON
			// this is needed as controller advice is not used by errors thrown
			// by filter
			RestError error = new RestError(HttpStatus.UNAUTHORIZED, getMessageString(e), request.getRequestURI());
			response.setStatus(error.getStatus().value());
			response.getWriter().write(new ObjectMapper().writeValueAsString(error));
			return;
		}

		// go to next filter
		chain.doFilter(request, response);
	}

	/**
	 * Get the message string from any exception.
	 * 
	 * @param th
	 * @return String
	 */
	private String getMessageString(Throwable th) {
		// get the message string, include nested exception's
		// string as well.
		if (th != null) {
			StringBuilder builder = new StringBuilder(th.toString());
			if (th.getCause() != null) {
				builder.append(" Cause: ");
				builder.append(th.getCause().getMessage());
			}
			return builder.toString();
		} else {
			return IConstants.EMPTY_STRING;
		}
	}

}
