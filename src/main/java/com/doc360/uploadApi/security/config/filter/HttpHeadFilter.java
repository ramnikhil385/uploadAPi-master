package com.doc360.uploadApi.security.config.filter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.doc360.apibridge.utility.IConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * Servlet filter that presents a HEAD request as a GET. The application doesn't
 * need to know the difference, as this filter handles all the details.
 */
@WebFilter(value = "/*", asyncSupported = true)
@Slf4j
public class HttpHeadFilter implements Filter, IConstants {

	private static final Logger logger = LoggerFactory.getLogger(HttpHeadFilter.class);

	/**
	 * Initialize the filter.
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// Do nothing
	}

	/**
	 * Check for HEAD type request, and convert those to GET type.
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpServletRequest = (HttpServletRequest) request;

		// check for HEAD type
		if (isHttpHead(httpServletRequest)) {
			// Current Solution: Skip HEAD request for now.
			logger.debug("HEAD request. skipping...");

			// Option #1: Change HEAD calls to GET
			// chain.doFilter(new ForceGetRequestWrapper(httpServletRequest),
			// response);

			// Option #2: Using NoBodyResponseWrapper to avoid container
			// dependency
			// HttpServletResponse httpServletResponse = (HttpServletResponse)
			// response;
			// NoBodyResponseWrapper noBodyResponseWrapper = new
			// NoBodyResponseWrapper(httpServletResponse);
			//
			// chain.doFilter(new ForceGetRequestWrapper(httpServletRequest),
			// noBodyResponseWrapper);
			// noBodyResponseWrapper.setContentLength();
		} else {
			// set the IP address in MDC for logging purpose
			MDC.put(MDC_USER_IP_ADDRESS, httpServletRequest.getRemoteAddr());
			String applicationIdentifier = httpServletRequest.getHeader(HEADER_APPLICATION_IDENTIFIER);
			MDC.put(MDC_APPLICATION_IDENTIFIER, applicationIdentifier != null ? applicationIdentifier : EMPTY_STRING);
			chain.doFilter(request, response);
		}
	}

	@Override
	public void destroy() {
		// Do nothing
	}

	/**
	 * Checks whether the HTTP method of this request is HEAD.
	 *
	 * @param request
	 *            The request to check.
	 * @return {@code true} if it is HEAD, {@code false} if it isn't.
	 */
	private boolean isHttpHead(HttpServletRequest request) {
		return REQUEST_TYPE_HEAD.equals(request.getMethod());
	}

	/**
	 * Request wrapper that lies about the Http method and always returns GET.
	 */
	private class ForceGetRequestWrapper extends HttpServletRequestWrapper {
		/**
		 * Initializes the wrapper with this request.
		 *
		 * @param request
		 *            The request to initialize the wrapper with.
		 */
		public ForceGetRequestWrapper(HttpServletRequest request) {
			super(request);
		}

		/**
		 * Lies about the HTTP method. Always returns GET.
		 *
		 * @return Always returns GET.
		 */
		@Override
		public String getMethod() {
			return REQUEST_TYPE_GET;
		}
	}

	/**
	 * Response wrapper that swallows the response body, leaving only the
	 * headers.
	 */
	private class NoBodyResponseWrapper extends HttpServletResponseWrapper {
		/**
		 * Outputstream that discards the data written to it.
		 */
		private final NoBodyOutputStream noBodyOutputStream = new NoBodyOutputStream();

		private PrintWriter writer;

		/**
		 * Constructs a response adaptor wrapping the given response.
		 *
		 * @param response
		 *            The response to wrap.
		 */
		public NoBodyResponseWrapper(HttpServletResponse response) {
			super(response);
		}

		/**
		 * Get the output stream.
		 * 
		 * @see javax.servlet.ServletResponseWrapper#getOutputStream()
		 */
		@Override
		public ServletOutputStream getOutputStream() throws IOException {
			return noBodyOutputStream;
		}

		/**
		 * Get the PrintWriter for this stream.
		 * 
		 * @see javax.servlet.ServletResponseWrapper#getWriter()
		 */
		@Override
		public PrintWriter getWriter() throws UnsupportedEncodingException {
			if (writer == null) {
				writer = new PrintWriter(new OutputStreamWriter(noBodyOutputStream, getCharacterEncoding()));
			}

			return writer;
		}

		/**
		 * Sets the content length, based on what has been written to the
		 * outputstream so far.
		 */
		void setContentLength() {
			super.setContentLength(noBodyOutputStream.getContentLength());
		}
	}

	/**
	 * Outputstream that only counts the length of what is being written to it
	 * while discarding the actual data.
	 */
	private class NoBodyOutputStream extends ServletOutputStream {
		/**
		 * The number of bytes written to this stream so far.
		 */
		private int contentLength = 0;

		/**
		 * @return The number of bytes written to this stream so far.
		 */
		int getContentLength() {
			return contentLength;
		}

		/**
		 * Write the byte
		 * 
		 * @see java.io.OutputStream#write(int)
		 */
		@Override
		public void write(int b) {
			contentLength++;
		}

		/**
		 * Write specific set of bytes
		 * 
		 * @see java.io.OutputStream#write(byte[], int, int)
		 */
		@Override
		public void write(byte buf[], int offset, int len) throws IOException {
			contentLength += len;
		}

		/**
		 * Check whether stream is ready or not.
		 * 
		 * @see javax.servlet.ServletOutputStream#isReady()
		 */
		@Override
		public boolean isReady() {
			return false;
		}

		/**
		 * Setup any write listener.
		 * 
		 * @see javax.servlet.ServletOutputStream#setWriteListener(javax.servlet.WriteListener)
		 */
		@Override
		public void setWriteListener(WriteListener writeListener) {
		}
	}
}
