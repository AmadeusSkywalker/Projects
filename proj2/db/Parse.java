package db;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;


import java.util.StringJoiner;
import java.util.ArrayList;
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

    static String parse(String line,Database x) throws IOException{  //put in the db package and rename the main function
        String result=eval(line,x);
        return result;  //Possible make all of the things below string return type??
    }

    private static String eval(String query,Database x) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            createTable(m.group(1),x);
            return"";
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            try {
                String name=loadTable(m.group(1));
                return x.load(name);
            }
            catch (IOException X){
                String errormess="ERROR: .*";
                return errormess;
            }
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            storeTable(m.group(1));
            return"";
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            String name=dropTable(m.group(1));
            return x.droptable(name);
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

    private static void createTable(String expr,Database x) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            createNewTable(m.group(1), m.group(2).split(COMMA),x);
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            System.err.printf("Malformed create: %s\n", expr);
        }
    }

    private static void createNewTable(String name, String[] cols,Database x) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < cols.length-1; i++) {
            joiner.add(cols[i]);
        }

        String colSentence = joiner.toString() + " " + cols[cols.length-1];
        ArrayList<String> names=new ArrayList<>();
        ArrayList<String> types=new ArrayList<>();
        boolean isend=false;
        while(!isend){
            int index1=colSentence.indexOf(" ");
            String name1=colSentence.substring(0,index1);
            names.add(name1);
            colSentence=colSentence.substring(index1+1);
            int index2=colSentence.indexOf(" ");
            if(index2==-1){
                index2=colSentence.length();
                isend=true;
            }
            String type1=colSentence.substring(0,index2);
            types.add(type1);
            if(!isend){
                colSentence=colSentence.substring(index2+1);
            }
        }
        x.createtable(name,names,types);
    }

    private static void createSelectedTable(String name, String exprs, String tables, String conds) {
        ArrayList<String> expressions = new ArrayList<>();
        while (exprs.contains(",")) {
            int indOfComma = exprs.indexOf(",");
            expressions.add(exprs.substring(0, indOfComma));
            exprs = exprs.substring(indOfComma + 1);
        }
        expressions.add(exprs);

        for (String expr : expressions) {
            if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                    || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                    && !exprs.contains(" as ") && exprs.length() >= (exprs.indexOf("as") + 3)) {
                throw new RuntimeException("Malformed column join: You need an alias");
            }
            expr = expr.replace(" as ", "[as]");
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

    private static String dropTable(String name) {
        return name;
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
