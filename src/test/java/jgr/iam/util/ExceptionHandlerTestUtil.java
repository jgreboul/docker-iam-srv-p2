package jgr.iam.util;

// External Objects
import static org.junit.jupiter.api.Assertions.fail; // https://junit.org/junit5/docs/5.0.1/api/org/junit/jupiter/api/Assertions.html
import org.apache.logging.log4j.Logger; // https://logging.apache.org/log4j/2.x/manual/api.html

// Exception Util to output the Stack Trace
public class ExceptionHandlerTestUtil {

    // Handle Test Exception
    public static void Handle(Logger logger, String sFunc, Exception e) {
        logger.error(sFunc + ": " + e.getClass().getCanonicalName() + ": " + e.getMessage());
        logger.error(sFunc + ": " + ExceptionHandlerTestUtil.toString(e));
        fail(sFunc + ":" + e.getClass().getCanonicalName() );
    }

    // Provide the Exception Stack
    public static String toString(Exception e) {
        String stackTrace = "";
        if (e != null) {
            StackTraceElement[] traceElements = e.getStackTrace();
            for (StackTraceElement traceElement : traceElements) {
                // Code to be executed for each element of the array
                stackTrace += "\t" + traceElement.toString() + "\n";
            }
        }
        return stackTrace;
    }
}
