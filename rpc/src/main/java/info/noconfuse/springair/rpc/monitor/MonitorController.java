package info.noconfuse.springair.rpc.monitor;

import info.noconfuse.springair.rpc.ServiceGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller of MVC.
 *
 * @author Zheng Zhipeng
 */
@Controller
public class MonitorController {

    @Autowired
    private ServiceMonitor serviceMonitor;

    @RequestMapping("/monitor")
    public String monitor(Model model) {
        List<ServiceGroup> services = null;
        try {
            services = serviceMonitor.allServices();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // UserService
        /*
        List<ServiceNode> userServices = new ArrayList<>(2);
        ServiceNode sn1 = new ServiceNode("UserService-1",
                "http://192.168.20.60:8081/UserService",
                "/services_registry/UserService/UserService-1");
        ServiceNode sn2 = new ServiceNode("UserService-2",
                "http://127.0.0.1:8082/UserService",
                "/services_registry/UserService/UserService-2");
        userServices.add(sn1);
        userServices.add(sn2);
        ServiceGroup userService = new ServiceGroup("UserService", userServices);

        // PushService
        List<ServiceNode> pushServices = new ArrayList<>(2);
        ServiceNode sn3 = new ServiceNode("PushService-1",
                "http://192.168.20.80:8088/PushService",
                "/services_registry/PushService/PushService-1");
        ServiceNode sn4 = new ServiceNode("PushService-2",
                "http://127.0.0.1:8089/PushService",
                "/services_registry/PushService/PushService-2");
        pushServices.add(sn3);
        pushServices.add(sn4);
        ServiceGroup pushService = new ServiceGroup("PushService", pushServices);

        // add all
        List<ServiceGroup> services = new ArrayList<>(2);
        services.add(userService);
        services.add(pushService);
        */
        model.addAttribute("services", services);

        return "monitor";
    }
}
