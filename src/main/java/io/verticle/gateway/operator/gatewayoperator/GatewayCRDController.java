package io.verticle.gateway.operator.gatewayoperator;


import io.fabric8.kubernetes.api.model.*;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.api.Context;
import io.javaoperatorsdk.operator.api.*;
import io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gateway.Gateway;
import io.verticle.gateway.operator.gatewayoperator.gatewayimpls.GatewayCatalog;
import io.verticle.gateway.operator.gatewayoperator.gatewayimpls.GatewayController;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Controller
public class GatewayCRDController implements ResourceController<Gateway> {

    public static final String TYPE_LOAD_BALANCER = "LoadBalancer";

    @Autowired
    GatewayCatalog catalog;

    @Value("${gateway.image.pullPolicy:IfNotPresent}")
    String imagePullPolicy;

    public static final String KIND = "Gateway";
    private static final Logger log = LoggerFactory.getLogger(GatewayCRDController.class);
    public static final String LABEL_IO_VERTICLE_GATEWAY_NAME = "io.verticle.gateway.name";
    public static final String LABEL_APP = "app";
    public static final String PREFIX_GATEWAY = "gateway-";
    public static final String LABEL_PROVIDED_BY = "providedBy";
    public static final int GW_CONTAINER_PORT = 9080;
    public static final int GW_SERVICE_PORT = 9080;

    private final KubernetesClient kubernetesClient;

    public GatewayCRDController(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @Override
    public DeleteControl deleteResource(Gateway resource, Context<Gateway> context) {
        log.info("Gateway deleteResource for: {}", resource.getMetadata().getName());

        String namespace = resource.getMetadata().getNamespace();
        if (namespace == null)
            namespace = "default";

        boolean deletedPod = kubernetesClient
                .pods()
                .inNamespace(namespace)
                .withLabel(LABEL_IO_VERTICLE_GATEWAY_NAME, resource.getMetadata().getName())
                .delete();

        log.info("deleting gateway pod {}, success {}", resource.getMetadata().getName(), deletedPod);

        boolean deletedService = kubernetesClient
                .services()
                .inNamespace(namespace)
                .withLabel(LABEL_IO_VERTICLE_GATEWAY_NAME, resource.getMetadata().getName())
                .delete();

        log.info("deleting gateway service {}, success {}", resource.getMetadata().getName(), deletedService);

        return DeleteControl.DEFAULT_DELETE;
    }

    @Override
    public UpdateControl<Gateway> createOrUpdateResource(
            Gateway resource, Context<Gateway> context) {
        log.info("Gateway createOrUpdateResource for: {}", resource.getMetadata().getName());


        GatewayController gatewayimpl = catalog.locateImplForGatewayClass(resource.getSpec().getGatewayClassName());

        if (ObjectUtils.isNotEmpty(gatewayimpl)){


            String namespace = resource.getMetadata().getNamespace();
            if (namespace == null)
                namespace = "default";



            int podsExisting = kubernetesClient
                    .pods()
                    .inNamespace(namespace)
                    .withLabel(LABEL_IO_VERTICLE_GATEWAY_NAME, resource.getMetadata().getName())
                    .list().getItems().size();

            if (podsExisting == 0){
                log.info("launching new gateway {}", resource.getMetadata().getName());
                log.info("using gateway image pull policy ", imagePullPolicy);

                Pod pod = new PodBuilder()
                        .withNewMetadata()
                        .withName(PREFIX_GATEWAY + resource.getMetadata().getName())
                        .addToLabels(LABEL_PROVIDED_BY, "VerticleGatewayOperator")
                        .addToLabels(LABEL_APP, PREFIX_GATEWAY + resource.getMetadata().getName())
                        .addToLabels(LABEL_IO_VERTICLE_GATEWAY_NAME, resource.getMetadata().getName())
                        .endMetadata()
                        .withSpec(new PodSpecBuilder()
                                .withContainers(new ContainerBuilder()
                                        .withName(PREFIX_GATEWAY + resource.getMetadata().getName())
                                        .withImage(gatewayimpl.getContainerImage())
                                        .withImagePullPolicy(imagePullPolicy)
                                        .withPorts(new ContainerPortBuilder()
                                                .withName("http")
                                                .withContainerPort(GW_CONTAINER_PORT)
                                                .withProtocol("TCP")
                                                .build())
                                        .build())
                                .build()).build();

                kubernetesClient
                        .pods()
                        .inNamespace(namespace)
                        .createOrReplace(pod);
            } else {
                log.info("not launching new gateway, already exists {}", resource.getMetadata().getName());
            }


            // if LOADBALANCER

            ServicePort servicePort = new ServicePort();
            servicePort.setPort(GW_SERVICE_PORT);
            servicePort.setTargetPort(new IntOrString(GW_CONTAINER_PORT));
            ServiceSpec serviceSpec = new ServiceSpec();
            serviceSpec.setType(TYPE_LOAD_BALANCER);
            serviceSpec.setPorts(Collections.singletonList(servicePort));
            Map<String, String> selectorMap = new HashMap<>();
            selectorMap.put("app", PREFIX_GATEWAY + resource.getMetadata().getName());
            serviceSpec.setSelector(selectorMap);

            kubernetesClient
                    .services()
                    .inNamespace(resource.getMetadata().getNamespace())
                    .createOrReplace(
                            new ServiceBuilder()
                                    .withNewMetadata()
                                    .withName(PREFIX_GATEWAY + resource.getMetadata().getName())
                                    .addToLabels("providedBy", "VerticleGatewayOperator")
                                    .addToLabels(LABEL_IO_VERTICLE_GATEWAY_NAME, resource.getMetadata().getName())
                                    .endMetadata()
                                    .withSpec(serviceSpec)
                                    .build());

            return UpdateControl.updateCustomResource(resource);
        } else {
            log.info("Could not locate referenced gateway class. Please check the spelling and make sure the gateway class is installed first.");
        }


        return UpdateControl.noUpdate();
    }



}
