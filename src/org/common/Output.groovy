package org.common

class Output {
    private def _context

    Output() {
        this._context = Context.get()
    }

    def stash(name, path=null){
        if (path == null){
            path = name
        }
        this._context.stash(allowEmpty: true, name: "${name}", includes: "${path}")
    }

    def stashDir(name, dir=null){
        if (dir == null){
            dir = name
        }
        this._context.stash(allowEmpty: true, name: "${name}", includes: "${dir}")
    }

    def unstash(name){
        this._context.unstash(name: "${name}")
    }

    def shWithOutput(String cmd) {
        this._context.sh(returnStdout: true, script: cmd).trim()
    }

    def sh(String cmd) {
        this._context.sh(cmd)
    }

    def archive(name){
        this._context.archiveArtifacts(allowEmptyArchive: true, artifacts: "${name}", fingerprint: true)
    }

    def sendMail(recipients, subject, attachment=null, body='''${SCRIPT, template="groovy-html.template"}''') {
        this._context.emailext(
                body: body,
                attachmentsPattern: attachment,
                mimeType: 'text/html',
                subject: subject,
                to: recipients,
                recipientProviders: [this._context.requestor()]
        )
    }

}
