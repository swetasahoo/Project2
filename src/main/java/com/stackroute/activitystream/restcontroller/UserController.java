package com.stackroute.activitystream.restcontroller;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;

import javax.annotation.Generated;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.stackroute.activitystream.dao.UserDao;

import com.stackroute.activitystream.model.User;


@RestController
@RequestMapping("/api/user")
@EnableWebMvc
public class UserController {
	
	//Why not private variables?
	@Autowired
	 UserDao userDAO;

	@Autowired
	 User user;
	
	
	@GetMapping("/allusers")
	public List getAllUser() {
		
		List<User> allUsers=userDAO.list();
		for(User user:allUsers)
		{
			Link link=linkTo(UserController.class).slash(user.getEmailId()).withSelfRel();
			user.add(link);
		}
		return allUsers;
	}

	@RequestMapping(value="/getUser",method=RequestMethod.POST)
	public ResponseEntity getUser(@RequestBody User user) {
		
		//User user1=userDAO.get(user.getEmailId());
		if (userDAO.get(user.getEmailId()) == null) {
			return new ResponseEntity<Object>("No User found for ID "+user.getEmailId(), HttpStatus.NOT_FOUND);
		}
//which user you are returning?  This may be null.
		return new ResponseEntity<User>(user, HttpStatus.OK);
	}


	@RequestMapping(value="/create",method=RequestMethod.POST)
	public ResponseEntity createUser(@RequestBody User user) {

		if(userDAO.isUserExist(user))
		{
			//IT should not be not found, you can say conflicts
			return new ResponseEntity(user.getEmailId()+" already Exist.", HttpStatus.NOT_FOUND);
		}
		userDAO.save(user);
		return new ResponseEntity<User>(user, HttpStatus.OK);
		
	}
	//This method should return all the details of user home.  Not just valid OR invalid
	@RequestMapping(value="/authenticate",method=RequestMethod.POST)
	public ResponseEntity<User> authenticateUser(@RequestBody User user,HttpSession session)
	{
		user=userDAO.validate(user.getEmailId(), user.getPassword());
		
		if(user==null)
		{
			return new ResponseEntity("Invalid Username and Password", HttpStatus.NOT_FOUND);
		}
		else
		{
			session.setAttribute("username",user.getEmailId());
			return new ResponseEntity<User>(user,HttpStatus.OK);
		}
	}
	@RequestMapping(value="/logout",method=RequestMethod.GET)
	public ResponseEntity logout(HttpSession session)
	{
		String username=(String)session.getAttribute("username");
		if(username!=null)
			
		{
			session.invalidate();
			return new ResponseEntity("Logout Successfull.",HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity("Already Logged out!! Please Login again.",HttpStatus.OK);
		}
	}

}
