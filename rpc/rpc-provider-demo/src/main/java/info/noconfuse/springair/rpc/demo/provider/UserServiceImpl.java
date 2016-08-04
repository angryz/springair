package info.noconfuse.springair.rpc.demo.provider;

import info.noconfuse.springair.rpc.RpcService;
import info.noconfuse.springair.rpc.demo.client.UserService;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * Created by zzp on 7/29/16.
 */
@RpcService
public class UserServiceImpl implements UserService {

    @Override
    public String hello(String user) {
        return "Hello " + user + "!";
    }

}
