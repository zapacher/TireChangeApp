
If run localy:
Before run by docker, check servers ip gateways and opened ports 
that they are euqal to those that are in docker-compose.yaml - change if necessary.

docker inspect -f '{{range .NetworkSettings.Networks}}{{.Gateway}}{{end}}' container-id-here

when everything is ready:
```sh
docker-compose up --build 
```

frontend is reachable trough browser by link: 
http://localhost:9005/ 