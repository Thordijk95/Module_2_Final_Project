package com.nedap.university;


import com.nedap.university.client.MyClientTUI;
import com.nedap.university.server.PiFileServer;

public class Main {

    private static boolean keepAlive = true;
    private static boolean running = false;

    static final int PORTNUMBER = 8080;
    static final String hostName = "RaspberryPI";

    static final int HEADERSIZE = 10;
    static final int DATAGRAMSIZE = 1024 - HEADERSIZE;



    private Main() {}

    public static void main(String[] args) {
        if (args.length == 0) {  // Start a server
            running = true;
            System.out.println("Hello, Nedap University!");
            //System.out.println("Starting qoute server at port 8080");
            //QuoteServer.main(new String[]{"/home/pi/Quote/Quotes.txt", "8080"});
            PiFileServer.main(new Integer[]{PORTNUMBER, HEADERSIZE, DATAGRAMSIZE});
            initShutdownHook();

            while (keepAlive) {
                try {
                    // do useful stuff
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            System.out.println("Stopped");
            running = false;
        } else {
            //start a client tui
            MyClientTUI.main(args);
        }
    }

    private static void initShutdownHook() {
        final Thread shutdownThread = new Thread() {
            @Override
            public void run() {
                System.out.println("Negate keep alive");
                keepAlive = false;
                while (running) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        };
        Runtime.getRuntime().addShutdownHook(shutdownThread);
    }
}
