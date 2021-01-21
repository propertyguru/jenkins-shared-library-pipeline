package org.common

import java.text.SimpleDateFormat

@Singleton
class Log {
    private static def _context
    private static ArrayList<String> levels = ["debug", "info", "error"]
    private static String dateFormat = "dd.MM.yyyy|HH:mm:ss.SSS"
    private static String logLevel = "info"

    static def setup() {
        _context = Context.get()
        if (_context.LOGLEVEL == "true") {
            logLevel = "debug"
        }
    }

    static def level() {
        return logLevel
    }

    static def debug(def message){
        logMessage("debug", "\033[33m ${message} \033[0m")
    }

    static def info(def message) {
        logMessage("info", "\033[32m ${message} \033[0m")
    }

    static def error(def message){
        logMessage("error", "\033[31m ${message} \033[0m")
    }

    private static logMessage(String level, String message){
        if(levels.indexOf(level) >= levels.indexOf(logLevel)){
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat)
            String date = formatter.format(new Date())
            _context.println "${date} [${level.toUpperCase()}] ${message}"
        }
    }
}
