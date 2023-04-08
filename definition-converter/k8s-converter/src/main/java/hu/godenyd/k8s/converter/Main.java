package hu.godenyd.k8s.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.json.Json;
import javax.json.JsonObject;

import hu.godenyd.k8s.converter.deployment.DeploymentBuilder;
import hu.godenyd.k8s.converter.service.ServiceBuilder;
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

    public static void main(String... args) {

        javax.json.JsonReader JsonReader;
        try {
            JsonReader = Json.createReader(new FileInputStream(new File(
                    "/home/davidkaa/Szakdoga/definition-converter/k8s-converter/src/main/java/resources/test.json")));
            JsonObject databaseObject = JsonReader.readObject();
            JsonReader.close();

            System.out.println(Yaml.dump(ServiceBuilder.buildService(databaseObject)));
            System.out.println(Yaml.dump(DeploymentBuilder.buildDeployment(databaseObject)));


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
