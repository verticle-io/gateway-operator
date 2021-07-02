package io.verticle.gateway.operator.gatewayoperator.gatewayimpls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class GatewayCatalog {

    private static final Logger log = LoggerFactory.getLogger(GatewayCatalog.class);

    @Autowired
    ListableBeanFactory beanFactory;

    Map<String, GatewayController> registeredGatewayClasses = new HashMap<>();
    Map<String, GatewayController> gatewayControllers = new HashMap<>();

    @PostConstruct
    private void registerImpls(){
        beanFactory.getBeansOfType(GatewayController.class).forEach((s, gateway) -> {
            log.info("registering gateway controller ", gateway.getControllerName());
            gatewayControllers.put(gateway.getControllerName(), gateway);
        });
    }

    public GatewayController registerGatewayClass(String gatewayClassName, String gatewayControllerName){

        GatewayController gateway = gatewayControllers.get(gatewayControllerName);
        if (gateway != null){
            log.info("registering gateway class {} with controller {} ", gatewayClassName, gatewayControllerName);
            registeredGatewayClasses.put(gatewayClassName, gateway);
        } else {
            log.warn("unknown gateway controller: " + gatewayControllerName);
        }

        return gateway;

    }
    public GatewayController locateImplForGatewayClass(String gatewayClassName){
        GatewayController gateway = registeredGatewayClasses.get(gatewayClassName);
        if (gateway != null){
            log.info("located gateway class {} with controller {} ", gatewayClassName, gateway.getControllerName());

        } else {
            log.warn("unknown gateway class: {} ", gatewayClassName);
        }

        return gateway;
    }


}
