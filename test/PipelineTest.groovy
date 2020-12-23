import com.lesfurets.jenkins.unit.BasePipelineTest
import org.junit.Test

class PipelineTest extends BasePipelineTest {

    @Test
    void should_execute_without_errors() throws Exception {
        helper.scriptRoots = ['vars']
//        scriptRoots += 'vars'
        super.setUp()
        helper.registerAllowedMethod("wrap",[LinkedHashMap.class, Closure.class],null) // investigate what it does
        helper.registerAllowedMethod('docker', [String.class], null)
        helper.registerAllowedMethod('image', [String.class], null)
        helper.registerAllowedMethod('parameters', [Closure.class], { Closure parametersBody ->

            // Register the contained elements
            helper.registerAllowedMethod('string', [Map.class], { Map stringParam ->

                // Add the param default for a string
                addParam(stringParam.name, stringParam.defaultValue)

            })
            helper.registerAllowedMethod('booleanParam', [Map.class], { Map boolParam ->
                // Add the param default for a string
                addParam(boolParam.name, boolParam.defaultValue.toString().toBoolean())
            })

            // Run the body closure
            def paramsResult = parametersBody()

            // Unregister the contained elements
//            helper.unRegisterAllowedMethod('string', [Map.class])
//            helper.unRegisterAllowedMethod('booleanParam', [Map.class])

            // Result to higher level. Is this needed?
            return paramsResult
        })

//        helper.registerAllowedMethod("extendedChoice", [LinkedHashMap.class],{LOGLEVEL = false})
//        helper.registerAllowedMethod('parameters', [ArrayList.class], {
//            env = new HashMap();
//            env.put("JOB_NAME","devtools-component/subcomponent/build")
//            ENVIRONMENT="integration"
//        })
//        binding.setVariable('ENVIRONMENT', 'integration')

        def script = loadScript("vars/MainPipeline.groovy")
        script.call(null)
        printCallStack()
    }

    void addParam(String name, Object val, Boolean overWrite = false) {
        Map params = binding.getVariable('params') as Map
        if (params == null) {
            params = [:]
            binding.setVariable('params', params)
        }
        if ( (val != null) && (params[name] == null || overWrite)) {
            params[name] = val
        }
    }
}