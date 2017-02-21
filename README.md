# JSOM

Fluent, Java-style JSON view with all important methods from Map and List.

Works on arbitrary structures of Map<String, Object>, List<Object>, String,
Number, Boolean, and null. Functional methods (forEach etc.) have been
omitted in favor of Streams. Object insertion methods always unwrap Jsom
instances before inserting them.

The only possible exceptions are ClassCastExceptions and
NullPointerExceptions. If any of these are thrown, assumptions about the
given JSON structure were wrong.

Not thread-safe. Requires Java 8.

## Example Usage

```
import static de.mobilcom.jsom.Jsom.*;

// Create JSON views of existing Maps or Lists
Map<String,Object> foo = new HashMap<>();
List<Object> bar = new LinkedList<>();

Jsom fooJson = $(foo);
Jsom barJson = $(bar);

// Use known Map and List operations
// Chainable
fooJson.put("hello", "world").put("not", "bad");
barJson.add(1).add(2);

// Create a todo list
// list(item...) creates ArrayLists
// map() creates HashMaps
Jsom todo = list(
        map()
                .put("title", "learn java")
                .put("done", true)
                .put("year", 1999),
        map()
                .put("title", "get jsom")
                .put("done", false)
                .put("year", 2017),
        map()
                .put("title", "be awesome")
                .put("done", false)
);

// Count things to do using at() operation on list stream (sl)
assertEquals(2, todo.stream()
        .filter(item -> !item.get("done").toBoolean())
        .count());

// Get maximum year
int year = todo.stream()
        .filter(item -> !item.get("done").toBoolean())
        .mapToInt(item -> item.get("year").toInt())
        .max()
        .getAsInt();

// Find item containing awesomeness using a custom filter
Jsom awesome = todo.stream()
        .filter(item -> item.get("title").toString().contains("awesome"))
        .findFirst()
        .get();

// Check everything
todo.stream().forEach(item -> item.put("done", true));
```

## Parse and stringify

JSOM does not contain a parser or stringifier.
[Jackson](https://github.com/FasterXML/jackson) is recommended:

```
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static de.mobilcom.util.Jsom.*;

final ObjectMapper objectMapper = new ObjectMapper();
final TypeReference<HashMap<String, Object>> MAP_TYPE_REF
        = new TypeReference<HashMap<String, Object>>() {
};

// parse
Jsom in = $(objectMapper.readValue("{\"hello\":\"world\"}", MAP_TYPE_REF));

// stringify
String out = objectMapper.writeValueAsString(valueOf(in));
```

## Installation

Using Maven and JitPack:

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <!-- ... --->
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
    <dependencies>
        <dependency>
            <groupId>com.github.mobilcom-debitel</groupId>
            <artifactId>jsom</artifactId>
            <version>master-SNAPSHOT</version>
        </dependency>
    </dependencies>
</project>
```