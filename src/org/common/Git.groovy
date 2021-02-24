package org.common

import com.cloudbees.groovy.cps.NonCPS

@Singleton
class Git {
    private static String commitID
    private static Map<String, ArrayList> changelog = [:]
    private static ArrayList defaultCheckoutExtensions = [
            [$class: 'UserIdentity' , name:'Jenkins', email:'jenkins@propertyguru.com.sg'],
            [$class: 'LocalBranch', localBranch: "**"],
            [$class: 'CheckoutOption', timeout: 5],
            [$class: 'CloneOption', depth: 0, honorRefspec: true, noTags: false, reference: '', shallow: false, timeout: 5],
            [$class: 'CleanBeforeCheckout'],
            [$class: 'PruneStaleBranch']
    ]

    static void checkout(String repository, ArrayList extensions=[]) {
        extensions.addAll(defaultCheckoutExtensions)
        StepExecutor.retry(3, {
            StepExecutor.checkout(repository, extensions)
        })
    }

    static void createTag(String name, String commitID) {
        StepExecutor.retry(3) {
            try {
                StepExecutor.sh("git tag ${name} ${commitID}")
                StepExecutor.sh("git push origin refs/tags/${name}")
            } catch (Exception e) {
                Log.error("Failure setting & pushing tag: ${name} -> ${commitID}")
                Log.error(e.toString())
                removeTag(name)
            }
        }
    }

    static void removeTag(String name) {
        try {
            StepExecutor.sh("git tag -d ${name}")
            StepExecutor.sh("git push --delete origin ${name}")
        } catch(Exception e) {
            Log.error("Failure removing tag: ${name}")
            Log.error(e.toString())
        }
    }

    static String getLastDeployedTag(String environment) {
        String cmd
        try {
            cmd = "git tag --merged HEAD --sort=committerdate | grep ${environment}"
            return StepExecutor.shWithOutput(cmd)
        } catch(Exception e) {
            Log.info("Failure running: ${cmd}")
            Log.info(e.toString())
        }
        return null
    }

    static String getCommitID() {
        if (commitID == null) {
            try {
                commitID = StepExecutor.shWithOutput("git rev-parse HEAD")
            } catch(Exception e) {
                Log.info("Unable to get the commitID")
            }
        }
        return commitID
    }

    static String getSHA(Integer len=6) {
        return getCommitID().substring(0, len)
    }

    static ArrayList<String> getChangelog(String environment) {
        if (changelog.get(environment, null) == null) {
            String from = getLastDeployedTag(environment)
            if (from == "") {
                from = "HEAD~10"
            }
            String cmd = "git log ...${from} --name-only --no-merges " +
                    "--pretty=format:'|||\ncommit %H%nauthor %aN%nauthorEmail %aE%ncommitter %cN%ncommitteremail " +
                    "%cE%nsubject %s%nbody %B%nfiles'"
            String data = StepExecutor.shWithOutput(cmd)
            changelog[environment] = []
            data.tokenize("|||").each { String log ->
                changelog[environment].add(new Changelog(log).parse())
            }
        }
        return changelog[environment]
    }

    static void updatePRStatus(String stage, String step, String status) {
        if (stage != "Setup") {
            StepExecutor.updatePRStatus("${Blueprint.name()}", Blueprint.repository(), StepExecutor.env("ghprbActualCommit"), step, status)
        }
    }
//    static void updateCommitStatus(String message, String status) {
//        def sha = _context.env.ghprbActualCommit
//        _context.step([
//                $class: 'GitHubCommitStatusSetter',
//                commitShaSource: [$class: "ManuallyEnteredShaSource", sha: sha],
//                reposSource: [$class: "ManuallyEnteredRepositorySource", url: "${Blueprint.repository()}"],
//                contextSource: [$class: "ManuallyEnteredCommitContextSource", context: message],
//                errorHandlers: [[$class: "ChangingBuildStatusErrorHandler", result: "UNSTABLE"]],
//                statusResultSource: [
//                        $class: 'ConditionalStatusResultSource',
//                        results: [
//                                [$class: "AnyBuildResult", message: message, state: status]
//                        ]
//                ]
//        ]);
//    }
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
                this.sha = parseLine(line)
            } else if (line.startsWith('author ')){
                this.author = parseLine(line)
            } else if (line.startsWith('authorEmail ')) {
                this.authorEmail = parseLine(line)
            } else if (line.startsWith('committer ')){
                this.committer = parseLine(line)
            } else if (line.startsWith('committeremail ')) {
                this.committerEmail = parseLine(line)
            } else if (line.startsWith('subject ')) {
                this.messageTitle = parseLine(line)
            } else if (line.startsWith('body ')) {
                this.messageBody = parseLine(line)
            } else if (line.startsWith('files')) {
                fileFlag = true
                this.changedFiles = []
            }
        }
    }

    @NonCPS
    private static String parseLine(String data) {
        return data.tokenize(' ')[1..-1].join(' ').trim()
    }

    Map parse() {
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