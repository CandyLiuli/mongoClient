# mongoClient

## usage

----------------------------------

```
java -classpath ../lib/mongo-java-driver-3.4.0.jar:../lib/main.jar Main ../src/config.properties
```
----------------------------------


## commands
----------------------------------

* 1. list dbs
```
dbs
```

* 2. list collections
```
collections
```

* 3. query collection size
```
collection size
```

* 4. query collection all
```
collection all
```
 
* 5. query collection by key-value
```
collection key value
```

* 6. query collection by regex pattern
```
collection key pattern reg
```

* 7. writeKeyValue to collection
```
collection key value writekeyvalue
```

* 8. write json to collection
```
collection json write
```

* 9. delete collection by key-value
```
collection key value delete
```

* 10. quit
```
q (or quit exit)
```

----------------------------------


## compile
----------------------------------
* 1. compile classes
```
cd src
javac -classpath ../lib/mongo-java-driver-3.4.0.jar Main.java
```

* 2. create jar
```
jar cvf ../lib/main.jar Main*.class
```

* 3. run
```
java -classpath ../lib/mongo-java-driver-3.4.0.jar:../lib/main.jar Main config.properties
```
----------------------------------


# log store
----------------------------------
```
java -classpath ../lib/mongo-java-driver-3.4.0.jar:../lib/morphia-1.3.1.jar:../lib/main.jar Store config.properties
```
----------------------------------

