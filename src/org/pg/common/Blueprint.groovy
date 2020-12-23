package org.pg.common

@Singleton
class Blueprint {
    static def context
    def data

    static def setup(context) {
        this.context = context
    }

    static def repository() {
        def repo = Blueprint.instance.data.get('deploy', {}).get('repository', '')
        return "git@${repo}.git"
    }

    static def load() {
        Blueprint.instance.data()
    }

    def data(){
        if (this.data == null) {
            try {
                (new Salt(this.context)).sync()
                this.data = (new Salt(this.context)).blueprint()
                this.data = data.local
            }
            catch(Exception e) {
                throw e
            }
        }
        return this.data
    }

}
