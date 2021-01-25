package org.common

class Docker {
    private String name
    private String tag
    private String cloud
    private String dockerFile
    private String dockerArgs
    private Map<String, String> gcpProjectIDs = [
            "integration": "pg-integration-1",
            "production": "pg-production-4"
    ]

    Docker() {}

    def setup() {
        this.name = Blueprint.component() + "/" + Blueprint.subcomponent()
        this.tag = BuildArgs.buildNumber()
        if (Blueprint.component() == "airflow") {
            this.tag = StepExecutor.env('BRANCH')
        }
        this.cloud = Blueprint.cloud()
        this.dockerFile = Blueprint.dockerfile()
        this.dockerArgs = Blueprint.dockerArgs()
    }

    def login() {
        StepExecutor.withUsernamePassword('docker-hub', 'USERNAME', 'PASSWORD', {
            StepExecutor.sh("docker login --username ${USERNAME} --password ${PASSWORD}")
        })
    }

    def build(String environment, String target=null, String output=null, String additionalArgs="") {
        this.login()

        String cmd = ""
        if (output != null) {
            cmd += "DOCKER_BUILDKIT=1 "
        }
        cmd += "docker build --pull "
        if (output != null) {
            cmd += "--output ${output} "
        }
        if (target != null) {
            cmd += "--target ${target} "
        }
        cmd += "-f ${this.dockerFile} ${this.dockerArgs} ${additionalArgs} -t ${imageName(environment)} ."
        StepExecutor.sh(cmd)
    }

    def push() {
        if (this.cloud == 'aws') {
            StepExecutor.sh("aws ecr create-repository --repository-name ${this.name} || true")
            StepExecutor.sh("eval \$(aws ecr get-login --no-include-email)")
            StepExecutor.sh("docker push ${imageName("integration")}")
        } else if (this.cloud == 'gcp') {
            // we have different registries per environment in GCP.
            // therefore if we are deploying the same build to multiple environments, we need to make sure the image
            // exists in every docker registry
            for (String env in StepExecutor.env('ENVIRONMENT').tokenize(',')) {
                if (env != "integration"){
                    // by default, we create image in integration. We are just copying the image to other envs too.
                    rename("${imageNameWithoutTag("integration")}", "${imageNameWithoutTag(env)}")
                }
                StepExecutor.sh("docker push ${imageName(env)}")
            }
        }
    }

    def pull() {
        String cmd = "docker pull ${imageName()}"
        StepExecutor.sh(cmd)
    }

    def remove() {
        String cmd = "docker rmi ${imageName()}"
        StepExecutor.sh(cmd)
    }

    def rename(String source, String target) {
        StepExecutor.sh("docker tag ${source}:${this.tag} ${target}:${this.tag}")
    }

    String imageName(String environment="integration") {
        return "${repo(environment)}/${this.name}:${this.tag}"
    }

    String imageNameWithoutTag(String environment="integration") {
        return "${repo(environment)}/${this.name}"
    }

    def repo(String environment="integration") {
        if (this.cloud == 'aws'){
            return "199699173728.dkr.ecr.ap-southeast-1.amazonaws.com"
        } else if (this.cloud == 'gcp'){
            return "gcr.io/${gcpProjectIDs[environment]}"
        }
    }

}
