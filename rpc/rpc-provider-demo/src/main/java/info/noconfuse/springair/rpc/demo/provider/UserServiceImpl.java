package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.demo.client.UserService;

/**
 * Created by zzp on 7/29/16.
 */
public class UserServiceImpl implements UserService {

    @Override
    public String hello(String user) {
        return "Hello " + user + "!";
    }

}
