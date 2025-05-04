package org.metalib.maven.plugin.json.path.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.File;
import java.util.Properties;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class JsonInput {
    String format;
    String text;
    File file;
    Properties queryMaps;
}
