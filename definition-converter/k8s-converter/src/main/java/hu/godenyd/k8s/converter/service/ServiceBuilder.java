package hu.godenyd.k8s.converter.service;

import java.util.Map;

import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.openapi.models.V1ServicePortBuilder;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.openapi.models.V1ServiceSpecBuilder;

public class ServiceBuilder {

    public static V1Service buildService(String name, int port) {

        V1ServiceBuilder serviceBuilder = new V1ServiceBuilder();

        V1ObjectMeta serviceMeta = new V1ObjectMeta();

        serviceMeta.setName(name);

        serviceMeta.setLabels(Map.of("app", name));
        serviceBuilder.withMetadata(serviceMeta);

        V1ServiceSpec serviceSpec = new V1ServiceSpecBuilder()
                .withType("NodePort")
                .withPorts(new V1ServicePortBuilder()
                        .withPort(port)
                        .build())
                .withSelector(Map.of("app", name))
                .build();

        serviceBuilder.withSpec(serviceSpec);

        V1Service service = serviceBuilder
                .withApiVersion("v1")
                .withKind("service")
                .build();

        return service;
    }

    private ServiceBuilder() {

    }
}
