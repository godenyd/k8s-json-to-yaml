package com.example.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.json.Json;

import org.junit.jupiter.api.Test;

import hu.godenyd.k8s.converter.util.BuilderUtil;
import io.kubernetes.client.openapi.models.V1ContainerPort;
import io.kubernetes.client.openapi.models.V1ContainerPortBuilder;
import io.kubernetes.client.openapi.models.V1ServicePort;
import io.kubernetes.client.openapi.models.V1ServicePortBuilder;
import io.kubernetes.client.util.Yaml;

public class PortBuilderTest {

    @Test
    public void testAnonymServicePort() {
        V1ServicePortBuilder portBuilder = new V1ServicePortBuilder();

        List<V1ServicePort> port = List.of(portBuilder.withPort(1234).build());

        List<V1ServicePort> generatedPort = BuilderUtil.getServicePorts(Json.createArrayBuilder().add("1234").build());

        assertEquals(Yaml.dump(port), Yaml.dump(generatedPort));
    }

    @Test
    public void testNamedServicePort() {
        V1ServicePortBuilder portBuilder = new V1ServicePortBuilder();

        List<V1ServicePort> port = List.of(portBuilder.withName("Testing").withPort(1234).build());

        List<V1ServicePort> generatedPort = BuilderUtil
                .getServicePorts(Json.createArrayBuilder().add("Testing:1234").build());

        assertEquals(Yaml.dump(port), Yaml.dump(generatedPort));
    }

    @Test
    public void testMultipleServicePorts() {

        List<V1ServicePort> port = List.of(new V1ServicePortBuilder().withName("Testing").withPort(1234).build(),
                new V1ServicePortBuilder().withPort(5678).build(),
                new V1ServicePortBuilder().withName("something").withPort(123).build(),
                new V1ServicePortBuilder().withPort(555).build());

        List<V1ServicePort> generatedPort = BuilderUtil.getServicePorts(
                Json.createArrayBuilder().add("Testing:1234").add("5678").add("something:123").add("555").build());

        assertEquals(Yaml.dump(port), Yaml.dump(generatedPort));
    }

    @Test
    public void testAnonymContainerPort() {
        V1ContainerPortBuilder portBuilder = new V1ContainerPortBuilder();

        List<V1ContainerPort> port = List.of(portBuilder.withContainerPort(1234).build());

        List<V1ContainerPort> generatedPort = BuilderUtil
                .getContainerPorts(Json.createArrayBuilder().add("1234").build());

        assertEquals(Yaml.dump(port), Yaml.dump(generatedPort));
    }

    @Test
    public void testNamedContainerPort() {
        V1ContainerPortBuilder portBuilder = new V1ContainerPortBuilder();

        List<V1ContainerPort> port = List.of(portBuilder.withName("Testing").withContainerPort(1234).build());

        List<V1ContainerPort> generatedPort = BuilderUtil
                .getContainerPorts(Json.createArrayBuilder().add("Testing:1234").build());

        assertEquals(Yaml.dump(port), Yaml.dump(generatedPort));
    }

    @Test
    public void testMultipleContainerPorts() {

        List<V1ContainerPort> port = List.of(
                new V1ContainerPortBuilder().withName("Testing").withContainerPort(1234).build(),
                new V1ContainerPortBuilder().withContainerPort(5678).build(),
                new V1ContainerPortBuilder().withName("something").withContainerPort(123).build(),
                new V1ContainerPortBuilder().withContainerPort(555).build());

        List<V1ContainerPort> generatedPort = BuilderUtil.getContainerPorts(
                Json.createArrayBuilder().add("Testing:1234").add("5678").add("something:123").add("555").build());

        assertEquals(Yaml.dump(port), Yaml.dump(generatedPort));
    }

}
