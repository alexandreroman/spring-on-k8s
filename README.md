# Running your Spring Boot app on Kubernetes

This project describes how to run your Spring Boot app on Kubernetes.
You don't actually need to rewrite your app in order to target a K8s
cluster: Spring Boot can run on many platforms, thanks to
the abstraction level it provides.

This app is made of a single REST controller:
```java
@RestController
class HelloController {
    @Value("${app.message:Hello world!}")
    private String message;

    @GetMapping(value = "/", produces = MediaType.TEXT_PLAIN_VALUE)
    String greeting() {
        // Just return a simple String.
        return message;
    }
}
```

## How to use it?

Compile this app using a JDK 21+:
```bash
$ ./mvnw clean package
```

You can run this app on your workstation:
```bash
$ ./mvnw spring-boot:run
```

The app is available at http://localhost:8080:
```bash
$ curl localhost:8080
Hello world!%
```

## Creating a Docker image

Our goal is to run this app in a K8s cluster: you first need to package
this app in a Docker image.

Use [Cloud Native Buildpacks](https://buildpacks.io)
to build & deploy your Docker image:
```bash
$ ./mvnw spring-boot:build-image
```

If you need to use a different image name, please edit `pom.xml`:

```xml
<plugin>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-maven-plugin</artifactId>
    <configuration>
        <image>
            <name>myrepo/spring-on-k8s</name>
        </image>
    </configuration>
</plugin>
```

This command will take care of building a Docker image containing
a base image, a JRE, and this image will be optimized (unzipped
JAR file, different layers for app/config/lib).

As you can see, you don't need a `Dockerfile` when using Cloud-Native Buildpacks
for building a Docker image: that's magic!

## Deploying to Kubernetes

This project includes Kubernetes descriptors, so you can easily deploy
this app to your favorite K8s cluster:
```bash
$ kubectl apply -k k8s/base
```

Using this command, monitor the allocated IP address for this app:
```bash
$ kubectl -n spring-on-k8s get svc
NAME     TYPE           CLUSTER-IP       EXTERNAL-IP     PORT(S)        AGE
app-lb   LoadBalancer   10.100.200.204   35.205.141.26   80:31633/TCP   90s
```

At some point, you should see an IP address under the column `EXTERNAL-IP`.

If you hit this address, you will get a greeting message from the app:
```bash
$ curl 35.205.141.26
Hello Kubernetes!%
```

You can also rely on your ingress controller to expose the app.
Edit the file [k8s/ingress/ingress.yml](k8s/ingress/ingress.yml)
and set the domain name accordingly.

Then run this command:

```bash
kubectl apply -k k8s/ingress
```

## Contributing

Contributions are always welcome!

Feel free to open issues & send PR.

## License

Copyright &copy; 2025 [Broadcom, Inc. or its affiliates](https://tanzu.vmware.com).

This project is licensed under the [Apache Software License version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
