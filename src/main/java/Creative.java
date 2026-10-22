import org.apache.logging.log4j.core.config.Configurator;
import org.fusesource.jansi.AnsiConsole;
import util.app.AppInfo;

import java.net.URL;
import java.util.Locale;

public class Creative {
    // Ansi constants to display color
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";

    private static String HELP = "-h";
    private static String DEBUG = "-d";

    static {
        try {
            URL configUrl = Thread.currentThread().getContextClassLoader().getResource("log4j2.xml");
            if (configUrl != null) {
                // Forceer de initialisatie direct bij het allereerste JVM-contact
                Configurator.initialize("MyAppCtx", configUrl.toURI().toString());
            } else {
                Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
            }
        } catch (Exception e) {
            Configurator.setRootLevel(org.apache.logging.log4j.Level.OFF);
        }
    }

    /**
     * Main startup application:
     *
     * @param args: additional arguments
     *            -T = test mode
     */
    public static void main(String[] args) {
        AppInfo appInfo = AppInfo.INSTANCE;

        // to render colors in System.out
        System.setProperty("jansi.passthrough", "true");
        AnsiConsole.systemInstall();

        System.out.println(ANSI_RED + "Running Creative Midi with '-h' displays help" + ANSI_RESET);

        for (String arg : args) {
            if (HELP.equals(arg.toLowerCase())) {
                System.out.println(ANSI_BLUE+"Use application with arguments");
                System.out.println("-h this screen");
                System.out.println("-t run in test mode");
                System.out.println("-l switch logging on"+ANSI_RESET);

                AnsiConsole.systemUninstall();
                System.exit(0);
            }

            if (DEBUG.equals(arg.toLowerCase())) {
                appInfo.setDebugMode(true);
            }

        }

        System.out.println(ANSI_GREEN + "Finished Creative Midi neatly" + ANSI_RESET);
        AnsiConsole.systemUninstall();
    }
}
