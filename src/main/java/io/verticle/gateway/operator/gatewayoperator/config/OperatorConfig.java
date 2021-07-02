package io.verticle.gateway.operator.gatewayoperator.config;

import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.javaoperatorsdk.operator.Operator;
import io.javaoperatorsdk.operator.api.ResourceController;
import io.javaoperatorsdk.operator.config.runtime.DefaultConfigurationService;
import io.verticle.gateway.operator.gatewayoperator.GatewayCRDController;
import io.verticle.gateway.operator.gatewayoperator.GatewayClassCRDController;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@EnableConfigurationProperties
@Configuration
public class OperatorConfig {

    @Bean
    public KubernetesClient kubernetesClient() {
        return new DefaultKubernetesClient();
    }

    @Bean
    public GatewayCRDController gatewayCRDController(KubernetesClient client) {
        return new GatewayCRDController(client);
    }

    @Bean
    public GatewayClassCRDController gatewayClassCRDController(KubernetesClient client) {
        return new GatewayClassCRDController(client);
    }

    //  Register all controller beans
    @Bean
    public Operator operator(KubernetesClient client, List<ResourceController> controllers) {
        Operator operator = new Operator(client, DefaultConfigurationService.instance());
        controllers.forEach(operator::register);
        return operator;
    }
}