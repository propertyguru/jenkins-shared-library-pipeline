package org.stages

import org.common.Blueprint
import org.common.DockerClient
import org.common.Git
import org.common.StepExecutor

class Sonarqube extends Base {

    Sonarqube() {
        super()
        this.stage = "Sonarqube"
    }

    @Override
    def body() {
        String name
        String tag

        this.step("Building dockerfile sonarqube target", {
            tag = Git.getSHA()
            name = Blueprint.component() + "/" + Blueprint.subcomponent() + ":" + tag
            DockerClient.build(
                    name,
                    Blueprint.dockerfile(),
                    Blueprint.dockerArgs(),
                    "sonarqube",
                    "sonarqube"
            )
        })
        this.step("sonarqube", {
            String sonarPath
            sonarPath = StepExecutor.shWithOutput("pwd").trim()
            sonarPath = "${sonarPath}/sonarqube/app"
            StepExecutor.sh("docker run --rm -e SONAR_HOST_URL='http://sonarqube.guruestate.com:9000' " +
                    "-e SONAR_PROJECT_BASE_DIR='/app' -e sonar.scm.provider=git -v ${sonarPath}:/app " +
                    "sonarsource/sonar-scanner-cli:4.4")
            // TODO: see if this can be inside try/catch
            // cleanup
            StepExecutor.sh("rm -rf ./out")
        })
    }

    @Override
    Boolean skip() {
        // skip this stage if sonar-project.properties file do not exist.
        if (StepExecutor.fileExists("sonar-project.properties")) {
            return false
        }
        // skip this stage if we are not deploying on integration and hotfix is not checked.
        if (!('integration' in StepExecutor.env('ENVIRONMENT').tokenize(',')) &&
                StepExecutor.env('HOTFIX') == "false") {
            return true
        }
        return true
    }
}
