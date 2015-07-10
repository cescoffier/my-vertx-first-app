# My First Vert.x 3 Application

This project is a very simple Vert.x 3 application and contains some explaination on how this application is built 
and tested.

## Building

You build the project using:

```
mvn clean package
```

## Testing

The application is tested using [vertx-unit](http://vertx.io/docs/vertx-unit/java/).

## Packaging

The application is packaged as a _fat jar_, using the 
[Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/).

## Running

Once packaged, just launch the _fat jar_ as follows:

```
java -jar target/my-first-app-1.0-SNAPSHOT-fat.jar
```

Then, open a browser to http://localhost:8080.

