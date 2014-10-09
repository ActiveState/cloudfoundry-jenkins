package org.activestate.stackatojenkins;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DeploymentInfoTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetApplicationInfo() throws Exception {
        File manifest = new File(getClass().getResource("hello-java-manifest.yml").toURI());
        ManifestReader reader = new ManifestReader(manifest);
        Map<String, Object> result = reader.getApplicationInfo();
        assertEquals(result.get("name"), "hello-java");
        assertEquals(result.get("memory"), "512M");
        assertEquals(result.get("path"), "target/hello-java-1.0.war");
    }

    @Test
    public void testGetApplicationInfoEnvVars() throws Exception {
        File manifest = new File(getClass().getResource("env-vars-manifest.yml").toURI());
        ManifestReader reader = new ManifestReader(manifest);
        Map<String, Object> result = reader.getApplicationInfo();
        assertEquals(result.get("name"), "hello-java");
        assertEquals(result.get("memory"), "512M");
        assertEquals(result.get("path"), "target/hello-java-1.0.war");
        @SuppressWarnings("unchecked")
        Map<String, String> envVars = (Map<String, String>) result.get("env");
        assertEquals(envVars.get("ENV_VAR_ONE"), "value1");
        assertEquals(envVars.get("ENV_VAR_TWO"), "value2");
        assertEquals(envVars.get("ENV_VAR_THREE"), "value3");
    }

    @Test
    public void testGetApplicationInfoServicesNames() throws Exception {
        File manifest = new File(getClass().getResource("services-names-manifest.yml").toURI());
        ManifestReader reader = new ManifestReader(manifest);
        Map<String, Object> result = reader.getApplicationInfo();
        assertEquals(result.get("name"), "hello-java");
        assertEquals(result.get("memory"), "512M");
        assertEquals(result.get("path"), "target/hello-java-1.0.war");
        @SuppressWarnings("unchecked")
        List<String> servicesNames = (List<String>) result.get("services");
        assertTrue(servicesNames.contains("service1"));
        assertTrue(servicesNames.contains("service2"));
        assertTrue(servicesNames.contains("service3"));
    }

    @Test
    public void testGetApplicationInfoMalformedYML() throws Exception {
        exception.expect(ManifestParsingException.class);
        exception.expectMessage("Malformed YAML file");
        File manifest = new File(getClass().getResource("malformed-manifest.yml").toURI());
        new ManifestReader(manifest);
    }

    @Test
    public void testGetApplicationInfoNotAMap() throws Exception {
        exception.expect(ManifestParsingException.class);
        exception.expectMessage("Could not parse the manifest file into a map");
        File manifest = new File(getClass().getResource("not-a-map-manifest.yml").toURI());
        new ManifestReader(manifest);
    }

    @Test
    public void testGetApplicationInfoNoApplicationBlock() throws Exception {
        exception.expect(ManifestParsingException.class);
        exception.expectMessage("Manifest file does not start with an 'applications' block");
        File manifest = new File(getClass().getResource("no-application-block-manifest.yml").toURI());
        new ManifestReader(manifest);
    }

    @Test
    public void testGetApplicationInfoWrongAppName() throws Exception {
        exception.expect(ManifestParsingException.class);
        exception.expectMessage("Manifest file does not contain an app named goodbye-java");
        File manifest = new File(getClass().getResource("hello-java-manifest.yml").toURI());
        ManifestReader reader = new ManifestReader(manifest);
        reader.getApplicationInfo("goodbye-java");
    }
}
