package pl.marian.consul.maven;

import com.pszymczyk.consul.junit.ConsulResource;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.apache.maven.project.MavenProject;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import pl.marian.consul.maven.test.ConsulTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class PropertiesExportMojoTest {

    @ClassRule
    public static final ConsulResource consul = new ConsulResource();

    @Rule
    public MojoRule rule = new MojoRule();

    @Rule
    public TestResources resources = new TestResources();

    private ConsulTestUtil consulTestUtil = new ConsulTestUtil(consul.getHttpPort());

    @Test
    public void propertiesExportOnValidProject() throws Exception {
        executeMojo("sample-01");

        assertThat(consulTestUtil.findAllProperties("config/app"))
                .containsOnly("config/app/prop01.value", "config/app/prop02.name", "config/app/feature01.config");

        assertThat(consulTestUtil.loadPropertyValueFromConsul("config/app", "prop01.value")).isEqualTo("123");
        assertThat(consulTestUtil.loadPropertyValueFromConsul("config/app", "prop02.name")).isEqualTo("CustomName");
        assertThat(consulTestUtil.loadPropertyValueFromConsul("config/app", "feature01.config")).isEqualTo("11");
    }

    private void executeMojo(String project) throws Exception {
        MavenProject mvnProject = rule.readMavenProject(this.resources.getBasedir(project));
        MavenSession mvnSession = rule.newMavenSession(mvnProject);
        mvnSession.getSystemProperties().setProperty("consulPort", Integer.toString(consul.getHttpPort()));
        MojoExecution execution = rule.newMojoExecution("propertiesExport");
        rule.executeMojo(mvnSession, mvnProject, execution);
    }

}