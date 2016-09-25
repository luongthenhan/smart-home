package com.hcmut.smarthome.rest;

import javax.transaction.NotSupportedException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.User;


@RestController
public class UserResource {

	@RequestMapping(method = RequestMethod.POST, path = "/login")
	public ResponseEntity<Void> login(@RequestBody User newUser) throws NotSupportedException {
		throw new NotSupportedException();
	}

	@RequestMapping(method=RequestMethod.PUT, path = "/signup/{username}")
	public ResponseEntity<Void> signUp(@PathVariable String username) throws NotSupportedException{
		throw new NotSupportedException();
	}
	

}
