export PROJECT_ID=tldr-blog

./mvnw clean package
gcloud builds submit --tag gcr.io/${PROJECT_ID}/squoosh-quarkus
gcloud run deploy squoosh-quarkus --image gcr.io/${PROJECT_ID}/squoosh-quarkus --platform managed --region asia-southeast1 --allow-unauthenticated --port 8080