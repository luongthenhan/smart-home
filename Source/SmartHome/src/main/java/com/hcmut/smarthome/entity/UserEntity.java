package com.hcmut.smarthome.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="account_user")
public class UserEntity implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="user_name", nullable = false , length = 45)
	private String usrName;
	
	@Column(name="name", nullable = false , length = 45)
	private String name;
	
	@Column(name="password", nullable = false , length = 45)
	private String password;
	
	@Column(name="description", nullable = true , length = 512)
	private String description;
	
	@Column(name="is_active", nullable = false)
	private boolean isActive;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true,mappedBy="user", fetch = FetchType.EAGER)
	private List<HomeEntity> homes;
	
	public UserEntity() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUsrName() {
		return usrName;
	}
	public void setUsrName(String usrName) {
		this.usrName = usrName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<HomeEntity> getHomes() {
		return homes;
	}
	public void setHomes(List<HomeEntity> homes) {
		this.homes = homes;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((usrName == null) ? 0 : usrName.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		UserEntity other = (UserEntity) obj;
		if (id != other.id)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (usrName == null) {
			if (other.usrName != null)
				return false;
		} else if (!usrName.equals(other.usrName))
			return false;
		return true;
	}
	
	
}
