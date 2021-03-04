# certme-quarkus-extension

#### Quarkus Extension for Automatically Generating Let's Encrypt Certificate At Buildtime ( java 11 )

1. Build Step ( need to be sudo in order to user the port 80 )
```bash

    cd   quarkus-certme-extention/ ; \
    sudo mvn clean install         ; \
    cd ../certme/                  ; \
    sudo mvn clean package 
```

2. Run Step
```bash

    java -jar target/certme-1.0-runner.jar

```


#### Improvements
  - Embed Generated Let's Encrypt certificate at buildTime
  - Add support of dns-01 challenge
