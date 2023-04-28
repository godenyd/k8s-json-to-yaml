package hu.godenyd.k8s.converter.util;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonString;

import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1ContainerPortBuilder;
import io.kubernetes.client.openapi.models.V1EnvVar;
import io.kubernetes.client.openapi.models.V1EnvVarBuilder;
import io.kubernetes.client.openapi.models.V1EnvVarSourceBuilder;
import io.kubernetes.client.openapi.models.V1HostPathVolumeSourceBuilder;
import io.kubernetes.client.openapi.models.V1ObjectFieldSelectorBuilder;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServicePortBuilder;
import io.kubernetes.client.openapi.models.V1Volume;
import io.kubernetes.client.openapi.models.V1VolumeBuilder;
import io.kubernetes.client.openapi.models.V1VolumeMount;
import io.kubernetes.client.openapi.models.V1VolumeMountBuilder;

public class BuilderUtil {

    public static final String SHARED_VOLUME_NAME = "shared-data-volume";

    public static List<V1ServicePort> getServicePorts(JsonArray ports) {

        List<V1ServicePort> portList = ports.stream().map(port -> {

            String portString = ((JsonString) port).getString();

            String[] parts = portString.split(":");

            V1ServicePortBuilder portBuilder = new V1ServicePortBuilder();

            if (parts.length > 1) {
                portBuilder.withName(parts[0]);
                portBuilder.withPort(Integer.parseInt(parts[1]));
            } else {
                portBuilder.withPort(Integer.parseInt(parts[0]));
            }

            return portBuilder.build();
        }).collect(Collectors.toList());

        return portList;
    }

    public static List<V1ContainerPort> getContainerPorts(JsonArray ports) {

        List<V1ContainerPort> portList = ports.stream().map(port -> {

            String portString = ((JsonString) port).getString();

            String[] parts = portString.split(":");

            V1ContainerPortBuilder portBuilder = new V1ContainerPortBuilder();

            if (parts.length > 1) {
                portBuilder.withName(parts[0]);
                portBuilder.withContainerPort(Integer.parseInt(parts[1]));
            } else {
                portBuilder.withContainerPort(Integer.parseInt(parts[0]));
            }

            return portBuilder.build();
        }).collect(Collectors.toList());

        return portList;
    }

    public static List<V1EnvVar> getEnv(JsonObject envObject) {

        List<V1EnvVar> envList = new ArrayList<V1EnvVar>();
        Set<String> keys = envObject.keySet();

        keys.stream().forEach(key -> {
            envList.add(new V1EnvVarBuilder().withName(key).withValue(envObject.getString(key)).build());
        });

        envList.add(new V1EnvVarBuilder()
                .withName("NAMESPACE")
                .withValueFrom(new V1EnvVarSourceBuilder()
                        .withFieldRef(new V1ObjectFieldSelectorBuilder()
                                .withApiVersion("v1")
                                .withFieldPath("metadata.namespace")
                                .build())
                        .build())
                .build());

        return envList;
    }

    public static List<V1VolumeMount> getVolumeMounts(JsonObject fullJson) {

        List<V1VolumeMount> volumeMounts = new ArrayList<V1VolumeMount>();

        fullJson.getJsonArray(JsonKeys.VOLUMES_KEY).forEach(volume -> {

            String mountPath = ((JsonString) volume).getString();

            volumeMounts.add(new V1VolumeMountBuilder()
                    .withName(SHARED_VOLUME_NAME)
                    .withMountPath(mountPath)
                    .withSubPathExpr((getSubPathExpr(fullJson) + mountPath).replace("//", "/"))
                    .build());
        });

        return volumeMounts;
    }

    public static V1Volume getSharedVolume() {

        return new V1VolumeBuilder()
                .withName(SHARED_VOLUME_NAME)
                .withHostPath(new V1HostPathVolumeSourceBuilder()
                        .withPath(SystemUtil.getSharedVolumePath())
                        .withType("DirectoryOrCreate")
                        .build())
                .build();
    }

    public static String getSubPathExpr(JsonObject fullJson) {
        return "$(NAMESPACE)/" + fullJson.getString(JsonKeys.SERVICE_NAME_KEY) + "/";
    }


    public static JsonObject getJsonObject(String jsonString) {

        System.out.println(jsonString);

        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        return object;
}

    private BuilderUtil() {

    }
}
