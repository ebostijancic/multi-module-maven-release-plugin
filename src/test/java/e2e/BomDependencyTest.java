package e2e;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import scaffolding.MvnRunner;
import scaffolding.TestProject;

import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static scaffolding.ExactCountMatcher.oneOf;
import static scaffolding.GitMatchers.hasTag;

@Ignore("WIP")
public class BomDependencyTest {

    final TestProject testProject = TestProject.dependencyManagementProject();



    @BeforeClass
    public static void installPluginToLocalRepo() throws MavenInvocationException {
        MvnRunner.installReleasePluginToLocalRepo();
    }

    @Test
    @Ignore
    public void nextWillReleasedAllWhenBomIsChanged() throws Exception {
        //Workaround to first release the bom as the module releasing is not working
        List<String> bomReleaseLog = testProject.mvnRelease("50","root-bom");
        System.out.println(bomReleaseLog);

        final List<String> projectFirstReleaseLogs = testProject.mvnRelease("1");
        // System.out.println(projectFirstReleaseLogs);

        testProject.commitRandomFile("root-bom").pushIt();
        testProject.commitRandomFile("console-app").pushIt();
        List<String> output = testProject.mvnReleaserNext("2");
        assertTagDoesNotExist("console-app-3.2.2");
        assertTagDoesNotExist("parent-module-1.2.3.2");
        assertTagDoesNotExist("core-utils-2.0.2");
        assertTagDoesNotExist("more-utils-10.0.2");
        assertTagDoesNotExist("deep-dependencies-aggregator-1.0.2");
//
        System.out.println(output);
//
        assertThat(output, oneOf(containsString("[INFO] Releasing parent-module 1.2.3.2 as root-bom has changed.")));
        assertThat(output, oneOf(containsString("[INFO] Releasing more-utils 10.0.2 as parent-module has changed.")));
        assertThat(output, oneOf(containsString("[INFO] Releasing core-utils 2.0.2 as more-utils has changed.")));
        assertThat(output, oneOf(containsString("[INFO] Releasing console-app 3.2.2 as core-utils has changed.")));
        assertThat(output, oneOf(containsString("[INFO] Will use version 1.0.1 for dependencymanagement-aggregator as it has not been changed since that release.")));
    }

    @Test
    public void willReleasedAllWhenBomIsChanged() throws Exception {
        //Workaround to first release the bom as the module releasing is not working
       // List<String> bomReleaseLog = bomProject.mvnRelease("0");
        // System.out.println(bomReleaseLog);

        final List<String> projectFirstReleaseLogs = testProject.mvnRelease("1");
        System.out.println(projectFirstReleaseLogs);

        testProject.commitRandomFile("root-bom").pushIt();
        testProject.commitRandomFile("console-app").pushIt();
        List<String> output = testProject.mvnRelease("2");
      //  assertTagDoesNotExist("console-app-3.2.2");
      //  assertTagDoesNotExist("parent-module-1.2.3.2");
      //  assertTagDoesNotExist("core-utils-2.0.2");
     //   assertTagDoesNotExist("more-utils-10.0.2");
     //   assertTagDoesNotExist("deep-dependencies-aggregator-1.0.2");
//
        System.out.println(output);
//
        assertThat(output, oneOf(containsString("[INFO] Releasing parent-module 1.2.3.2 as root-bom has changed.")));
        assertThat(output, oneOf(containsString("[INFO] Releasing more-utils 10.0.2 as parent-module has changed.")));
        assertThat(output, oneOf(containsString("[INFO] Releasing core-utils 2.0.2 as more-utils has changed.")));
        assertThat(output, oneOf(containsString("[INFO] Releasing console-app 3.2.2 as core-utils has changed.")));
        assertThat(output, oneOf(containsString("[INFO] Will use version 1.0.1 for dependencymanagement-aggregator as it has not been changed since that release.")));
    }

    private void assertTagDoesNotExist(String tagName) {
        assertThat(testProject.local, not(hasTag(tagName)));
        assertThat(testProject.origin, not(hasTag(tagName)));
    }

    private void assertTagExists(String tagName) {
        assertThat(testProject.local, hasTag(tagName));
        assertThat(testProject.origin, hasTag(tagName));
    }

}
