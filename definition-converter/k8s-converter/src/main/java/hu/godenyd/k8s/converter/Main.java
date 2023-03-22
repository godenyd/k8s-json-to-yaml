package hu.godenyd.k8s.converter;

import java.io.StringReader;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonObject;

import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerBuilder;
import io.kubernetes.client.openapi.models.V1ContainerPortBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentBuilder;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentSpecBuilder;
import io.kubernetes.client.openapi.models.V1DeploymentStrategy;
import io.kubernetes.client.openapi.models.V1HostPathVolumeSourceBuilder;
import io.kubernetes.client.openapi.models.V1LabelSelectorBuilder;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpecBuilder;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpecBuilder;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.openapi.models.V1ServiceBuilder;
import io.kubernetes.client.openapi.models.V1ServicePortBuilder;
import io.kubernetes.client.openapi.models.V1ServiceSpec;
import io.kubernetes.client.openapi.models.V1ServiceSpecBuilder;
import io.kubernetes.client.openapi.models.V1VolumeBuilder;
import io.kubernetes.client.openapi.models.V1VolumeMountBuilder;
import io.kubernetes.client.util.Yaml;

/**
 * Fields to first implement:
 * - service name
 * - image
 * - env
 * - volumes
 * - ports
 * - initcontainers
 */
public class Main {

    private static String databaseString = "{\"service-name\":\"database\",\"image\":\"postgres:12\",\"env\":\"\",\"volumes\":\"/database/pg:/var/lib/postgresql/data\",\"ports\":\"5432\"}";

    private static final String SERVICE_NAME_KEY = "service-name";
    private static final String IMAGE_KEY = "image";
    private static final String ENV_KEY = "env";
    private static final String VOLUMES_KEY = "volumes";
    private static final String PORTS_KEY = "ports";

    public static void main(String... args) {

        javax.json.JsonReader JsonReader = Json.createReader(new StringReader(databaseString));
        JsonObject databaseObject = JsonReader.readObject();
        JsonReader.close();

        // Collect variables

        String name = databaseObject.getString(SERVICE_NAME_KEY);
        String image = databaseObject.getString(IMAGE_KEY);
        String env = databaseObject.getString(ENV_KEY);
        String volumes = databaseObject.getString(VOLUMES_KEY);
        int ports = Integer.parseInt(databaseObject.getString(PORTS_KEY));

        // Build Service

        V1ServiceBuilder serviceBuilder = new V1ServiceBuilder();

        V1ObjectMeta serviceMeta = new V1ObjectMeta();

        serviceMeta.setName(name);

        serviceMeta.setLabels(Map.of("app", name));
        serviceBuilder.withMetadata(serviceMeta);

        V1ServiceSpec serviceSpec = new V1ServiceSpecBuilder()
                .withType("NodePort")
                .withPorts(new V1ServicePortBuilder()
                        .withPort(ports)
                        .build())
                .withSelector(Map.of("app", name))
                .build();

        serviceBuilder.withSpec(serviceSpec);

        V1Service service = serviceBuilder
                .withApiVersion("v1")
                .withKind("service")
                .build();

        // Build Deployment

        V1DeploymentBuilder deploymentBuilder = new V1DeploymentBuilder();

        V1ObjectMeta deploymentMeta = new V1ObjectMeta();
        deploymentMeta.setName(name);
        deploymentMeta.setLabels(Map.of("app", name));

        deploymentBuilder.withMetadata(deploymentMeta);

        // V1PodTemplate podTemplate = new V1PodTemplateBuilder()
        // .withMetadata(new V1ObjectMetaBuilder()
        // .withName(name)
        // .withLabels(Map.of("app", name))
        // .build())
        // .build();

        String volumesString = databaseObject.getString(VOLUMES_KEY);

        String namespace = "sample-namespace";

        V1Container container = new V1ContainerBuilder()
                .withName(name)
                .withImage(image)
                .withImagePullPolicy("IfNotPresent")
                .withPorts(new V1ContainerPortBuilder()
                        .withContainerPort(ports)
                        .withProtocol("TCP")
                        .build())
                .withVolumeMounts(new V1VolumeMountBuilder()
                        .withMountPath(volumesString.split(":")[1])
                        .withName(name + "-volume")
                        .withSubPathExpr(namespace + volumesString.split(":")[0])
                        .build())
                .build();

        V1PodTemplateSpec templateSpec = new V1PodTemplateSpecBuilder()
                .withSpec(new V1PodSpecBuilder()
                        .withContainers(container)
                        .withRestartPolicy("Always")
                        .withVolumes(new V1VolumeBuilder()
                                .withName(name + "-volume")
                                .withHostPath(new V1HostPathVolumeSourceBuilder()
                                        .withPath("/data_distributed/volume1/")
                                        .withType("DirectoryOrCreate")
                                        .build())
                                .build())
                        .build())
                .build();

        V1DeploymentSpec deploymentSpec = new V1DeploymentSpecBuilder()
                .withReplicas(1)
                .withSelector(new V1LabelSelectorBuilder().withMatchLabels(Map.of("app", name)).build())
                .withStrategy(new V1DeploymentStrategy().type("Recreate"))
                .withTemplate(templateSpec)
                .build();

        deploymentBuilder.withSpec(deploymentSpec);

        V1Deployment deployment = deploymentBuilder
                .withApiVersion("apps/v1")
                .withKind("Deployment")
                .build();

        System.out.println(Yaml.dump(service));
        System.out.println(Yaml.dump(deployment));

    }
}
