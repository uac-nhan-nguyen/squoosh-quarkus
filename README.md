# Squoosh API with quarkus and kotlin

## Development

Start dev mode
```shell script
./mvnw compile quarkus:dev
```

## Build 

1- Package
```shell
./mvnw clean package
```

2- Build Docker image
```shell
docker build -t squoosh-quarkus . --progress=plain --no-cache
```

3- Run Docker image
```shell
docker run -p 8080:8080 squoosh-quarkus
```

## Deployment

Run deploy script `./deploy.sh` to deploy to GCP Cloud Run
```shell
export PROJECT_ID=tldr-blog

./mvnw clean package
gcloud builds submit --tag gcr.io/${PROJECT_ID}/squoosh-quarkus
gcloud run deploy squoosh-quarkus --image gcr.io/${PROJECT_ID}/squoosh-quarkus --platform managed --region asia-southeast1 --allow-unauthenticated --port 8080
```

## Related Guides

- If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin

- [Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
