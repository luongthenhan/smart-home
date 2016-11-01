package com.hcmut.smarthome.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@ContextConfiguration( locations = { "classpath:ApplicationContext.xml", "classpath:spring-security.xml" } )
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class UserDaoTest {
	
	@Autowired
	private IUserDao userDao;
	
	@Test
	public void should_activate_user() throws Exception {
		
		/*UserEntity userEntity = new UserEntity("nttung", "123456", "Nguyen Thanh Tung", "tung.cs@gmail.com");
		int id = userDao.addUser(userEntity);
		boolean result = userDao.activateUser(id);
		UserEntity activatedUser = userDao.getById(id);
		System.out.println("username " + activatedUser.getUsrName() );
		System.out.println("Activated: " + activatedUser.isActivated());
		assertThat(activatedUser.isActivated(), CoreMatchers.is(true));*/
		
	}
	
}
