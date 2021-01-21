package org.stages

import org.common.Blueprint
import org.common.Log
import org.common.Output
import org.common.PGbuild

class Build extends Base {

    Build() {
        super()
        this.stage = "Build"
        this.description = "Build"
    }

    def body() {
        // we have few services sharing the repository.
        // we store deployPath in blueprints to get the subpath in the repo.
        Log.info("Moving to subdirectory ${Blueprint.deployPath()}")
        this._context.dir(Blueprint.deployPath()) {
            // starting a new step instead of a state for unit tests etc.
            this.step("Stashing files", {
                (new Output()).stashDir("infra", "${Blueprint.appConfig()}/*.*")
                (new Output()).stash("pgbuild", Blueprint.pgbuild())
            })

            this.step("Running pre-build from pgbuild file", {
                Log.debug("Running pre-build steps from pgbuild.")
                PGbuild.executeSteps("pre-build")
            })

            this.step("Running build steps from pgbuild", {
                Log.debug("Running build steps from pgbuild.")
                PGbuild.executeSteps("build")
            })

            this.step("Running unit-tests from pgbuild", {
                Log.debug('Running unit-tests steps from pgbuild.')
                PGbuild.executeSteps('unit_tests')
            })

        }
    }

    @Override
    Boolean skip() {
        return false
    }
}
