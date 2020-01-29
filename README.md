# Running your Spring Boot app on Kubernetes

This project describes how to run your Spring Boot app on Kubernetes.
You don't actually need to rewrite your app in order to target a K8s
cluster: Spring Boot can run on a large number of platforms, thanks to
the abstraction level it provides.

This app is made of a single REST controller:
```java
@RestController
class HelloController {
    @GetMapping("/")
    public Mono<String> greeting() {
        return Mono.just("Hello world!");
    }
}
```

## How to use it?

Compile this app using a JDK 8+:
```bash
$ ./mvnw clean package
```

You can run this app on your workstation:
```bash
$ java -jar target/spring-on-k8s.jar
```

The app is available at http://localhost:8080:
```bash
$ curl localhost:8080
Hello world!%
```

## Creating a Docker image

Our goal is to run this app in a K8s cluster: you first need to package
this app in a Docker image.

Here's a `Dockerfile` you can use:
```Dockerfile
# 1. First we build this app.
FROM adoptopenjdk:8-jdk-hotspot as BUILDER
RUN mkdir /build
ADD . /build
WORKDIR /build
# Use Maven wrapper script to build & test this app.
RUN ./mvnw -B clean package

# As this point the app is fully built & tested,
# and the artifact is waiting in /build/target.

# 2. We build the target image, containing the app artifact.
FROM adoptopenjdk:8-jre-hotspot
# We don't want to run this app as root, so let's create a new user.
RUN useradd -m -s /bin/bash app
USER app
# Copy app artifact from previous run.
COPY --from=BUILDER /build/target/spring-on-k8s.jar /home/app
# Let the JVM know we are running in a containerized environment.
ENTRYPOINT [ "java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/home/app/spring-on-k8s.jar" ]
```

Run this command to build this image:
```bash
$ docker build -t myrepo/spring-on-k8s .
```

You can now push this image to your favorite Docker registry:
```bash
$ docker push myrepo/spring-on-k8s
```

As an alternative, you can use [Cloud Native Buildpacks](https://buildpacks.io)
to build & deploy your Docker image.

Granted you have the [pack](https://github.com/buildpack/pack) 
CLI installed on your workstation, you can run this command
to build & deploy a Docker image:
```bash
$ pack build myrepo/spring-on-k8s --publish
```

This command will take care of building a Docker image containing
a base image, a JRE, and this image will be optimized (unzipped
JAR file, different layers for app/config/lib).

## Deploying to Kubernetes

This project includes Kubernetes descriptors, so you can easily deploy
this app to your favorite K8s cluster:
```bash
$ kubectl apply -f k8s
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
Hello world!%
```

## Contribute

Contributions are always welcome!

Feel free to open issues & send PR.

## License

Copyright &copy; 2020 [VMware, Inc](https://vmware.com).

This project is licensed under the [Apache Software License version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
