package io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gatewayclass;


public class GatewayClassSpec {

    private String controller;
    private Object parametersRef;

    public Object getParametersRef() {
        return parametersRef;
    }

    public void setParametesrRef(Object parametersRef) {
        this.parametersRef = parametersRef;
    }

    public String getController() {
        return controller;
    }

    public void setController(String controller) {
        this.controller = controller;
    }
}