package org.stages

import org.common.Blueprint
import org.common.Docker
import org.common.Log

class Sonarqube extends Base {

    private String environment

    Sonarqube(String environment) {
        super()
        this.environment = environment
        this.stage = "Sonarqube"
        this.description = "Running code analysis"
    }

    @Override
    def body() {
        def docker = new Docker()
        docker.setup()

        this._context.dir(Blueprint.deployPath()) {
            this.step("sonarqube", {
                try {
                    String sonarPath
                    sonarPath = this._context.sh(script: 'pwd', returnStdout: true).trim()
                    sonarPath = "${sonarPath}/out/app"
                    docker.build(this.environment, "sonarqube", "out")
                    this._context.sh("docker run --rm -e SONAR_HOST_URL='http://sonarqube.guruestate.com:9000' " +
                            "-e SONAR_PROJECT_BASE_DIR='/app' -e sonar.scm.provider=git -v ${sonarPath}:/app " +
                            "sonarsource/sonar-scanner-cli:4.4")
                } catch (Exception e) {
                    Log.error("Failure building dockerimage with sonarqube target")
                    this._context.error(e.toString())
                } finally {
                    // cleanup the workspace
                    this._context.sh("rm -rf ./out")
                }
            })
        }
    }

    @Override
    Boolean skip() {
        this._context.dir(Blueprint.deployPath()) {
            if (this._context.fileExists("sonar-project.properties")) {
                return false
            }
        }
        return true
    }
}
