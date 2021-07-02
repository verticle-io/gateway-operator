package io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gatewayclass;

public class GatewayClassStatus {

    ConditionsSpec[] conditions;

    public ConditionsSpec[] getConditions() {
        return conditions;
    }

    public void setConditions(ConditionsSpec[] conditions) {
        this.conditions = conditions;
    }
}
