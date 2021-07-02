# Verticle Gateway Operator

A kubernetes operator that spawns and configures  instances of [Spring Cloud Gateway](https://cloud.spring.io/spring-cloud-gateway/reference/html/) on your cluster.

It uses Verticle's implementation of Spring Cloud Gateway with enhancements: [https://github.com/verticle-io/spring-cloud-gateway-kubernetes](https://github.com/verticle-io/spring-cloud-gateway-kubernetes) 


![image](/verticle-gateway-operator.png)

# Gateway Operator Features

## Cloud native deployment

The gateway operator utilizes the new [Kubernetes Gateway API](https://gateway-api.sigs.k8s.io/) and is configured by CRDs like `Gateway`, `GatewayClass` and `HTTPRoute`.

The operator also takes additional metadata configuration to leverage SCG features like rate-limiting, dedup, rewrites etc.


## Behind-Ingress or Loadbalancer

Gateways can be configured to either expose as LoadBalancer or be positioned as ingressed service.

## Self-Configuring Gateways
Spawned Gateways will watch specs like `HTTPRoutes`, `TCPRoutes` to configure themselves at runtime.

## Extended Authorization methods with APIKeys

The gateways support standard OIDC which can be configured with a broad range of identity providers which already works of-the-shelf with Spring Security 5.

In addition machine-to-machine communication can be secured using APIKeys with headers, e.g. `Authorization: apikey <BRILLIANTSECRET>`

APIKeys are configured via standard Kubernetes `Secrets`. 

# About Spring Cloud Gateway

In case you are not familiar with SCG, a short intro from the docs:

_"This project provides a library for building an API Gateway on top of Spring WebFlux. Spring Cloud Gateway aims to provide a simple, yet effective way to route to APIs and provide cross cutting concerns to them such as: security, monitoring/metrics, and resiliency."_

* Built on Spring Framework 5, Project Reactor and Spring Boot 2.0
* Able to match routes on any request attribute.

* Predicates and filters are specific to routes.

* Circuit Breaker integration.

* Spring Cloud DiscoveryClient integration

* Easy to write Predicates and Filters

* Request Rate Limiting

* Path Rewriting

# Quickstart

## Installation

### via Kubectl

```
$ kubectl apply -n gateway-operator -f https://raw.githubusercontent.com/verticleio/gateway-operator/stable/manifests/install.yaml 
```

### via Helm

```
$ helm repo add verticle-operator https://github.com/verticle-io/gateway-operator
"verticle-operator" has been added to your repositories

$ helm install --name my-release verticle-operator/gateway-operator
NAME: my-release
...
```

Checkout configuration values on the wiki.

## Create a new GatewayClass

Following the API specs, GatewayClass resources are supposed to be provisioned by the infrastructure provider, who manages the cluster. They can supply multiple different Gateway types with specific configuration.

```yaml
cat <<EOF | kubectl apply -f -
apiVersion: networking.x-k8s.io/v1alpha1
kind: GatewayClass
metadata:
  name: secured-gateway
spec:
  controller: "io.verticle.operator/gateway-apikey"
  parametersRef:
    group: v1
    kind: ConfigMap
    name: secured-gateway-config

---
# Configuring a gateway class that supports APIKey auth
kind: ConfigMap
#apiVersion: io.verticle.operator/v1alpha1
apiVersion: v1
metadata:
  name: secured-gateway-config

data:
  daemonSetEnabled: false
  replicas: 3 // (default)
  auth-apikey-from: Kubernetes
  auth-apikey-namespace: default

EOF
```

The operator will register this `GatewayClass` configuration and watch for approproate `Gateway` CRDs matching it.

## Spawning a Gateway

Users ref the GatewayClass and define a gateway that will route their workload traffic. They specify the frontend port and protocol the gateway will listen on.

The operator will spawn the Spring Cloud Gateway while watching this CRD.

```yaml
cat <<EOF | kubectl apply -f -
apiVersion: networking.x-k8s.io/v1alpha1
kind: Gateway
metadata:
  name: private-gateway
spec:
  gatewayClassName: secured-gateway
  addresses: []
  listeners:
    - protocol: HTTP
      port: 80
      routes:
        kind: HTTPRoute
        selector:
          matchLabels:
            route: route-echo
EOF
```

All routes matching `app: route-echo` will be selected for this gateway.

## Configuring a HTTP route


Routing definitions are decoupled and match to a `Gateway`. 

```yaml
cat <<EOF | kubectl apply -f -
apiVersion: networking.x-k8s.io/v1alpha1
kind: HTTPRoute
metadata:
  name: route-echo-http-1
  labels:
    route: route-echo
spec:
  gateways:
    allow: SameNamespace
    gatewayRefs:
      - name: private-gateway
        namespace: default
  hostnames:
    - "localhost"
  rules:
    - matches:
        - path:
            type: Exact
            value: /echo
      forwardTo:
        - serviceName: echoservice
          port: 8080
          weight: 100
    - matches:
        - path:
            type: Exact
            value: /get
      forwardTo:
        - serviceName: httpbin.org
          port: 80
          weight: 100


EOF
```

# Documentation

See wiki.

# Roadmap

## Vault support for APIKeys

Storing and managing APIKeys in vaults, e.g. Azure Keyvault, HashiCorp Vault etc.

## Horizontal Pod Autoscaling

Support for autoscaling gateway pods.

## Operator Frontend

Managing gateways, configure routes and security in webbased frontend.

## OLM support

Deploying and managing the Operator lifecycle via [OLM](https://github.com/operator-framework/operator-lifecycle-manager)

## Customization Support

Ability to spawn your own extensions of the standard Spring Cloud Gateway functionality via the operator
