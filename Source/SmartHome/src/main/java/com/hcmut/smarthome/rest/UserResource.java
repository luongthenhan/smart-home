package com.hcmut.smarthome.rest;

import javax.transaction.NotSupportedException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.User;
import com.hcmut.smarthome.service.IHomeService;

@CrossOrigin
@RequestMapping("/users")
@RestController
public class UserResource {

	@Autowired
	private IHomeService homeService;
	
//	@RequestMapping(method = RequestMethod.GET, path = "/{userId}/homes")
//	public ResponseEntity<List<Home>> getAllHomes(@PathVariable int userId){
//		return new ResponseEntity<List<Home>>(homeService.getAllHomes(userId), HttpStatus.OK); 
//	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/login")
	public ResponseEntity<Void> login(@RequestBody User newUser) throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method=RequestMethod.PUT, path = "/signup/{username}")
	public ResponseEntity<Void> signUp(@PathVariable String username) throws NotSupportedException{
		
		throw new NotSupportedException();
	}
	

}

