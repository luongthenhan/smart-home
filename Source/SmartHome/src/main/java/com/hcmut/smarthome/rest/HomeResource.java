package com.hcmut.smarthome.rest;

import static com.hcmut.smarthome.utils.ConstantUtil.ALL_GPIO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hcmut.smarthome.model.Home;
import com.hcmut.smarthome.model.Mode;
import com.hcmut.smarthome.service.IHomeService;

@RestController
@RequestMapping("users/{userId}")
@CrossOrigin
public class HomeResource {

	@Autowired
	private IHomeService homeService;
	
	@RequestMapping(method = RequestMethod.DELETE, path = "/homes/{homeId}")
	public ResponseEntity<Void> deleteHome(@PathVariable int homeId, @PathVariable int userId){
		if( homeService.deleteHome(userId, homeId) )
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(method = RequestMethod.PUT, path = "/homes/{homeId}")
	public ResponseEntity<Void> updateHome(@PathVariable int homeId, @PathVariable int userId, @RequestBody Home home){
		if( homeService.updateHome(userId, homeId, home) )
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/homes")
	public ResponseEntity<Void> addHome( @PathVariable int userId, @RequestBody Home home){
		if( homeService.addHome( userId, home) )
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(method = RequestMethod.GET, path= "/homes")
	public ResponseEntity<List<Home>> getHomes(@PathVariable int userId){
		return new ResponseEntity<List<Home>>(homeService.getAllHomes(userId),HttpStatus.OK);
	}
	
	@RequestMapping(method = RequestMethod.DELETE, path = "/homes/{homeId}/modes/{modeId}")
	public ResponseEntity<Void> deleteMode(@PathVariable int homeId, @PathVariable int modeId, @PathVariable int userId){
		if( homeService.deleteMode(homeId, modeId) )
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(method = RequestMethod.PUT, path = "/homes/{homeId}/modes/{modeId}")
	public ResponseEntity<Void> updateMode(@PathVariable int homeId, @PathVariable int modeId, @RequestBody Mode mode){
		if( homeService.updateMode( homeId, modeId, mode) )
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(method = RequestMethod.POST, path = "/homes/{homeId}/modes")
	public ResponseEntity<Void> addMode( @PathVariable int homeId, @RequestBody Mode mode){
		if( homeService.addMode( homeId, mode) > 0 )
			return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
		else return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(method = RequestMethod.GET, path= "/homes/{homeId}/modes")
	public ResponseEntity<List<Mode>> getModes(@PathVariable int homeId, @PathVariable int userId){
		return new ResponseEntity<List<Mode>>(homeService.getAllModes(homeId),HttpStatus.OK);
	}
	
	/**
	 * Get all gpio
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, path = "/homes//allGpio")
	public ResponseEntity<List<Integer>> getAllGpio() {
		return new ResponseEntity<List<Integer>>(ALL_GPIO, HttpStatus.OK);
	}


}
