package io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gatewayclass;

public class ConditionsSpec {

    String type;
    String status;
    String  Reason;
    String Message;
    String lastTransitionTime;

    public String getLastTransitionTime() {
        return lastTransitionTime;
    }

    public void setLastTransitionTime(String lastTransitionTime) {
        this.lastTransitionTime = lastTransitionTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }
}
