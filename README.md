# json-path maven plugin

A Maven plugin that wraps the [JsonPath](https://github.com/json-path/JsonPath) Java library, enabling JSON parsing 
and querying directly within Maven builds.

## Features

- Parse JSON files or inline JSON strings.
- Extract values from JSON using JsonPath expressions.
- Automatically update `.env` files with extracted values.
- Automatically update `.gitignore`

## Requirements

- Java 21 or higher
- Maven 3.9.9 or higher

## Installation

To use the plugin, add the following to your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.metalib.maven.plugin</groupId>
            <artifactId>json-path-maven-plugin</artifactId>
            <version>0.0.3</version>
        </plugin>
    </plugins>
</build>
```

## Usage

### Configuration

The plugin accepts an array of `JsonInput` objects via the `paths` parameter. Each `JsonInput` object can specify:

- `format`: The format of the input (e.g., JSON).
- `text`: Inline JSON content.
- `file`: A file containing JSON content.
- `queryMaps`: A set of key-value pairs where the key is the property name and the value is the JsonPath query.

`text` and `file` parameters are mutually exclusive.

### Example

Here is an example configuration in your `pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.metalib.maven.plugin</groupId>
            <artifactId>json-path-maven-plugin</artifactId>
            <version>0.0.3</version>
            <configuration>
                <paths>
                    <path>
                        <file>json-text.json</file>
                        <queryMaps>
                            <FOO>$.foo</FOO>
                            <BAR_BAZ>$.bar.baz</BAR_BAZ>
                        </queryMaps>
                    </path>
                </paths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

with the sample input file `json-text.json`:
```json
{
  "foo": "bar",
  "bar": {
    "baz": "qux"
  }
}
```

### Goals

#### `paths`

This goal processes the JSON inputs and performs the following:

1. Extracts values from JSON using the specified JsonPath queries.
2. Updates the `.env` file in the project directory with the extracted values.
3. Ensures the `.env` file is listed in `.gitignore` to prevent accidental commits.

### Running the Plugin

To execute the plugin, run:
```shell
mvn jq:paths
```

It will create or update `.env` as shown: 
```properties
#Wed Apr 23 16:58:10 CDT 2025
BAR_BAZ=qux
FOO=bar
```

It will make sure that there is a  
```properties
### protecting .env file ###
.env
```

## References

- [JsonPath Library](https://github.com/json-path/JsonPath)
- [Maven Plugin Development Guide](https://maven.apache.org/guides/plugin/guide-java-plugin-development.html)

## License

This project is licensed under the Apache License, version 2.0. See the [LICENSE](LICENSE) file for details.
