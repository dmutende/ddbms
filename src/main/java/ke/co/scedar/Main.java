package ke.co.scedar;

import ke.co.scedar.api.Server;
import ke.co.scedar.db.Databases;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

import static org.fusesource.jansi.Ansi.ansi;

public class Main {

    public static void main(String[] args) {

        //Sort out Jansi Console Art
        AnsiConsole.systemInstall();

        System.out.println(ansi().fg(Ansi.Color.WHITE).bold().a("" +
                "████████▄  ████████▄  ▀█████████▄    ▄▄▄▄███▄▄▄▄      ▄████████ \n" +
                "███   ▀███ ███   ▀███   ███    ███ ▄██▀▀▀███▀▀▀██▄   ███    ███ \n" +
                "███    ███ ███    ███   ███    ███ ███   ███   ███   ███    █▀  \n" +
                "███    ███ ███    ███  ▄███▄▄▄██▀  ███   ███   ███   ███        \n" +
                "███    ███ ███    ███ ▀▀███▀▀▀██▄  ███   ███   ███ ▀███████████ \n" +
                "███    ███ ███    ███   ███    ██▄ ███   ███   ███          ███ \n" +
                "███   ▄███ ███   ▄███   ███    ███ ███   ███   ███    ▄█    ███ \n" +
                "████████▀  ████████▀  ▄█████████▀   ▀█   ███   █▀   ▄████████▀").reset());
        System.out.println(ansi().render("@|green Distributed Database Management System API|@ @|white (v0.1-SNAPSHOT)|@"));
        System.out.println();

        /*
         * Initialize databases
         */
        System.out.println(ansi().render("@|green Initializing databases...|@"));
        Databases.initialize();
        System.out.println();

        /*
         * Start the Distributed Database Management Server API
         */
        Server.start();

        System.out.println();
        System.out.println(ansi()
                .fg(Ansi.Color.GREEN).a("Server started at: ")
                .bold().a(Server.host + ":" + Server.port).reset());
        System.out.println("\n");
    }
}
