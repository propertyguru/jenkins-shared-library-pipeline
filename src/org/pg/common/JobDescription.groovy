package org.pg.common

@Singleton
class JobDescription {

    /*
    _description is an array. we merge the elements together and set the job description.
     */

    private static def _context
    private static ArrayList<String> _description
    private static Map<String, String> lastCommitID = [:]
    private static Map<String, List<String>> jobEnvs = [
            'android': ['alpha', 'beta', 'internal', 'production'],
            'default': ['integration', 'staging', 'production']
    ]

    static void setup() {
        _context = Context.get()
        Log.info("Current description: ${getDescription()}")
        Log.info("Current description class: ${_context.currentBuild.rawBuild.project.description.getClass()}")
        _description = getDescription()
        // TODO: use job parameters to get environments
        if (_description.size() == 1) {
            _description = [
                    "<a href=''>integration</a>",
                    "<a style='padding-left:30%' href=''>staging</a>",
                    "<a style='padding-left:30%' href=''>production</a>"
            ]
            if (BuildArgs.component() == "android"){
                _description = [
                        "<a href=''>alpha</a>",
                        "<a style='padding-left:20%' href=''>beta</a>",
                        "<a style='padding-left:20%' href=''>internal</a>",
                        "<a style='padding-left:20%' href=''>production</a>"
                ]
            }
        }
    }

    private static void set(String value) {
        _context.currentBuild.rawBuild.project.description = value
    }

    static void update(String environment, String value) {
        for (Integer i=0; i<_description.size(); i++) {
            if (_description[i].contains('>'+environment+'<')) {
                _description[i] = buildDesc(environment, value)
            }
        }
        set(_description.join('&nbsp'))
    }

    static ArrayList<String> getDescription() {
        String desc = _context.currentBuild.rawBuild.project.description
        if (desc != "") {
            return desc.split('&nbsp')
        }
        return []
    }

    static String getValue(String environment) {
        if (lastCommitID.get(environment, null) == null) {
            try {
                String desc = _description.split('&nbsp')
                String href
                for (Integer i = 0; i < desc.size(); i++) {
                    if (desc[i].contains('>' + environment + '<')) {
                        def htmlParser = new XmlSlurper().parseText(desc[i])
                        htmlParser.depthFirst().collect { it }.findAll {
                            it.name() == "a"
                        }.each {
                            href = "${it.@href.text()}"
                        }

                        href = href.split("\\?")
                        if (href.size() > 1 && href[1] != "null") {
                            lastCommitID[environment] = href[1].trim()
                            return lastCommitID[environment]
                        }
                    }
                }
            } catch (Exception e) {
                Log.info("Couldnt find any commitID from job description")
                lastCommitID[environment] = ""
            }
        }
        return null
    }

    private static String buildDesc(String environment, String value) {
        String buildURL
        buildURL = BuildArgs.buildURL()
        buildURL = buildURL.split('/')
        buildURL = buildURL[0..2].join('/') + "/view/PIPELINE/" + buildURL[3..9].join('/') + "/changes/?" + value
        return "<a href=\"${buildURL}\">${environment}</a>"
    }

}
