package com.hcmut.smarthome.sec.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.hcmut.smarthome.dao.IUserDao;
import com.hcmut.smarthome.entity.UserEntity;

/**
 * Implements Spring Security {@link UserDetailsService} that is injected into authentication provider in configuration XML.
 * It interacts with domain, loads user details and wraps it into {@link UserContext} which implements Spring Security {@link UserDetails}.
 */
public class CustomUserDetailsService implements UserDetailsService {
	
	private final static Logger LOGGER = Logger.getLogger(CustomUserDetailsService.class);

	@Autowired
	private IUserDao userDao;

	/**
	 * This will be called from
	 * {@link org.springframework.security.authentication.dao.DaoAuthenticationProvider#retrieveUser(java.lang.String, org.springframework.security.authentication.UsernamePasswordAuthenticationToken)}
	 * when {@link AuthenticationService#authenticate(java.lang.String, java.lang.String)} calls
	 * {@link AuthenticationManager#authenticate(org.springframework.security.core.Authentication)}. Easy.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		LOGGER.debug(" *** CustomUseDetailService.loadUserByUsername");
		UserEntity user = userDao.getByUsername(username);
		if (user == null) {
			throw new UsernameNotFoundException("User " + username + " not found");
		}
		return new CustomUserDetails(user);
	}
}
