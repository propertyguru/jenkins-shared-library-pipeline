import org.pg.Pipeline
import org.pg.common.Blueprint
import org.pg.common.BuildArgs
import org.pg.common.Log

def call(body) {
    Log.setup(this)
    BuildArgs.setup(this)
    Blueprint.setup(this)

    (new Pipeline(this)).execute()

}