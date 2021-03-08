# CertMe ( Quarkus-Extension )

#####  Automatically Generating Let's Encrypt / SelfSigned Certificate At Buildtime / Runtime
###### Based on Java 11 & ACME ( Automatic Certificate Management Environment ) Client API

1. **Maven Dependecy** : 

```
   <dependency>
     <groupId>com.github.rac021</groupId>
     <artifactId>certme-quarkus-extension</artifactId>
     <version>1.9</version>
   </dependency>
```

2. **Build Step ( for let's Encrypt, need to be sudo in order to use the port 80 )**

```bash

    cd ../my_quarkus_project/ && sudo mvn clean package 
    
    # Will Create a "CertMe" Directory with Let's Encrypt Or SelfSigned Certificate

```
3. **Run Step**

```bash

    java -jar target/quarkus-app/quarkus-run.jar
    
    Go to : https://localhost:8443/hello

```
4. **Supported Arguments :**

```
   -Dcertme_domain     : specify the domain. DEFAULT : Try to Resolve the Current Domain
   -Dcertme_interface  : Interface.          DEFAULT : 0.0.0.0
   -Dcertme_port       : Http Port Server.   DEFAULT : 80 ( Port Used By Let's Encrypt )
   -Dcertme_env        : DEV / PROD.         DEFAULT : DEV
   -Dcertme_file_name  : Certificate Name.   DEFAULT : "app" ( followed by "_domain-chain.crt" )
   -Dcertme_out_folder : Where the Certificates are generated. DEFAULT folder : certMe   
   -Dcertme_ignore     : if TRUE, CertMe will be ignored at Build / Runtime. DEFAULT : FALSE
   -Dcertme_force_gen  : if TRUE, it generate a new Certificate at Build / Runtime 
                         even if it exists. DEFAULT : FALSE 
```

5. **Build Step With Tests ( By Default, the tests are skiped ! )**

```bash

    cd quarkus-certme-extention/ && sudo mvn clean install -Dmaven.test.skip=false
```

#### Improvements
  - Embed Generated Let's Encrypt certificate at buildTime
  - Add support of dns-01 challenge
