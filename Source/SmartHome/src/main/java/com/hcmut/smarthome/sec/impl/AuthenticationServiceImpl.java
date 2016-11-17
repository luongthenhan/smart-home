package com.hcmut.smarthome.sec.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import com.hcmut.smarthome.entity.HomeEntity;
import com.hcmut.smarthome.model.Home;
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.sec.ITokenManager;
import com.hcmut.smarthome.sec.TokenInfo;
import com.hcmut.smarthome.service.IHomeService;

/**
 * Service responsible for all around authentication, token checks, etc. This
 * class does not care about HTTP protocol at all.
 */
public class AuthenticationServiceImpl implements IAuthenticationService {

	private static Logger LOGGER = Logger
			.getLogger(AuthenticationServiceImpl.class);

	private static final int USER_CANNOT_BE_FOUND = -1;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private IHomeService homeService;

	private final AuthenticationManager authenticationManager;
	private final ITokenManager tokenManager;

	public AuthenticationServiceImpl(
			AuthenticationManager authenticationManager,
			ITokenManager tokenManager) {
		this.authenticationManager = authenticationManager;
		this.tokenManager = tokenManager;
	}

	@PostConstruct
	public void init() {
		LOGGER.debug(" *** AuthenticationServiceImpl.init with: "
				+ applicationContext);
	}

	@Override
	public TokenInfo authenticate(String login, String password) {
		LOGGER.debug(" *** AuthenticationServiceImpl.authenticate");

		// Here principal=username, credentials=password
		Authentication authentication = new UsernamePasswordAuthenticationToken(
				login, password);
		try {
			authentication = authenticationManager.authenticate(authentication);
			// Here principal=UserDetails (UserContext in our case),
			// credentials=null (security reasons)
			SecurityContextHolder.getContext()
					.setAuthentication(authentication);

			if (authentication.getPrincipal() != null) {
				UserDetails userContext = (UserDetails) authentication
						.getPrincipal();
				TokenInfo newToken = tokenManager.createNewToken(userContext);
				if (newToken == null) {
					return null;
				}
				return newToken;
			}
		} catch (AuthenticationException e) {
			LOGGER.debug(" *** AuthenticationServiceImpl.authenticate - FAILED: "
					+ e.toString());
		}
		return null;
	}

	@Override
	public boolean checkToken(String token) {
		LOGGER.debug(" *** AuthenticationServiceImpl.checkToken");

		UserDetails userDetails = tokenManager.getUserDetails(token);
		if (userDetails == null) {
			return false;
		}

		Authentication securityToken = new PreAuthenticatedAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());
		SecurityContextHolder.getContext().setAuthentication(securityToken);

		return true;
	}

	@Override
	public void logout(String token) {
		UserDetails logoutUser = tokenManager.removeToken(token);
		LOGGER.debug(" *** AuthenticationServiceImpl.logout: " + logoutUser);
		SecurityContextHolder.clearContext();
	}

	@Override
	public UserDetails getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext()
				.getAuthentication();
		if (authentication == null) {
			return null;
		}
		return (UserDetails) authentication.getPrincipal();
	}

	@Override
	public boolean isAccessable(int selectedHomeId) {
		UserDetails currentUser = getCurrentUser();

		if (currentUser == null) {
			return false;
		}

		if (currentUser instanceof CustomUserDetails) {
			return isAccessable((CustomUserDetails) currentUser, selectedHomeId);
		}

		return false;
	}

	private boolean isAccessable(CustomUserDetails user, int selectedHomeId) {
		
		Home userHome = homeService.getHome(user.getUserEntity().getId(),
				selectedHomeId);

		if (userHome != null) {
			return true;
		}
		
		return false;
	}

	@Override
	public int getCurrentUserId() {

		CustomUserDetails currentUser = (CustomUserDetails) getCurrentUser();
		if (currentUser == null) {
			return USER_CANNOT_BE_FOUND;
		}

		return currentUser.getUserEntity().getId();
	}
}
