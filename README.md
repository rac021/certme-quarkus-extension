# certme-quarkus-extension

#### Quarkus Extension for Automatically Generating Let's Encrypt Certificate At Buildtime ( java 11 )

1. Build Step ( need to be sudo in order to use the port 80 )

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
3. Supported Arguments : 

```
   -Dcertme_domain     : specify the domain. Default : Resolve the Current Domain
   -Dcertme_interface  : Interface.          Default : 0.0.0.0
   -Dcertme_port       : Http Port Server.   Default : 80
   -Dcertme_staging    : DEV / PROD.         Default : DEV
   -Dcertme_force_gen  : if TRUE, it generate the certificate even it exists. Default : false 
   -Dcertme_out_folder : Where the Certificates are generated. Default folder : certMe
   -Dcertme_file_name  : Certificate Name.   Default : app ( followed by _domain-chain.crt )
   -Dcertme_ignore     : if TRUE, Certificate Generation will be ignored. Default : FALSE
```

4. Build Step With Tests ( By Default, the tests are skiped ! )

```bash

    cd   quarkus-certme-extention/ ; \
    sudo mvn clean install -Dmaven.test.skip=false
```

#### Improvements
  - Embed Generated Let's Encrypt certificate at buildTime
  - Add support of dns-01 challenge
