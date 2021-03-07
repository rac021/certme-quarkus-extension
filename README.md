# certme-quarkus-extension

#### Quarkus Extension for Automatically Generating Let's Encrypt At Buildtime / SelfSigned Certificate ( java 11 )

1. **Maven Dependecy** : 

```
   <dependency>
     <groupId>com.github.rac021</groupId>
     <artifactId>certme-quarkus-extension</artifactId>
     <version>1.7</version>
   </dependency>
```

2. **Build Step ( for let's Encrypt, need to be sudo in order to use the port 80 )**

```bash

    cd ../my_project/ && sudo mvn clean package 

```
3. **Run Step**

```bash

    java -jar target/my_project-1.0-runner.jar
    
    Go to : https://localhost:8443/

```
4. **Supported Arguments :**

```
   -Dcertme_domain     : specify the domain. DEFAULT : Try to Resolve the Current Domain
   -Dcertme_interface  : Interface.          DEFAULT : 0.0.0.0
   -Dcertme_port       : Http Port Server.   DEFAULT : 80
   -Dcertme_env        : DEV / PROD.         DEFAULT : DEV
   -Dcertme_file_name  : Certificate Name.   DEFAULT : app ( followed by _domain-chain.crt )
   -Dcertme_out_folder : Where the Certificates are generated. DEFAULT folder : certMe
   -Dcertme_force_gen  : if TRUE, it generate the certificate even if it exists. DEFAULT : FALSE 
   -Dcertme_ignore     : if TRUE, Certificate Generation will be ignored. DEFAULT : FALSE
```

5. **Build Step With Tests ( By Default, the tests are skiped ! )**

```bash

    cd   quarkus-certme-extention/ ; \
    sudo mvn clean install -Dmaven.test.skip=false
```

#### Improvements
  - Embed Generated Let's Encrypt certificate at buildTime
  - Add support of dns-01 challenge
