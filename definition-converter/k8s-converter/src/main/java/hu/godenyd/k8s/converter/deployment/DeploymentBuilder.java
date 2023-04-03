package hu.godenyd.k8s.converter.deployment;

import java.util.Map;

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
import io.kubernetes.client.openapi.models.V1VolumeBuilder;
import io.kubernetes.client.openapi.models.V1VolumeMountBuilder;

public class DeploymentBuilder {

    public static V1Deployment buildDeployment(String namespace, String name, String image, int ports, String volumes) {

        V1DeploymentBuilder deploymentBuilder = new V1DeploymentBuilder();

        V1ObjectMeta deploymentMeta = new V1ObjectMeta();
        deploymentMeta.setName(name);
        deploymentMeta.setLabels(Map.of("app", name));

        deploymentBuilder.withMetadata(deploymentMeta);

        V1Container container = new V1ContainerBuilder()
                .withName(name)
                .withImage(image)
                .withImagePullPolicy("IfNotPresent")
                .withPorts(new V1ContainerPortBuilder()
                        .withContainerPort(ports)
                        .withProtocol("TCP")
                        .build())
                .withVolumeMounts(new V1VolumeMountBuilder()
                        .withMountPath(volumes.split(":")[1])
                        .withName(name + "-volume")
                        .withSubPathExpr(namespace + volumes.split(":")[0])
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

        return deployment;
    }

    private DeploymentBuilder() {
        
    }

}
