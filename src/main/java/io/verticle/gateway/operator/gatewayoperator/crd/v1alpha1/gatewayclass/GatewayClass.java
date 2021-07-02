package io.verticle.gateway.operator.gatewayoperator.crd.v1alpha1.gatewayclass;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.ShortNames;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("networking.x-k8s.io")
@Version("v1alpha1")
@ShortNames("gc")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GatewayClass extends CustomResource<GatewayClassSpec, GatewayClassStatus> {}