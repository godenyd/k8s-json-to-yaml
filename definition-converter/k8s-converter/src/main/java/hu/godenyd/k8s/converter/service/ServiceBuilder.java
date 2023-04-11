package hu.godenyd.k8s.converter.service;

import java.util.Map;

import javax.json.JsonObject;

import hu.godenyd.k8s.converter.util.BuilderUtil;
import hu.godenyd.k8s.converter.util.JsonKeys;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.openapi.models.V1ServiceSpecBuilder;

public class ServiceBuilder {

        public static V1Service buildService(JsonObject object) {

                String name = object.getString(JsonKeys.SERVICE_NAME_KEY);

                V1ServiceBuilder serviceBuilder = new V1ServiceBuilder();

                V1ObjectMeta serviceMeta = new V1ObjectMeta();

                serviceMeta.setName(name);

                serviceMeta.setLabels(Map.of("app", name));
                serviceBuilder.withMetadata(serviceMeta);

                V1ServiceSpec serviceSpec = new V1ServiceSpecBuilder()
                                .withType("NodePort")
                                .withPorts(BuilderUtil
                                                .getServicePorts(object.getJsonArray(JsonKeys.PORTS_KEY).asJsonArray()))
                                .withSelector(Map.of("app", name))
                                .build();

                serviceBuilder.withSpec(serviceSpec);

                V1Service service = serviceBuilder
                                .withApiVersion("v1")
                                .withKind("Service")
                                .build();

                return service;
        }

        private ServiceBuilder() {

        }
}
