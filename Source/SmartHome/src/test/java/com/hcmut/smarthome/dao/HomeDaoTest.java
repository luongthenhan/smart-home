package com.hcmut.smarthome.dao;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration( value = "classpath:ApplicationContext.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class HomeDaoTest {
	
	@Autowired
	private IHomeDao homeDao;

	@Test
	@Transactional
	public void should_return_home_is_enabled() throws Exception {
		
		assertThat(homeDao.isEnabled(1), is(true));
	}
	
}
