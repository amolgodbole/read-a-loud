package readaloud.app;


import java.util.Date;

import readaloud.models.User;

public class Populate extends People {

	public static void main(String[] args) {
		System.out.println("Loading data to pplhib");
		Populate p = new Populate();
		User usr = new User();
		usr.setId("nandhu.nells@gmail.com");
		usr.setFirstName("Nans");
		usr.setLastName("Nells");
		usr.setPassword("hello");
		usr.setCreated(new Date());
		p.createUser(usr);
		p.find("nandhu.nells@gmail.com");
	}
}
