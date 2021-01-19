package org.common

import com.cloudbees.groovy.cps.NonCPS

@Singleton
class Git {

    private static def _context
    private static String commitID
    private static Map<String, ArrayList> changelog = [:]
    private static ArrayList defaultExtensions

    static void setup() {
        _context = Context.get()
        defaultExtensions = [
                [$class: 'UserIdentity' , name:'Jenkins', email:'jenkins@propertyguru.com.sg'],
                [$class: 'LocalBranch', localBranch: "**"],
                [$class: 'CheckoutOption', timeout: 5],
                [$class: 'CloneOption', depth: 0, honorRefspec: true, noTags: false, reference: '', shallow: false, timeout: 5],
                [$class: 'CleanBeforeCheckout'],
                [$class: 'PruneStaleBranch']
        ]
    }

    static void checkout(ArrayList extensions=[]) {
        extensions.addAll(defaultExtensions)
        _context.retry(3) {
            _context.checkout([
                    $class: 'GitSCM',
                    branches: [[name: "${_context.BRANCH}"]],
                    doGenerateSubmoduleConfigurations: false,
                    gitTool: "git",
                    extensions: extensions,
                    userRemoteConfigs: [[
                            credentialsId: 'github',
                            refspec: "+refs/heads/*:refs/remotes/origin/* +refs/tags/*:refs/remotes/origin/*",
                            url: Blueprint.repository()
                    ]],
                    submoduleCfg: [],
            ])
        }
    }

    static void createTag(String name, String commitID="") {
        try {
            String cmd = "git tag ${name}"
            if (commitID != "") {
                cmd += " ${commitID}"
            }
            new Output().sh(cmd)
        } catch(Exception e) {
            Log.info("Failure setting tag: ${name} -> ${commitID}")
            Log.info(e)
        }
    }

    static String getLastTag(String pattern) {
        String cmd
        try {
            cmd = "git tag --merged HEAD --sort=committerdate | grep ${pattern} | tail -1"
            return new Output().shWithOutput(cmd)
        } catch(Exception e) {
            Log.info("Failure running: ${cmd}")
            Log.info(e)
        }
        return ""
    }

    static String getCommitID() {
        if (commitID == null) {
            try {
                commitID = new Output().shWithOutput("git rev-parse HEAD")
            } catch(Exception e) {
                Log.info("Unable to get the commitID")
            }
            commitID = ""
        }
        return commitID
    }

    static ArrayList<String> getChangelog(String environment) {
        if (changelog.get(environment, null) == null) {
            String from = getLastTag(environment)
            if (from == "") {
                from = "HEAD~10"
            }
            String cmd = "git log ...${from} --name-only --no-merges " +
                    "--pretty=format:'---\ncommit %H%nauthor %aN%nauthorEmail %aE%ncommitter %cN%ncommitteremail " +
                    "%cE%nsubject %s%nbody %B%nfiles'"
            String data = new Output().shWithOutput(cmd)
            changelog[environment] = []
            data.tokenize("---").each { String log ->
                changelog[environment].add(new Changelog(log).toString())
            }
        }
        return changelog[environment]
    }

    static void updateCommitStatus(String message, String status) {
        def sha = _context.env.ghprbActualCommit
        _context.step([
                $class: 'GitHubCommitStatusSetter',
                commitShaSource: [$class: "ManuallyEnteredShaSource", sha: sha],
                reposSource: [$class: "ManuallyEnteredRepositorySource", url: "${Blueprint.repository()}"],
                contextSource: [$class: "ManuallyEnteredCommitContextSource", context: message],
                errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
                statusResultSource: [
                        $class: 'ConditionalStatusResultSource',
                        results: [
                                [$class: "AnyBuildResult", message: message, state: status]
                        ]
                ]
        ]);
    }
}

class Changelog {
    private String sha
    private String author
    private String authorEmail
    private String committer
    private String committerEmail
    private String messageTitle
    private String messageBody
    private ArrayList<String> changedFiles

    Changelog(String log) {
        Boolean fileFlag = false
        for (String line in log.tokenize("\n")) {
            if (line.trim() == ""){
                continue
            }
            if (fileFlag) {
                this.changedFiles.add(line)
            }
            if (line.startsWith('commit ')) {
                this.sha = parse(line)
            } else if (line.startsWith('author ')){
                this.author = parse(line)
            } else if (line.startsWith('authorEmail ')) {
                this.authorEmail = parse(line)
            } else if (line.startsWith('committer ')){
                this.committer = parse(line)
            } else if (line.startsWith('committeremail ')) {
                this.committerEmail = parse(line)
            } else if (line.startsWith('subject ')) {
                this.messageTitle = parse(line)
            } else if (line.startsWith('body ')) {
                this.messageBody = parse(line)
            } else if (line.startsWith('files')) {
                fileFlag = true
                this.changedFiles = []
            }
        }
    }

    @NonCPS
    private static String parse(String data) {
        return data.tokenize(' ')[1..-1].join(' ').trim()
    }

    String toString() {
        return [
                "sha": this.sha,
                "author": this.author,
                "authorEmail": this.authorEmail,
                "committer": this.committer,
                "committerEmail": this.committerEmail,
                "messageTitle": this.messageTitle,
                "messageBody": this.messageBody,
                "changedFiles": this.changedFiles
        ]
    }
}