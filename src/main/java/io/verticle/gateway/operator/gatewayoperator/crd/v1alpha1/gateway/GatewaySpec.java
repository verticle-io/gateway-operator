package io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gateway;

public class GatewaySpec {

    private String gatewayClassName;

    private Object listeners;

    private Object addresses;

    public String getGatewayClassName() {
        return gatewayClassName;
    }

    public void setGatewayClassName(String gatewayClassName) {
        this.gatewayClassName = gatewayClassName;
    }

    public Object getListeners() {
        return listeners;
    }

    public void setListeners(Object listeners) {
        this.listeners = listeners;
    }

    public Object getAddresses() {
        return addresses;
    }

    public void setAddresses(Object addresses) {
        this.addresses = addresses;
    }
}