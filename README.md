```
export JAVA_HOME=/usr/lib/jvm/jdk1.8.0_74/
```

To generate this project
```sh
mvn archetype:generate  -DgroupId=ch.weetech -DartifactId=HttpClientLoadBalancer -DarchetypeArtifactId=maven-archetype-quickstart
```

How do I setup this project in eclipse?
just import this project into eclipse. File -> Import...

To build the jar
```sh
$ mvn package
```
