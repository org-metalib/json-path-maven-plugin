package org.metalib.maven.plugin.json.path;

import com.jayway.jsonpath.JsonPath;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

public class JsonPathMojoTest {

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {}
        @Override
        protected void after() {}
    };

     static final String jsonText = """
            {
                "foo": "bar",
                "bar": {
                    "baz": "qux"
                }
            }
            """;

    @Test
    public void testSomething() throws Exception {
        final var pom = new File( "target/test-classes/project-to-test/" );
        assertNotNull( pom );
        assertTrue( pom.exists() );

        final var secretSetMojo = (JsonPathMojo) rule.lookupConfiguredMojo( pom, "paths");
        assertNotNull(secretSetMojo);

        secretSetMojo.execute();
    }

    @Test
    public void test() {
        final var value = JsonPath.parse(jsonText).read("$.foo", String.class);
        assertEquals("bar", value);
    }
}