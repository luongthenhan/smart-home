package com.hcmut.smarthome.service.test;

import static com.hcmut.smarthome.utils.ConstantUtil.VALID_USER_ID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.hcmut.smarthome.model.Home;
import com.hcmut.smarthome.service.IHomeService;

@RunWith( SpringJUnit4ClassRunner.class )
@ContextConfiguration( locations = { "classpath:ApplicationContext.xml", "classpath:spring-security.xml" } )
public class HomeServiceTest {
	
	private static final int MAIN_HOME_ID = 2;
	private static final int HOME_ID = 8;
	
	@Autowired
	@Qualifier("Test")
	private IHomeService homeService;

	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	@Test
	@Transactional
	public void test_update_home_address() throws Exception {
		Home homeToUpdate = new Home();
		String address = "Ahihi do ngoc";
		homeToUpdate.setAddress(address);
		
		homeService.updateHome(VALID_USER_ID, HOME_ID, homeToUpdate);
		
		Home homeAfterUpdate = homeService.getHome(VALID_USER_ID, HOME_ID);
		assertNotNull(homeAfterUpdate);
		assertThat(homeAfterUpdate.getAddress(), is(address));
	}
	
	@Test
	@Transactional
	public void test_update_existing_home_address_must_throw_exception() throws Exception {
		
		exception.expect(Exception.class);
		
		// Init address to update
		Home homeToUpdate = new Home();
		String address = "Same address";
		homeToUpdate.setAddress(address);
		
		// First home
		homeService.updateHome(VALID_USER_ID, HOME_ID, homeToUpdate);
		
		// Another home
		homeService.updateHome(VALID_USER_ID, MAIN_HOME_ID, homeToUpdate);
		
		// NOTE: If you don't get data from DB by using SQLquery you can't make this test passed 
		homeService.isEnabled(MAIN_HOME_ID);
	}
	
	@Test
	@Transactional
	public void test_update_existing_home_name_same_user_must_throw_exception() throws Exception {
		
		exception.expect(Exception.class);
		
		// Init name to update
		Home homeToUpdate = new Home();
		String name = "Same name";
		homeToUpdate.setName(name);
		
		// First home
		homeService.updateHome(VALID_USER_ID, HOME_ID, homeToUpdate);
		
		// Another home
		homeService.updateHome(VALID_USER_ID, MAIN_HOME_ID, homeToUpdate);
		
		// NOTE: If you don't get data from DB by using SQLquery you can't make this test passed 
		homeService.isEnabled(MAIN_HOME_ID);
	}
}
