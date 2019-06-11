# 1. First build we build this app.
FROM adoptopenjdk:8-jdk-hotspot as BUILDER
RUN mkdir /build
ADD . /build
WORKDIR /build
# Use Maven wrapper script to build & test this app.
RUN ./mvnw -B clean package

# As this point the app is built & tested,
# and the artifact is available in /build/target.

# 2. We build the target image, containing the app artifact.
FROM adoptopenjdk:8-jre-hotspot
# We don't want to run this app as root, so let's create a new user.
RUN useradd -m -s /bin/bash app
USER app
# Copy the app artifact from the previous run.
COPY --from=BUILDER /build/target/spring-on-k8s.jar /home/app
# Let the JVM know we are running in a containerized environment.
ENTRYPOINT [ "java", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-jar", "/home/app/spring-on-k8s.jar" ]
