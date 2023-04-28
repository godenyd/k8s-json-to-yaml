package hu.godenyd.k8s.converter.deployment;

import java.util.Map;

import javax.json.JsonObject;

import hu.godenyd.k8s.converter.util.BuilderUtil;
import hu.godenyd.k8s.converter.util.JsonKeys;
import io.kubernetes.client.openapi.models.V1Container;
import io.kubernetes.client.openapi.models.V1ContainerBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1DeploymentBuilder;
import io.kubernetes.client.openapi.models.V1DeploymentSpec;
import io.kubernetes.client.openapi.models.V1DeploymentSpecBuilder;
import io.kubernetes.client.openapi.models.V1DeploymentStrategy;
import io.kubernetes.client.openapi.models.V1LabelSelectorBuilder;
import io.kubernetes.client.openapi.models.V1ObjectMeta;
import io.kubernetes.client.openapi.models.V1PodSpecBuilder;
import io.kubernetes.client.openapi.models.V1PodTemplateSpec;
import io.kubernetes.client.openapi.models.V1PodTemplateSpecBuilder;
import io.kubernetes.client.util.Yaml;

public class DeploymentBuilder {

        public static String buildDeploymentAString(JsonObject object) {
                return Yaml.dump(object);
        }

        public static String buildDeploymentAString(String jsonString) {
                return Yaml.dump(buildDeployment(jsonString));
        }

        public static V1Deployment buildDeployment(String jsonString) {

                return buildDeployment(BuilderUtil.getJsonObject(jsonString));
        }

        public static V1Deployment buildDeployment(JsonObject object) {

                String name = object.getString(JsonKeys.SERVICE_NAME_KEY);
                String image = object.getString(JsonKeys.IMAGE_KEY);

                V1DeploymentBuilder deploymentBuilder = new V1DeploymentBuilder();

                V1ObjectMeta deploymentMeta = new V1ObjectMeta();
                deploymentMeta.setName(name);
                deploymentMeta.setLabels(Map.of("app", name));

                deploymentBuilder.withMetadata(deploymentMeta);

                V1Container container = new V1ContainerBuilder()
                                .withName(name)
                                .withImage(image)
                                .withImagePullPolicy("IfNotPresent")
                                .withPorts(BuilderUtil.getContainerPorts(object.getJsonArray(JsonKeys.PORTS_KEY)))
                                .withEnv(BuilderUtil.getEnv(object.getJsonObject(JsonKeys.ENV_KEY)))
                                .withVolumeMounts(BuilderUtil.getVolumeMounts(object))
                                .build();

                V1PodTemplateSpec templateSpec = new V1PodTemplateSpecBuilder()
                                .withMetadata(deploymentMeta)
                                .withSpec(new V1PodSpecBuilder()
                                                .withContainers(container)
                                                .withRestartPolicy("Always")
                                                .withVolumes(BuilderUtil.getSharedVolume())
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
