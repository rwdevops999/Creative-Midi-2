import org.apache.logging.log4j.core.config.Configurator;
import org.fusesource.jansi.AnsiConsole;

import java.net.URL;

import static util.Constants.ANSI_RED;
import static util.Constants.ANSI_RESET;

public class Creative {
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
        // to render colors in System.out
        AnsiConsole.systemInstall();

        System.out.println(ANSI_RED + "Running Creative Midi with '-h' displays help" + ANSI_RESET);

        for (String arg : args) {
            System.out.println("Found arg: " + arg);
        }

        AnsiConsole.systemUninstall();
    }
}
