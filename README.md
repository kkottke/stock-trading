# Stock Trading Simulation
Simple stock trading simulation to show certain Vert.x features.
 
## Build
 
```bash 
mvn clean install
```

## Start Application

### Start Elastic Stack

```bash 
docker-compose -f docker-compose.yml up -d
```

### Start Quote Generator

```bash 
java -jar quote-generator/target/quote-generator.jar
```

### Start Trading Service

```bash 
java -jar trading-service/target/trading-service.jar
```

### Start Audit Service

```bash 
java -jar audit-service/target/audit-service.jar
```

### Start Traders

```bash 
java -jar traders/target/traders.jar
```