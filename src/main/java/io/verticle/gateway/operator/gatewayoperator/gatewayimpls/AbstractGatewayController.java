package io.verticle.gateway.operator.gatewayoperator.gatewayimpls;

public class AbstractGatewayController implements GatewayController {

    String containerImage = "";
    String controllerName = "";

    @Override
    public String getContainerImage() {
        return containerImage;
    }

    @Override
    public void setContainerImage(String containerImage) {
        this.containerImage = containerImage;
    }

    @Override
    public String getControllerName() {
        return controllerName;
    }

    @Override
    public void setControllerName(String controllerName) {
        this.controllerName = controllerName;
    }
}
