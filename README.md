# helidonse

Minimal Helidon SE project suitable to start from scratch.

## Build and run


With JDK17+
```bash
mvn package
java -jar target/helidonse.jar
```

## Exercise the application
```
curl -X GET http://localhost:8080/simple-greet
{"message":"Hello World!"}
```



## Using Micrometer

Access the `/micrometer` endpoint which reports the newly-added timer and counter.

```bash
curl http://localhost:8080/micrometer
```
`SimpleGreetService` has a micrometer counter that is incremented each time a GET request is made at
`http://localhost:8080/greet-count`. The counter name is `allRequests` and is shown in the console
with the number of time it was triggered.

```
curl http://localhost:8080/micrometer
# HELP allRequests_total
# TYPE allRequests_total counter
allRequests_total 0.0
```


## Try metrics

```
# Prometheus Format
curl -s -X GET http://localhost:8080/metrics
# TYPE base:gc_g1_young_generation_count gauge
. . .

# JSON Format
curl -H 'Accept: application/json' -X GET http://localhost:8080/metrics
{"base":...
. . .
```



## Building a Native Image

Make sure you have GraalVM locally installed:

```
$GRAALVM_HOME/bin/native-image --version
```

Build the native image using the native image profile:

```
mvn package -Pnative-image
```

This uses the helidon-maven-plugin to perform the native compilation using your installed copy of GraalVM. It might take a while to complete.
Once it completes start the application using the native executable (no JVM!):

```
./target/helidonse
```

Yep, it starts fast. You can exercise the application’s endpoints as before.


## Building the Docker Image

```
docker build -t helidonse .
```

## Running the Docker Image

```
docker run --rm -p 8080:8080 helidonse:latest
```

Exercise the application as described above.
                                
