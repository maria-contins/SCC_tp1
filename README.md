# House Rental Backend on Azure

This project implements a scalable backend for a house rental service using Azure cloud services. Users can list houses, rent houses, and manage related media. The system also supports house-specific questions and answers.

## Features
- **User Management**: Create, update, and delete users with associated media.
- **House Management**: List, create, update, and delete houses, with availability and pricing.
- **Rentals**: Manage rental records by period and price.
- **Questions**: Post and answer house-related questions.
- **Search**: Search houses by location, availability, and discounts.

## Technologies
- **Azure App Service**: REST API hosting.
- **Azure Blob Storage**: Media storage.
- **Azure Cosmos DB**: Data storage.
- **Azure Cache for Redis**: Application-level caching.
- **Azure Functions**: Periodic and reactive tasks.

## Testing
Automated testing with Artillery to validate all endpoints.

## Features
- Geo-replication.
- Advanced search using Azure Cognitive Search.
- Analytics with Apache Spark.

## Kubernetes Deployment (Part 2)

The project will also be deployed using Docker and Kubernetes to enhance scalability and manageability.

### Features
  - Deployed in Azure Kubernetes Service (AKS).
  - Uses Redis caching service in AKS.
  - Use a persistent volume for media data (replacing Blob Storage).
  - Replaced Cosmos DB with a database in AKS (mongo DB).
  - Replaced Azure Functions with HTTP servers and Kubernetes functions.
  - Deployed the test system in Azure Container Instances and collect results from clients in different data centers.
