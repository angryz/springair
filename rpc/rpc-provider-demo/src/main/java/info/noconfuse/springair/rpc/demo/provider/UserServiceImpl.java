package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.demo.client.UserService;
import org.springframework.stereotype.Service;

/**
 * Created by zzp on 7/29/16.
 */
@Service
public class UserServiceImpl implements UserService {

    @Override
    public String hello(String user) {
        return "Hello " + user + "!";
    }

}
