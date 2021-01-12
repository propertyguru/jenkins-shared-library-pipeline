package org.pg.stages

import org.pg.common.Blueprint
import org.pg.common.BuildArgs
import org.pg.common.Docker
import org.pg.common.agents.DockerAgent

class DockerImage extends Base {

    DockerImage(environment) {
        super(environment)
        this.stage = "Docker Image"
        this.description = "building & publishing docker image"
    }

    @Override
    def body() {

        def docker = new Docker()
        docker.setup()

        this.step("Building docker image", {
            // TODO: Do not build if it's not "integration" environment
            // TODO: Cycle staging and production tags on Continuous Delivery and not rebuild containers
            //		 to avoid newer versions than integration in staging and production environments
            this.context.dir(Blueprint.deployPath()) {
                docker.build(this.environment)
                if (!BuildArgs.isPRJob()) {
                    if (!Blueprint.skipDeployment()) {
                        docker.push()
                    }
                }
            }
        })
    }

    @Override
    Boolean skip() {
        return false
    }
}
