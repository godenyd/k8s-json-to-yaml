package hu.godenyd.k8s.converter;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;

import hu.godenyd.k8s.converter.deployment.DeploymentBuilder;
import hu.godenyd.k8s.converter.service.ServiceBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;
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
        V1Service service = ServiceBuilder.buildService(name, ports);

        // Build Deployment

        V1Deployment deployment = DeploymentBuilder.buildDeployment("sample-namespace", name, image, ports, volumes);

        System.out.println(Yaml.dump(service));
        System.out.println(Yaml.dump(deployment));

    }
}
