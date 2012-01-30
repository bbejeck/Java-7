package bbejeck.nio.util;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by IntelliJ IDEA.
 * User: bbejeck
 * Date: 1/14/12
 * Time: 12:27 PM
 */

public class FileGenerator {
    private static final String LINE_OF_TEXT =  "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do " +
                                               "eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad " +
                                               "minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea " +
                                               "commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit" +
                                               " esse cillum dolore eu fugiat nulla pariatur. " +
                                               "Excepteur sint occaecat cupidatat non proident, sunt " +
                                               "in culpa qui officia deserunt mollit anim id est laborum.";

    private FileGenerator(){}
    
    
    public static void generate(Path path, int lines) throws Exception {
        try(PrintWriter printWriter = new PrintWriter(Files.newBufferedWriter(path, Charset.defaultCharset()))){
            for(int i = 0; i< lines; i++){
                printWriter.println(LINE_OF_TEXT);
            }
        }
    }
}
