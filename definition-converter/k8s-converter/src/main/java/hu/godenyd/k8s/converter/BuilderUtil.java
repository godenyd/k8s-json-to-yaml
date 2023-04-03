package hu.godenyd.k8s.converter;

import java.util.List;
import java.util.stream.Collectors;

import javax.json.JsonArray;

import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1ContainerPortBuilder;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServicePortBuilder;

public class BuilderUtil {
    
    public static List<V1ServicePort> getServicePorts(JsonArray ports) {

        List<V1ServicePort> portList = ports.stream().map(port -> {
            
            String portString = port.toString().replace("\"", "");
            String name = "-";
            int portNumber;

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
            
            String portString = port.toString().replace("\"", "");
            String name = "-";
            int portNumber;

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

    private BuilderUtil() {

    }
}
