package info.noconfuse.springair.demo.web.controller;

import info.noconfuse.springair.demo.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {

    @RequestMapping(method = RequestMethod.POST)
    public void createUser(@RequestParam String firstName,
                           @RequestParam String lastName) {
        System.out.println("FirstName: " + firstName);
        System.out.println("LastName: " + lastName);
    }

    @RequestMapping(headers = {"accept:json"})
    public void testJson(@RequestBody User user) {
        System.out.println(user);
    }

    @RequestMapping(headers = {})
    public void testForm(@RequestBody User user) {
        System.out.println(user);
    }
}
