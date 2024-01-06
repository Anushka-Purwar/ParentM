# This is a complete microservice project that's build using java and spring-boot
### Pull code in your machine and follow instruction to run this project 
## >> Run this project using docker
1.Download docker desktop
2.Run `mvn clean package -DskipTests` to build the applications and create the docker image locally.
3.Run `docker-compose up -d` to start the applications.
## >> Run without using Docker
1.Run `mvn clean verify -DskipTests` by going inside each folder to build the applications.
2.After that `run mvn spring-boot:run` by going inside each folder to start the applications.
## >> To visualize using prometheus and grafana
1. Go to `localhost:9090` from your browser, then go to status>>Tragets. You should see your service conatiners running up.
2. Then go to `localhost:3000` from your browser >> add your first database using promethus. URL-> `http://promethus:9090` and click on save and test.
3. Then create a dashboard using template JSON and visulize your application.

