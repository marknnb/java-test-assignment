## How to runt the project

-  Import the  project in intellij or any java IDE
-  run the command (assumption: maven is installed in the system or maven wrapper in the project structure can be used to invoke this command)
```
mvn clean install
```
-  open terminal in project directory 

- run the command (assumption: docker and docker compose is installed)
``` 
 docker compose up -d 
```

---

## How to test the project

-  In project base directory find postman collection under documents directory
- Export the collection into postman
- Postman consist of sample payloads and sample responses from the application

---