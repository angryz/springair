package info.noconfuse.modules.web.http;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 测试用 Controller
 * Created by zzp on 6/21/16.
 */
@Controller
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/params")
    public void testParams(@RequestParam String firstName,
                           @RequestParam String lastName,
                           @RequestParam int age) {
        System.out.println("FirstName: " + firstName);
        System.out.println("LastName: " + lastName);
        System.out.println("Age: " + age);
    }

    @RequestMapping("/json")
    public void testJson(@RequestBody User user) {
        System.out.println(user);
    }

    @RequestMapping("/form")
    public void testForm(@RequestBody User user) {
        System.out.println(user);
    }
}
