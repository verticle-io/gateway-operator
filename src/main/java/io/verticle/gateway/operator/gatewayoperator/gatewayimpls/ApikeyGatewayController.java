package io.verticle.gateway.operator.gatewayoperator.gatewayimpls;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "gateways.apikey")
public class ApikeyGatewayController extends AbstractGatewayController implements GatewayController {

    public ApikeyGatewayController(){

    }
}
