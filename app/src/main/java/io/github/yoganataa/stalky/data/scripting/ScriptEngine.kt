package io.github.yoganataa.stalky.data.scripting

import org.mozilla.javascript.Context
import org.mozilla.javascript.Scriptable
import org.mozilla.javascript.ScriptableObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScriptEngine @Inject constructor() {
    
    fun executeScript(script: String, params: Map<String, Any> = emptyMap()): Any? {
        val context = Context.enter()
        return try {
            context.optimizationLevel = -1
            val scope: Scriptable = context.initStandardObjects()
            
            // Add parameters to scope
            params.forEach { (key, value) ->
                ScriptableObject.putProperty(scope, key, value)
            }
            
            context.evaluateString(scope, script, "script", 1, null)
        } catch (e: Exception) {
            throw ScriptException("Script execution failed", e)
        } finally {
            Context.exit()
        }
    }
    
    fun validateScript(script: String): ScriptValidationResult {
        return try {
            val context = Context.enter()
            context.optimizationLevel = -1
            val scope: Scriptable = context.initStandardObjects()
            context.compileString(script, "validation", 1, null)
            Context.exit()
            ScriptValidationResult(true, null)
        } catch (e: Exception) {
            ScriptValidationResult(false, e.message)
        }
    }
}

data class ScriptValidationResult(
    val isValid: Boolean,
    val error: String?
)

class ScriptException(message: String, cause: Throwable) : Exception(message, cause)