For tire change servers:
https://github.com/Surmus/tire-change-workshop


If run localy:
Before run by docker, check servers ip gateways and opened ports 
that they are euqal to those that are in endpoints in file docker-compose.yaml - change if necessary.

docker inspect -f '{{range .NetworkSettings.Networks}}{{.Gateway}}{{end}}' container-id-here

when everything is ready:
```sh
docker-compose up --build 
```

frontend is reachable trough browser by link: 
http://localhost:9005/ 

WARNING
if run without docker, correct endpoints in 'src/resources/application.yml' and 'frontend/script.js'

Notes:
    At the moment - tests are ignored;