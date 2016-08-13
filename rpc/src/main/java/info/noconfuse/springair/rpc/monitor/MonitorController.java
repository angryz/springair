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
        model.addAttribute("services", services);

        return "monitor";
    }
}
