package io.verticle.gateway.operator.gatewayoperator.gatewayimpls;

public interface GatewayController {


    String getContainerImage();

    void setContainerImage(String containerImage);

    String getControllerName();

    void setControllerName(String controllerName);
}
