package io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gateway;

import io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gatewayclass.ConditionsSpec;

public class GatewayStatus {

    ConditionsSpec[] conditions;

    public ConditionsSpec[] getConditions() {
        return conditions;
    }

    public void setConditions(ConditionsSpec[] conditions) {
        this.conditions = conditions;
    }
}
