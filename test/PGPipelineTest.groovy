import com.lesfurets.jenkins.unit.BasePipelineTest
import groovy.json.JsonBuilder
import org.junit.Before
import org.junit.Test

class PGPipelineTest extends BasePipelineTest {

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
        helper.registerAllowedMethod("slackSend", [Map.class], {
            return new SlackResponse()
        })
        helper.registerAllowedMethod("wrap", [Map.class, Closure])
        helper.registerAllowedMethod("fileExists", [String], { return true })
        helper.registerAllowedMethod("readYaml", [Map.class], {
            return [:]
        })
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


        binding.setVariable("LOGLEVEL", "true")
        binding.setVariable("SLACK_ID", "XYZ")
        binding.setVariable("BRANCH", "master")
        binding.setVariable("ENVIRONMENT", "integration")
        Map env = [:]
        env['JOB_NAME'] = "devtools-ads/product/build-test"
        env['BUILD_URL'] = "https://jenkins.guruestate.com/job/devtools-ads/job/product/job/build-test/290/"
        binding.setVariable('env', env)
    }

    @Test
    void should_execute_without_errors() throws Exception {
        def script = loadScript("vars/PGPipeline.groovy")
        script.call(null)
        printCallStack()
//        assertJobStatusSuccess()
    }

}

@SuppressWarnings(['EmptyMethod', 'MethodReturnTypeRequired', 'UnusedMethodParameter'])
class SlackResponse implements Serializable {
    static String getChannelId() {
        return "#prince-test"
    }

    static String getTs() {
        return "ts"
    }
}