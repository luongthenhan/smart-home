package com.hcmut.smarthome.rest;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.User;
import com.hcmut.smarthome.sec.IAuthenticationService;
import com.hcmut.smarthome.service.IMailService;
import com.hcmut.smarthome.service.IUserService;

@CrossOrigin
@RequestMapping("/users")
@PropertySource("classpath:smarthome.properties")
@RestController
public class UserResource {
	
	@Autowired
	private IAuthenticationService authService;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private IMailService mailService;
	
	@Value("${activation.successful.webpage}")
	private String successfulActivationWebpage;
	
	@Value("${activation.fail.webpage}")
	private String failActivationWebpage;
	
	@SuppressWarnings("unchecked")
	@RequestMapping( path = "/signup", method = RequestMethod.POST)
	public ResponseEntity<JSONObject> signUp(@RequestBody User user) {
		
		int id = userService.addUser(user);
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("returnCode", new Integer(id));
		if(id > 0) {
			mailService.sendActivationMail(user.getEmail(), id);
			return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.CREATED);
		}
		
		return new ResponseEntity<JSONObject>(jsonObject, HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(path = "/activation/{userId}", method = RequestMethod.GET)
	public ResponseEntity<String> activateUser(@PathVariable("userId") int userId) {
		
		boolean success = userService.activateUser(userId);
		if(success) {
			return new ResponseEntity<String>(successfulActivationWebpage, HttpStatus.OK);
		}
		
		return new ResponseEntity<String>(failActivationWebpage, HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<User> getUser() {
		
		User currentUser = userService.getById(authService.getCurrentUserId());
		if(currentUser == null) {
			return new ResponseEntity<User>(HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	}

}

