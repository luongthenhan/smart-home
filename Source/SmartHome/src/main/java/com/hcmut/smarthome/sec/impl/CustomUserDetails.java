package com.hcmut.smarthome.sec.impl;

import java.util.Collection;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.hcmut.smarthome.entity.UserEntity;

/** This object wraps {@link User} and makes it {@link UserDetails} so that Spring Security can use it. */
public class CustomUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	
	private UserEntity userEntity;

	public CustomUserDetails(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return null;
	}

	@Override
	public String getPassword() {
		return userEntity.getPassword();
	}

	@Override
	public String getUsername() {
		return userEntity.getUsrName();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object object) {
		return this == object
			|| object != null && object instanceof CustomUserDetails
			&& Objects.equals(userEntity, ((CustomUserDetails) object).userEntity);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(userEntity);
	}

	@Override
	public String toString() {
		return "UserContext{" +
			"user=" + userEntity +
			'}';
	}

	public UserEntity getUserEntity() {
		return userEntity;
	}
	
	public void setUserEntity(UserEntity userEntity) {
		this.userEntity = userEntity;
	}
	
}

