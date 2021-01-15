package org.pg.common

@Singleton
class Git {

    private static def _context
    private static String commitID
    private static Map<String, ArrayList> changelog
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
            String lastCommitID = JobDescription.getValue(environment)
            try {
                if (lastCommitID != null) {
                    changelog[environment] = getCommits(lastCommitID)
                }
            } catch (Exception e) {
                Log.info("Unable to load changelog for ${environment}")
            }
            changelog[environment] = []
        }
        return changelog[environment]
    }

    private static ArrayList<String> getCommits(from, to=this.context.BRANCH) {
        def cmd = "git log ${to}...${from} --name-only --no-merges " +
                "--pretty=format:'---\ncommit %H%nauthor %aN%nauthorEmail %aE%ncommitter %cN%ncommitteremail " +
                "%cE%nsubject %s%nbody %B%nfiles'"
        String data = new Output().shWithOutput(cmd)
        ArrayList<String> logs = []
        ArrayList<String> commits = []
        if (data.size() > 1){
            logs = data.split("---")[1..-1]
        }
        logs.each { log ->
            Map<String, String> commit
            // we get changedfiles list in separate lines. So we look for a line which starts with files and then store
            // all the files in the commit object.
            Boolean changedFilesFlag = false
            for (line in log.split('\n')) {
                // ignore if empty line
                if (line.trim() == "") {
                    continue
                }
                if (changedFilesFlag) {
                    commit["changedFiles"].add(line)
                }

                if (line.startsWith('commit ')) {
                    commit["sha"] = Git.instance.parse(line)
                } else if (line.startsWith('author ')){
                    commit["author"] = Git.instance.parse(line)
                } else if (line.startsWith('authorEmail ')) {
                    commit["authorEmail"] = Git.instance.parse(line)
                } else if (line.startsWith('committer ')){
                    commit["committer"] = Git.instance.parse(line)
                } else if (line.startsWith('committeremail ')) {
                    commit["committerEmail"] = Git.instance.parse(line)
                } else if (line.startsWith('subject ')) {
                    commit["messageTitle"] = Git.instance.parse(line)
                } else if (line.startsWith('body ')) {
                    commit["messageBody"] = Git.instance.parse(line)
                } else if (line.startsWith('files')) {
                    changedFilesFlag = true
                    commit["changedFiles"] = []
                }
            }
            commits.add(commit)
        }
        return commits
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
