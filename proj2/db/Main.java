package db;

import db.Database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Main {
    private static final String EXIT = "exit";
    private static final String PROMPT = "> ";

    public static void main(String[] args) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            Database db = new Database();
            System.out.print(PROMPT);

            String line = "";
            while ((line = in.readLine()) != null) {
                if (EXIT.equals(line)) {
                    break;
                }

                if (!line.trim().isEmpty()) {
                    String result = db.transact(line);
                    if (result.length() > 0) {
                        System.out.print(result);
                    }
                }
                System.out.print(PROMPT);
            }

            in.close();
        } catch (IOException x) {
            System.out.println("Couldn't read file");
        }
    }
}
