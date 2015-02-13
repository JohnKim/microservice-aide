AIDE :: Sample Resource API Server
----

**This project is currently under development.**

curl -v -u sample-client-cli:AAAAAA123456 localhost:8088/oauth/token\?grant_type=client_credentials





curl localhost:8081/ping -H "Authorization: Bearer cc23ddd4-e47c-469f-9603-32fce81038f5"

pong

{"error":"invalid_token","error_description":"Access token expired: cc23ddd4-e47c-469f-9603-32fce81038f5"}

{"error":"invalid_token","error_description":"Invalid access token: cc23ddd4-e47c-469f-9603-32fce81038f5"}