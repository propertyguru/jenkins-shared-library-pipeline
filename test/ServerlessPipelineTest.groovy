//import org.junit.*
//import com.lesfurets.jenkins.unit.cps.BasePipelineTestCPS
//
//class ServerlessPipelineTest extends BasePipelineTestCPS {
//
//    @Test
//    void should_execute_without_errors() throws Exception {
//        scriptRoots += 'vars'
//        super.setUp()
//        helper.registerAllowedMethod("extendedChoice", [LinkedHashMap.class],{LOGLEVEL = false})
//        helper.registerAllowedMethod('parameters', [ArrayList.class], {
//            env = new HashMap();
//            env.put("JOB_NAME","devtools-component/subcomponent/build")
//            ENVIRONMENT="integration"
//        })
//        binding.setVariable('ENVIRONMENT', 'integration')
//
//        def script = loadScript("vars/ServerlessPipeline.groovy")
//        script.call(null)
//        printCallStack()
//    }
//}