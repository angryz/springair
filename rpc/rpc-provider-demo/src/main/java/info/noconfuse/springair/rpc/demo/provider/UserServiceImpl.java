package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.demo.client.User;
import info.noconfuse.springair.rpc.provider.RpcService;
import info.noconfuse.springair.rpc.demo.client.UserService;

/**
 * Created by zzp on 7/29/16.
 */
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public String hello(User user) {
        return "Hello " + user + "!";
    }

}
