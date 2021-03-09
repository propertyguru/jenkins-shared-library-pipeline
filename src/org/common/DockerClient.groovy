package org.common

@Singleton
class DockerClient implements Serializable {
    static void loginToDockerHub() {
        try {
            StepExecutor.withUsernamePassword('docker-hub', { String user, pass ->
                StepExecutor.sh("docker login --username ${user} --password ${pass}")
            })
        } catch(Exception e) {
            Log.error("Failure login to docker-hub")
            Log.error(e.toString())
        }
    }

    static void loginToECR() {
        try {
            StepExecutor.sh("eval \$(aws ecr get-login --no-include-email)")
        } catch(Exception e) {
            Log.error("Failure login to ECR")
            Log.error(e.toString())
        }
    }

    static void createECRRepository(String name) {
        loginToECR()
        try {
            StepExecutor.sh("aws ecr create-repository --repository-name ${name}} || true")
        } catch(Exception e) {
            Log.error("Failure login to ECR")
            Log.error(e.toString())
        }
    }

    static void build(String name, String dockerfile="Dockerfile", String dockerArgs="", String target=null, String output=null) {
        loginToDockerHub()
        try {
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
            cmd += "-f ${dockerfile} ${dockerArgs} -t ${name} ."
            Log.info(cmd)
            StepExecutor.sh(cmd)
        } catch(Exception e) {
            Log.info("ERROR: " + e.toString())
            Log.error("Failure building docker image: ${e.toString()}")

        }
    }

    static void push(String name) {
        try {
            StepExecutor.sh("docker push ${name}")
        } catch(Exception e) {
            Log.error("Failed to push image: ${name}")
            Log.error(e.toString())
        }
    }

    static void pull(String name) {
        try {
            String cmd = "docker pull ${name}"
            StepExecutor.sh(cmd)
        } catch(Exception e) {
            Log.error("Failed to pull image: ${name}")
            Log.error(e.toString())
        }
    }

    static void remove(String name) {
        try {
            String cmd = "docker rmi ${name}"
            StepExecutor.sh(cmd)
        } catch (Exception e) {
            Log.error("Failed to remove image: ${name}")
            Log.error(e.toString())
        }
    }

    static void rename(String source, String target) {
        try {
             StepExecutor.sh("docker tag ${source} ${target}")
        } catch(Exception e) {
            Log.error("Failure setting tag: ${source} -> ${target}")
            Log.error(e.toString())
        }
    }
}