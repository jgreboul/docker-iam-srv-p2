# docker-iam-srv-p2

Welcome to part 2 of my MicroService Tutorial! 

In this repository, you'll find the definition of docker container running a Spring Boot Framework web application.

This application allows the user to interact with the mySQL iamDB database running in the iamDB Docker container we discussed in the first part of this tutorial. For more information, see https://github.com/jgreboul/docker-iam-db. 

The iam.service (version 0.0.1) can:
1) Create a Permission record in the mySQL iamDB database 
2) Update the Description of a Permission record
3) Archive or Un-archive a Permission record
4) Delete a Permission record
5) Gets a summary view of a Permission record
6) Gets a detail view of a Permission record
7) Gets a list of all Permission records (summary view for each Permission)
8) Gets a list of all Permission records (detail view for each Permission)
9) Create a metadata record for a Permission record
10) Update a metadata value for a Permission record
11) Delete a metadata value for a Permission record

# Task Two: Implement a Webservice on top the mySQL iamDB database  

Here are the step-by-step instructions:

## Step 1: Download and Install all pre-requisites
1. Docker: https://www.docker.com/products/docker-desktop/
2. Github Desktop: https://github.com/apps/desktop
3. IntelliJ IDEA: https://www.jetbrains.com/idea/download
4. Postman: https://www.postman.com/downloads/ 

## Step 2: Clone this repository
1. Open Github Desktop
2. Clone this repo...

## Step 3: Explore the code
1. Open and navigate the `docker-iam-srv-p2` folder
2. Below is the brief explanation of  structure of the code:

* src/main include the iam.service web application (version 0.0.1).
    * java
      * jgr
        * iam
          * constant: constant values
          * controller: **Controller** classes handling the users requests
          * enums: enumeration objects 
          * manager: classes handling the high-level interaction with the iamDB database, through the repository classes
          * model: **Model**
            * bo: business objects
            * dto: data transfer objects
          * payload : request/response objects **View**
            * request
              * metadata: payload request classes for the Metadata business object
              * permission: payload request classes for the Permission business object
            * response objects
              * metadata: payload response classes for the Metadata business object
              * permission: payload response classes for the Permission business object
            * repository: low-level classes accessing the iamDB database
            * service: service interface definition **Business Logic**
              * impl: service classes implementing the business logic
            * util: utility classes
    * iamMain: main class
* src/test  include all unit tests, providing 100%-method and 100$line code coverage. The implemented unit tests  leverages both mocks and end-to-end/integrated unit tests.
    * java
        * jgr
            * iam
              * constant: constant values used by unit tests
              * controller: end-to-end and mock unit tests for the RootController and PermissionController classes
              * manager: mock unit tests for both BaseManager and PermissionManager classes
              * model
                * bo: simple unit tests for all business object classes 
                * dto: simple unit test for the MetadataDTO class (*not all DTO classes required unit tests to ensure 100& test coverage, but in the real world, you may want to have unit tests for all classes*)
              * payload
                * request: simple unit test for ObjectRequest class (*same comment as above*)
              * repository: end-to-end and mock unit tests for the Repository classes
              * service: mock unit tests for the PermissionService classe
              * util: include unit tests for the iamDBConnectorUtil class as well utility classes leveraged by the unit tests. 

## Step 4: Compile the iam.service application
1. Open the `docker-iam-srv-p2` project in your IDE (e.g.: IntelliJ)
3. Identify the Java Maven build control panel in your IDE
3. Perform a Clean build using Maven
4. Perform a Compile build using Maven

## Step 5: Run the docker-iam-db container in the iam-service-network docker network
1. Go back to the `docker-iam-db` folder you created in Part 1 of this tutorial.
2. Open the README.md file in `docker-iam-db` folder.
3. Follow the instructions provided in Step 6: Create a Network.
4. Follow the instructions provided in Step 7.2: Run the MySQL Container.
5. Follow the instructions provided in Step 8: Verify the Container.
6. Follow the instructions provided in Step 9: Test the Sample Database. 
7. At this point, the `docker-iam-db` container should be running and you can successfully connect and query the iamDB database.

## Step 6.1 (IDE option): CLean, Compile, Verify and Package the iam.service application
1. Perform a Test build using Maven - this will run the 350 unit tests
2. Perform a Install build using Maven
3. Check the `iam.service-0.0.1.jar` has been generated in the target folder.

## Step 6.2 (Command Line option): CLean, Compile, Verify and Package the iam.service application
1. Open a terminal
2. Navigate to the `docker-iam-srv-p2` folder
3. Execute a clean build: `mvn clean install spring-boot:repackage`
4. Validate all unit tests passed
5. Validate the build complete successfully
6. Check the `iam.service-0.0.1.jar` has been generated in the target folder.

## Step 6: Review the Dockerfile for IAM Service docker image
1. Navigate the `docker-iam-srv-p2` folder
2. Open the `Dockerfile` file.
3. This *first iteration* docker image has two defined layers
   * The first layer of our image (openjdk:22-jdk-slim) is a slim debian linux environment with jdk 22
   * The second layer of our image consists in copying `iam.service-0.0.1.jar` into the /app folder

## Step 7: Build the Docker image 
1. Open a terminal and navigate to the `docker-iam-srv-p2` directory.
2. Run the following command to build the Docker image:
   `docker build -t docker-iam-srv-p2 .`
3. Check the created image: `docker image ls`
4. Note the IMAGE ID

## Step 8: Run the IamService Container
1. Once the Docker image is built, run the container using the following command:
   `docker run -d --name docker-iam-srv-p2 --network iam-service-network --env-file .env -p 8080:8080 docker-iam-srv-p2`

## Step 9: Verify the Container
1. Check the status of the running container using the following command: `docker ps -a`
2. You should see the `docker-iam-srv-p2` listed among the running containers.
3. Note the CONTAINER ID

## Step 10: Test your Application Database
1. Open your browser and navigate to the following url: `http://localhost:8080/`
2. Validate you're getting the following response: `"{"status":"NOT_FOUND","response":"content-not-found","count":0}"`
3. Open Postman
4. Import the postman collection `IAM Service (part 2).postman_collection.json`
5. Try the different APIs
6. Don't forget to delete any created Permission or Metadata.

## Step 11: Clean-up!
1. Kill your running docker: `docker kill CONTAINER ID`
2. Remove the container: `docker rm CONTAINER ID`
3. Remove the image: `docker rmi IMAGE ID`
4. Remove the network: `docker network rm  NETWORK ID`

For more insights, subscribe to my Youtube Channel: https://www.youtube.com/user/jgreboul
Thank you,
Jean-Gael Reboul
jgreboul@gmail.com