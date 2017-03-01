package db;

import java.awt.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;


import java.util.StringJoiner;

public class Parse {
    // Various common constructs, simplifies parsing.
    private static final String REST  = "\\s*(.*)\\s*",
                                COMMA = "\\s*,\\s*",
                                AND   = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
                                 LOAD_CMD   = Pattern.compile("load " + REST),
                                 STORE_CMD  = Pattern.compile("store " + REST),
                                 DROP_CMD   = Pattern.compile("drop table " + REST),
                                 INSERT_CMD = Pattern.compile("insert into " + REST),
                                 PRINT_CMD  = Pattern.compile("print " + REST),
                                 SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW  = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
                                               "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
                                 SELECT_CLS  = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                                               "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                                               "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+" +
                                               "[\\w\\s+\\-*/'<>=!]+?)*))?"),
                                 CREATE_SEL  = Pattern.compile("(\\S+)\\s+as select\\s+" +
                                                   SELECT_CLS.pattern()),
                                 INSERT_CLS  = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                                               "\\s*(?:,\\s*.+?\\s*)*)");

    static String parse(String line,Database x) {  //put in the db package and rename the main function
        String result=eval(line,x);
        return result;  //Possible make all of the things below string return type??
    }

    private static String eval(String query,Database x) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1));
            return"";
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            try {
                String name=loadTable(m.group(1));
                return x.load(name);
            }
            catch (IOException X){
                System.out.println("couldn't read file");
                return "";
            }
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            storeTable(m.group(1));
            return"";
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            dropTable(m.group(1));
            return "";
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            insertRow(m.group(1));
            return "";
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            String name=printTable(m.group(1));
            return x.print(name);
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            select(m.group(1));
            return "";
        } else {
            System.err.printf("Malformed query: %s\n", query);
            return "";
        }
    }

    private static void createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("Malformed create: %s\n", expr);
        }
    }

    private static void createNewTable(String name, String[] cols) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < cols.length-1; i++) {
            joiner.add(cols[i]);
        }

        String colSentence = joiner.toString() + " and " + cols[cols.length-1];
        System.out.printf("You are trying to create a table named %s with the columns %s\n", name, colSentence);
    }

    private static void createSelectedTable(String name, String exprs, String tables, String conds) {
        if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
            || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                && !exprs.contains(" as ") && exprs.length() >= (exprs.indexOf("as") + 3)) {
            throw new RuntimeException("Malformed column join: You need an alias");
        }

        exprs = exprs.replaceAll("\\s+",""); //This and all instances from next line url
        //http://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        //Also spaces and newlines are for losers

        System.out.printf("You are trying to create a table named %s by selecting these expressions:" +
                " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", name, exprs, tables, conds);
    }

    private static String loadTable(String name) {
        System.out.printf("You are trying to load the table named %s\n", name);
        return name;
    }

    private static void storeTable(String name) {
        System.out.printf("You are trying to store the table named %s\n", name);
    }

    private static void dropTable(String name) {
        System.out.printf("You are trying to drop the table named %s\n", name);
    }

    private static void insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed insert: %s\n", expr);
            return;
        }

        System.out.printf("You are trying to insert the row \"%s\" into the table %s\n", m.group(2), m.group(1));
    }

    private static String printTable(String name) {
        System.out.printf("You are trying to print the table named %s\n", name);
        return name;
    }

    private static void select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            System.err.printf("Malformed select: %s\n", expr);
            return;
        }

        select(m.group(1), m.group(2), m.group(3));
    }

    private static void select(String exprs, String tables, String conds) {
        System.out.printf("You are trying to select these expressions:" +
                " '%s' from the join of these tables: '%s', filtered by these conditions: '%s'\n", exprs, tables, conds);
    }
}
