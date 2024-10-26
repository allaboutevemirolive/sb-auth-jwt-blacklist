# sb-auth-jwt-blacklist

JWT implementation with blacklisting using Redis.

Start Redis with docker executing:

```sh
docker run --name my-redis-container -p 7001:6379 -d redis
```

Import `yaak.blacklist-token.json` into yaak application or Postman. Then execute requests.
