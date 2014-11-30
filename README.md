log4j2-kafka
============

Kafka appender for Log4j2

##Maven
```xml
<dependency>
   <groupId>com.github.stuxuhai</groupId>
   <artifactId>log4j2-kafka</artifactId>
   <version>1.0</version>
</dependency>
```

##Usage
```xml
<Appenders>
    <Kafka name="kafka" topic="test">
        <Property name="metadata.broker.list">127.0.0.1:9092</Property>
        <Property name="serializer.class">kafka.serializer.StringEncoder</Property>
    </Kafka>
</Appenders>
```


