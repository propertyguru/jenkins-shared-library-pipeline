package org.stages

import org.common.Blueprint
import org.common.BuildArgs
import org.common.DockerClient
import org.common.Git
import org.common.StepExecutor

class Docker extends Base {
    // TODO: this should go under resources
    Map repository = [
            "aws": "199699173728.dkr.ecr.ap-southeast-1.amazonaws.com",
            "gcp": [
                    "integration": "gcr.io/pg-integration-1",
                    "production": "gcr.io/pg-production-4"
            ]
    ]

    Docker() {
        super()
        this.stage = "Docker"
    }

    @Override
    def body() {
        String name
        String tag

        this.step("Building docker image", {
            tag = Git.getSHA()
            if (Blueprint.component() == "airflow") {
                // TODO: I don't remember why we thought of doing it like this.
                tag = StepExecutor.env('GIT_BRANCH')
            }
            name = Blueprint.component() + "/" + Blueprint.subcomponent() + ":" + tag

            // TODO: I don't remember why we are logging in to docker-hub
            DockerClient.loginToDockerHub()
            // before building, check if the image is present in registry!
            DockerClient.build(
                    name,
                    Blueprint.dockerfile(),
                    Blueprint.dockerArgs()
            )
        })

        // dont run this step if its a pr job or if deployment is to be skipped.
        if (!BuildArgs.isPRJob() && !Blueprint.skipDeployment()) {
            this.step("Pushing Docker Image", {
                if (Blueprint.cloud() == "gcp") {
                    StepExecutor.env("ENVIRONMENT").tokenize(',').each { env ->
                        String repo = repository["gcp"][env]
                        DockerClient.rename(name, repo + "/" + name)
                        DockerClient.push(repo + "/" + name)
                    }
                } else { // aws
                    // this will take care of login too.
                    DockerClient.createECRRepository("${Blueprint.component()}/${Blueprint.subcomponent()}")
                    String repo = repository["aws"]
                    DockerClient.rename(name, repo + "/" + name)
                }
            })
        }
    }

    @Override
    Boolean skip() {
        // this stage needs to make sure to promote the images.
        return !StepExecutor.fileExists("${Blueprint.dockerfile()}")
    }
}
