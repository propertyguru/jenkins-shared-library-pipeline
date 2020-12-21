//import org.junit.*
//import com.lesfurets.jenkins.unit.cps.BasePipelineTestCPS
//
//class MainPipelineTest extends BasePipelineTestCPS {
//
//    @Test
//    void should_execute_without_errors() throws Exception {
//        super.setUp()
//        def script = loadScript("vars/MainPipeline.groovy")
//
//        // none of the globals exposed by Jenkins are available
//        // so, they all need to be mocked
//        helper.registerAllowedMethod("extendedChoice",[LinkedHashMap.class],{LOGLEVEL = false})
//
////        helper.registerAllowedMethod("wrap",[LinkedHashMap.class, Closure.class],null) // investigate what it does
////        helper.registerAllowedMethod('parameters',[ArrayList.class],
////                {
////                    env = new HashMap();
////                    env.put("JOB_NAME","test-test/test")
////                    ENVIRONMENT="staging"
////                }
////        )
//        binding.setVariable('ENVIRONMENT', 'integration')
////        binding.setVariable('currentBuild',
////                [
////                        result: 'SUCCESS',
////                        rawBuild : [project : [description : "Some description"]]
////                ])
//        script.call(null)
//        printCallStack()
//    }
//}