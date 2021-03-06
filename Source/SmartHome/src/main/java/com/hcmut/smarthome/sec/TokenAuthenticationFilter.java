package com.hcmut.smarthome.sec;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.StringTokenizer;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.filter.GenericFilterBean;

import static com.hcmut.smarthome.utils.ConstantUtil.*;

public final class TokenAuthenticationFilter extends GenericFilterBean {

	private static Logger LOGGER = Logger
			.getLogger(TokenAuthenticationFilter.class);

	private static final String HEADER_TOKEN = "X-Auth-Token";
	private static final String HEADER_USERNAME = "X-Username";
	private static final String HEADER_PASSWORD = "X-Password";

	/**
	 * Request attribute that indicates that this filter will not continue with
	 * the chain. Handy after login/logout, etc.
	 */
	private static final String REQUEST_ATTR_DO_NOT_CONTINUE = "MyAuthenticationFilter-doNotContinue";
	private static final String LOGOUT = "logout";

	private final IAuthenticationService authenticationService;

	public TokenAuthenticationFilter(
			IAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		LOGGER.debug(" *** MyAuthenticationFilter.doFilter");
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		// if this is no login request, don't check login or token
		if (isNoLoginRequest(httpRequest)) {
			chain.doFilter(request, response);
			
		} else {
			boolean authenticated = checkToken(httpRequest, httpResponse);
			if (authenticated) {
				checkLogout(httpRequest);
			} else {
				checkLogin(httpRequest, httpResponse);
			}

			if (canRequestProcessingContinue(httpRequest)) {
				chain.doFilter(request, response);
			}
		}
		LOGGER.debug(" === AUTHENTICATION: "
				+ SecurityContextHolder.getContext().getAuthentication());
	}

	private void checkLogin(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		String authorization = httpRequest.getHeader("Authorization");
		String username = httpRequest.getHeader(HEADER_USERNAME);
		String password = httpRequest.getHeader(HEADER_PASSWORD);
		
		

		if (authorization != null) {
			checkBasicAuthorization(authorization, httpResponse);
			doNotContinueWithRequestProcessing(httpRequest);
		} else if (username != null && password != null) {
			checkUsernameAndPassword(username, password, httpResponse);
			doNotContinueWithRequestProcessing(httpRequest);
		}

	}

	private void checkBasicAuthorization(String authorization,
			HttpServletResponse httpResponse) throws IOException {
		StringTokenizer tokenizer = new StringTokenizer(authorization);
		if (tokenizer.countTokens() < 2) {
			return;
		}
		if (!tokenizer.nextToken().equalsIgnoreCase("Basic")) {
			return;
		}

		String base64 = tokenizer.nextToken();
		String loginPassword = new String(Base64.decode(base64
				.getBytes(StandardCharsets.UTF_8)));

		LOGGER.debug("loginPassword = " + loginPassword);
		tokenizer = new StringTokenizer(loginPassword, ":");
		LOGGER.debug("tokenizer = " + tokenizer);
		checkUsernameAndPassword(tokenizer.nextToken(), tokenizer.nextToken(),
				httpResponse);
	}

	private void checkUsernameAndPassword(String username, String password,
			HttpServletResponse httpResponse) throws IOException {
		
		// Encrypt password
		password = DigestUtils.sha1Hex(password);
				
		TokenInfo tokenInfo = authenticationService.authenticate(username,
				password);
		if (tokenInfo != null) {
			httpResponse.setHeader(HEADER_TOKEN, tokenInfo.getToken());
		} else {
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
		}
	}

	/** Returns true, if request contains valid authentication token. */
	private boolean checkToken(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse) throws IOException {
		String token = httpRequest.getHeader(HEADER_TOKEN);
		LOGGER.debug("=========Token: " + token);
		if (token == null) {
			return false;
		}

		if (authenticationService.checkToken(token)) {
			LOGGER.debug(" *** "
					+ HEADER_TOKEN
					+ " valid for: "
					+ SecurityContextHolder.getContext().getAuthentication()
							.getPrincipal());
			return true;
		} else {
			LOGGER.debug(" *** Invalid " + HEADER_TOKEN + ' ' + token);
			httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
			doNotContinueWithRequestProcessing(httpRequest);
		}
		return false;
	}

	private void checkLogout(HttpServletRequest httpRequest) {
		if (getCurrentLink(httpRequest).contains(LOGOUT)) {
			String token = httpRequest.getHeader(HEADER_TOKEN);
			// we go here only authenticated, token must not be null
			authenticationService.logout(token);
			doNotContinueWithRequestProcessing(httpRequest);
		}
	}

	// or use Springs util instead: new
	// UrlPathHelper().getPathWithinApplication(httpRequest)
	// shame on Servlet API for not providing this without any hassle :-(
	private String getCurrentLink(HttpServletRequest httpRequest) {
		if (httpRequest.getPathInfo() == null) {
			return httpRequest.getServletPath();
		}
		return httpRequest.getServletPath() + httpRequest.getPathInfo();
	}

	/**
	 * This is set in cases when we don't want to continue down the filter
	 * chain. This occurs for any {@link HttpServletResponse#SC_UNAUTHORIZED}
	 * and also for login or logout.
	 */
	private void doNotContinueWithRequestProcessing(
			HttpServletRequest httpRequest) {
		httpRequest.setAttribute(REQUEST_ATTR_DO_NOT_CONTINUE, "");
	}

	private boolean canRequestProcessingContinue(HttpServletRequest httpRequest) {
		return httpRequest.getAttribute(REQUEST_ATTR_DO_NOT_CONTINUE) == null;
	}

	private boolean isNoLoginRequest(HttpServletRequest httpRequest) {

		String currentLink = getCurrentLink(httpRequest);
		for (String noLoginRequest : NO_LOGIN_REQUESTS) {
			if (currentLink.contains(noLoginRequest)) {
				return true;
			}
		}

		return false;
	}
}
