package hu.godenyd.k8s.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.stream.Stream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import hu.godenyd.k8s.converter.deployment.DeploymentBuilder;
import hu.godenyd.k8s.converter.service.ServiceBuilder;
import io.kubernetes.client.openapi.models.V1Deployment;
import io.kubernetes.client.openapi.models.V1Service;
import io.kubernetes.client.proto.V1Apps.DeploymentList;
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

        if (args.length == 0) {
            System.out.println("Please provide files to convert");
        }

        Stream.of(args).forEach(file -> {

            String YamlFileName = file;

            if (YamlFileName.endsWith(".json")) {
                YamlFileName = YamlFileName.replace(".json", ".yaml");
            }

            File jsonFile = new File(file);
            File yamlFile = new File(YamlFileName);

            try (FileWriter writer = new FileWriter(yamlFile)) {
                JsonObject object = getJsonFromFile(jsonFile);

                V1Service service = ServiceBuilder.buildService(object);
                V1Deployment deployment = DeploymentBuilder.buildDeployment(object);

                StringBuilder sb = new StringBuilder();
                
                sb.append(Yaml.dump(service));
                sb.append("---\n");
                sb.append(Yaml.dump(deployment));

                writer.write(sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private static JsonObject getJsonFromFile(File file) throws FileNotFoundException {

        try (JsonReader JsonReader = Json.createReader(new FileInputStream(file))) {

            JsonObject databaseObject = JsonReader.readObject();
            JsonReader.close();
            return databaseObject;
        }
    }
}
