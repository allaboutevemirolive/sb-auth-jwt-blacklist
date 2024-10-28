# sb-auth-jwt-blacklist

This project provides a JWT implementation with blacklisting using Redis.

**Getting Started:**

1. **Start Redis using Docker:**

   Ensure compatibility by checking the `pom.xml` file (I am using JDK 21). Then, execute the following command to start a Redis container:

   ```sh
   docker run --name my-redis-container -p 7001:6379 -d redis
   ```

2. **Run the application:**

   ```sh
   mvn clean spring-boot:run
   ```

3. **Test the implementation:**

   Import the `scripts/yaak.blacklist-token.json` file into your Yaak application or Postman. Then, execute the requests defined in the file. 


