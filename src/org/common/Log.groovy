package org.common

import java.text.SimpleDateFormat

@Singleton
class Log {
    private static def _context
    private static ArrayList<String> levels = ["debug", "info", "error"]
    private static String dateFormat = "dd.MM.yyyy|HH:mm:ss.SSS"
    private static String logLevel = "info"
    private static Map<String, String> ansiCodes = [
            "debug": "\033[33m",
            "info": "\033[32m",
            "error": "\033[31m"
    ]

    static def setup() {
        _context = Context.get()
        if (_context.LOGLEVEL == "true") {
            logLevel = "debug"
        }
    }

    static def level() {
        return logLevel
    }

    static def debug(String message){
        logMessage("debug", message)
    }

    static def info(String message) {
        logMessage("info", message)
    }

    static def error(Exception e){
        logMessage("error", e.toString())
        _context.error(e)
    }

    private static logMessage(String level, String message){
        if(levels.indexOf(level) >= levels.indexOf(logLevel)){
            SimpleDateFormat formatter = new SimpleDateFormat(dateFormat)
            String date = formatter.format(new Date())
            _context.ansiColor('xterm') {
                _context.println "${ansiCodes[level]}${date} [${level.toUpperCase()}] ${message}\033[0m"
            }
        }
    }
}
