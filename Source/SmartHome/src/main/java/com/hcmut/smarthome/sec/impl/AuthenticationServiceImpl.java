package com.hcmut.smarthome.sec.impl;

import java.util.List;

import javax.annotation.PostConstruct;

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
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.sec.ITokenManager;
import com.hcmut.smarthome.sec.TokenInfo;

/**
 * Service responsible for all around authentication, token checks, etc.
 * This class does not care about HTTP protocol at all.
 */
public class AuthenticationServiceImpl implements IAuthenticationService {

	@Autowired
	private ApplicationContext applicationContext;

	private final AuthenticationManager authenticationManager;
	private final ITokenManager tokenManager;

	public AuthenticationServiceImpl(AuthenticationManager authenticationManager, ITokenManager tokenManager) {
		this.authenticationManager = authenticationManager;
		this.tokenManager = tokenManager;
	}

	@PostConstruct
	public void init() {
		System.out.println(" *** AuthenticationServiceImpl.init with: " + applicationContext);
	}

	@Override
	public TokenInfo authenticate(String login, String password) {
		System.out.println(" *** AuthenticationServiceImpl.authenticate");

		// Here principal=username, credentials=password
		Authentication authentication = new UsernamePasswordAuthenticationToken(login, password);
		try {
			authentication = authenticationManager.authenticate(authentication);
			// Here principal=UserDetails (UserContext in our case), credentials=null (security reasons)
			SecurityContextHolder.getContext().setAuthentication(authentication);

			if (authentication.getPrincipal() != null) {
				UserDetails userContext = (UserDetails) authentication.getPrincipal();
				TokenInfo newToken = tokenManager.createNewToken(userContext);
				if (newToken == null) {
					return null;
				}
				return newToken;
			}
		} catch (AuthenticationException e) {
			System.out.println(" *** AuthenticationServiceImpl.authenticate - FAILED: " + e.toString());
		}
		return null;
	}

	@Override
	public boolean checkToken(String token) {
		System.out.println(" *** AuthenticationServiceImpl.checkToken");

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
		System.out.println(" *** AuthenticationServiceImpl.logout: " + logoutUser);
		SecurityContextHolder.clearContext();
	}

	@Override
	public UserDetails currentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null) {
			return null;
		}
		return (UserDetails) authentication.getPrincipal();
	}

	@Override
	public boolean isAccessable(int selectedHomeId) {
		UserDetails currentUser = currentUser();
		
		if(currentUser == null) {
			return false;
		}
		
		if(currentUser instanceof CustomUserDetails) {
			return isAccessable((CustomUserDetails) currentUser, selectedHomeId);
		}
		
		return false;
	}
	
	private boolean isAccessable(CustomUserDetails user, int selectedHomeId) {
		
		List<HomeEntity> userHomes = user.getUserEntity().getHomes();
		if(userHomes == null || userHomes.isEmpty()) {
			return false;
		}
		
		for(HomeEntity home : userHomes) {
			if(selectedHomeId == home.getId()) {
				return true;
			}
		}
		
		return false;
	}
}
