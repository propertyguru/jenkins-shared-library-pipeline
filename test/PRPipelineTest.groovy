import com.lesfurets.jenkins.unit.BasePipelineTest
import groovy.json.JsonBuilder
import org.junit.Before
import org.junit.Test

class PRPipelineTest extends BasePipelineTest {

    @Override
    @Before
    void setUp() {
        helper.scriptRoots = ['vars']
        super.setUp()
        helper.registerAllowedMethod("extendedChoice", [Map.class], {
            [
                    description: 'Select environments to deploy',
                    multiSelectDelimiter: ',',
                    name: 'ENVIRONMENT',
                    quoteValue: false,
                    saveJSONParameterToFile: false,
                    type: 'PT_MULTI_SELECT',
                    value: 'integration,staging,production',
                    visibleItemCount: 10
            ]
        })
        helper.registerAllowedMethod("ansiColor", [String, Closure.class])
        helper.registerAllowedMethod("booleanParam", [Map.class], {
            [
                    defaultValue: false,
                    name: 'LOGLEVEL',
                    description: 'Check this to enable debug logs'
            ]
        })
        helper.registerAllowedMethod("parameters", [ArrayList.class])
        helper.registerAllowedMethod("slackUserIdFromEmail", [Map.class], {
            return "UASDASDAN"
        })
        helper.registerAllowedMethod("slackUploadFile", [Map.class])
        helper.registerAllowedMethod("slackSend", [Map.class], {
            return new SlackResponse()
        })
        helper.registerAllowedMethod("wrap", [Map.class, Closure])
        helper.registerAllowedMethod("fileExists", [String], { return true })
        helper.registerAllowedMethod("readYaml", [Map.class], {
            return [:]
        })
        helper.registerAllowedMethod("Utils.markStageSkippedForConditional", [String])
        Map blueprintData = [
                "local": [
                        "slack"       : [
                                "channels": [
                                        "production" : [
                                                "deployment"
                                        ],
                                        "staging"    : [
                                                "alerts-qa"
                                        ],
                                        "integration": [
                                                "alerts-qa"
                                        ]
                                ]
                        ],
                        "team_emails" : [
                                "chalat@ddproperty.com",
                                "pirasis@ddproperty.com",
                                "ashish@ddproperty.com",
                                "aurelien@ddproperty.com",
                                "sridhar@ddproperty.com",
                                "tomas@ddproperty.com",
                                "vishnuvarma@ddproperty.com",
                                "nimit@ddproperty.com"
                        ],
                        "deploy"      : [
                                "path"      : "packages/monitor-triggerer",
                                "repository": "github.com:propertyguru/ad-products"
                        ],
                        "component"   : "ads",
                        "name"        : "product",
                        "metrics"     : [
                                "cpu": 180
                        ],
                        "instances"   : [
                                [
                                        "metrics" : [
                                                "cpu": 180
                                        ],
                                        "metadata": [
                                                "cronjob" : true,
                                                "qa-jobs" : false,
                                                "schedule": "1/10 * * * *"
                                        ],
                                        "name"    : "product-checkactivationsgboostv2",
                                        "env"     : [
                                                "url": "api/v1/utilities/checkRecentActivation?region=sg&productType=boost-v2&alert=1"
                                        ]
                                ]
                        ],
                        "dependencies": [],
                        "type"        : "nodejs",
                        "resources"   : [
                                "scale" : [
                                        "max": 1,
                                        "min": 1
                                ],
                                "cpu"   : [
                                        "max": "30m",
                                        "min": "10m"
                                ],
                                "memory": [
                                        "max": "100Mi",
                                        "min": "80Mi"
                                ]
                        ],
                        "metadata"    : [
                                "cronjob": true,
                                "qa-jobs": false
                        ]
                ]
        ]
        helper.addShMock("salt-call -l debug github.subcomponent 'pg_ads_product' --output json",
                new JsonBuilder(blueprintData).toString(), 0)


        binding.setVariable('currentBuild', [
                absoluteUrl: 'http://example.com/dummy',
                buildVariables: [:],
                changeSets: [],
                currentResult: 'SUCCESS',
                description: 'dummy',
                displayName: '#1',
                duration: 1,
                durationString: '1 ms',
                fullDisplayName: 'dummy #1',
                fullProjectName: 'dummy',
                id: '1',
                keepLog: false,
                nextBuild: null,
                number: 1,
                previousBuild: null,
                projectName: 'dummy',
                rawBuild: [
                        'project': [
                                'description': ""
                        ]
                ],
                result: 'SUCCESS',
                startTimeInMillis: 1,
                timeInMillis: 1,
                upstreamBuilds: [],
        ])
        binding.setVariable("LOGLEVEL", "true")
        binding.setVariable("SLACK_ID", "XYZ")
        binding.setVariable("BRANCH", "master")
        binding.setVariable("ENVIRONMENT", "integration")

        Map env = [:]
        env['JOB_NAME'] = "devtools-ads/product/pr"
        env['BUILD_URL'] = "https://jenkins.guruestate.com/job/devtools-ads/job/product/job/pr/1/"
        env['LOGLEVEL'] = "true"
        env['SLACK_ID'] = "XYZ"
        env["BRANCH"] = "master"
        env["ENVIRONMENT"] = "integration"
        binding.setVariable('env', env)
    }

    @Test
    void should_execute_without_errors() throws Exception {
        def script = loadScript("vars/PRPipeline.groovy")
        script.call(null)
        printCallStack()
//        assertJobStatusSuccess()
    }

}

