package io.verticle.gateway.operator.gatewayoperator;


import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.*;
import io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gatewayclass.GatewayClass;
import io.verticle.gateway.operator.gatewayoperator.gatewayimpls.GatewayCatalog;
import io.verticle.gateway.operator.gatewayoperator.gatewayimpls.GatewayController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@Controller
public class GatewayClassCRDController implements ResourceController<GatewayClass> {

    @Autowired
    GatewayCatalog catalog;

    public static final String KIND = "GatewayClass";
    private static final Logger log = LoggerFactory.getLogger(GatewayClassCRDController.class);

    private final KubernetesClient kubernetesClient;

    public GatewayClassCRDController(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public DeleteControl deleteResource(GatewayClass resource, Context<GatewayClass> context) {
        log.info("Execution deleteResource for: {}", resource.getMetadata().getName());
        return DeleteControl.DEFAULT_DELETE;
    }

    @Override
    public UpdateControl<GatewayClass> createOrUpdateResource(
            GatewayClass resource, Context<GatewayClass> context) {
        log.info("GatewayClass createOrUpdateResource for: {}", resource.getMetadata().getName());

        GatewayController gateway = catalog.registerGatewayClass(resource.getMetadata().getName(), resource.getSpec().getController());
        if (gateway != null){
            log.info("registered controller for gateway class {}", resource.getSpec().getController());
        }

        return UpdateControl.updateCustomResource(resource);
    }
}
