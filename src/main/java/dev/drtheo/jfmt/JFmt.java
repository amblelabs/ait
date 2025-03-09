package dev.drtheo.jfmt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class JFmt {

    private static final String WELCOME = """
                                                               ,----,\s
                     ,---._                      ____        ,/   .`|\s
                   .-- -.' \\     ,---,.        ,'  , `.    ,`   .'  :\s
                   |    |   :  ,'  .' |     ,-+-,.' _ |  ;    ;     /\s
                   :    ;   |,---.'   |  ,-+-. ;   , ||.'___,/    ,' \s
                   :        ||   |   .' ,--.'|'   |  ;||    :     |  \s
                   |    :   ::   :  :  |   |  ,', |  ':;    |.';  ;  \s
                   :         :   |  |-,|   | /  | |  ||`----'  |  |  \s
                   |    ;   ||   :  ;/|'   | :  | :  |,    '   :  ;  \s
               ___ l         |   |   .';   . |  ; |--'     |   |  '  \s
             /    /\\    J   :'   :  '  |   : |  | ,        '   :  |  \s
            /  ../  `..-    ,|   |  |  |   : '  |/         ;   |.'   \s
            \\    \\         ; |   :  \\  ;   | |`-'          '---'     \s
             \\    \\      ,'  |   | ,'  |   ;/                        \s
              "---....--'    `----'    '---'                         \s
                                                                     \s
            """;

    public static void main(String[] args) {
        System.out.println(WELCOME);

        Path path;

        if (args.length >= 1) {
            path = Path.of(args[0]);
        } else {
            Path local = Path.of(".");

            if (Files.exists(local.resolve("build.gradle"))) {
                path = local.resolve("src");
            } else {
                path = local;
            }
        }

        handleIO(() -> recurseFiles(path));
    }

    private static void recurseFiles(Path root) throws IOException {
        Files.list(root).forEach(path -> {
            if (Files.isDirectory(path)) {
                handleIO(() -> recurseFiles(path));
                return;
            }

            if (path.toString().endsWith(".java"))
                format(path);
        });
    }

    private static void handleIO(IORunnable runnable) {
        try {
            runnable.run();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    interface IORunnable {
        void run() throws IOException;
    }

    private static void format(Path file) {

    }
}
