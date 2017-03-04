package db;


import db.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parse {
    // Various common constructs, simplifies parsing.
    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*",
            AND = "\\s+and\\s+";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\((\\S+\\s+\\S+\\s*" +
            "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+" +
                    "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+" +
                    "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+" +
                    "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+" +
                    SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?" +
                    "\\s*(?:,\\s*.+?\\s*)*)");

    static String parse(String line, Database x) throws IOException {  //put in the db package and rename the main function
        String result = eval(line, x);
        return result;  //Possible make all of the things below string return type??
    }

    private static String eval(String query, Database x) {
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1), x);
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            try {
                String name = loadTable(m.group(1));
                return x.load(name);
            }catch (IOException error){
                return "ERROR: load failed";
            }
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1), x);

        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            String name = dropTable(m.group(1));
            return x.droptable(name);
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1), x);
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            String name = printTable(m.group(1));
            return x.print(name);
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1), x);
        } else {
            System.err.printf("Malformed query: %s\n", query);
            return "";
        }
    }

    private static String createTable(String expr, Database x) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA), x);
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4), x);
        } else {
            System.err.printf("Malformed create: %s\n", expr);
            return "ERROR: You need to have the right format";
        }
    }

    private static String createNewTable(String name, String[] cols, Database x) {
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < cols.length - 1; i++) {
            joiner.add(cols[i]);
        }
        String colSentence = joiner.toString() + " " + cols[cols.length - 1];
        ArrayList<String> names = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        boolean isend = false;
        while (!isend) {
            colSentence=colSentence.trim();
            int index1 = colSentence.indexOf(" ");
            String name1 = colSentence.substring(0, index1);
            names.add(name1);
            colSentence = colSentence.substring(index1 + 1);
            colSentence=colSentence.trim();
            int index2 = colSentence.indexOf(" ");
            if (index2 == -1) {
                index2 = colSentence.length();
                isend = true;
            }
            String type1 = colSentence.substring(0, index2);
            if (type1.contains(",")) {
                type1 = type1.substring(0, type1.length() - 1);
            }
            if(!type1.equals("int")&&!type1.equals("float")&&!type1.equals("string")){
                return "ERROR: You give a wrong type";
            }
            types.add(type1);
            if (!isend) {
                colSentence = colSentence.substring(index2 + 1);
                colSentence=colSentence.trim();
            }
        }
        x.createtable(name, names, types);
        return"";
    }


    private static String createSelectedTable(String name, String exprs, String tables, String conds, Database db) {
        ArrayList<String> expressions = new ArrayList<>();
        while (exprs.contains(",")) {
            int indOfComma = exprs.indexOf(",");
            String term = exprs.substring(0, indOfComma);
            if ((term.contains("%") || term.contains("+") || term.contains("-")
                    || (term.contains("*") && term.length() > 1) || exprs.contains("/"))
            && !(term.contains(" as "))){
                System.err.printf("Malformed column join: no as");
            }
            term = term.replace(" as ", "[as]");
            term = term.replaceAll("\\s+","");
            expressions.add(term);
            exprs = exprs.substring(indOfComma + 1);
        }
        if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                && !(exprs.contains(" as "))){
            System.err.printf("Malformed column join: no as");
        }
        exprs = exprs.replace(" as ", "[as]");
        exprs = exprs.replaceAll("\\s+","");
        expressions.add(exprs);


        for (String expr : expressions) {
            if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                    || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                    && !(exprs.length() >= (exprs.indexOf("[as]") + 4))) {
                System.err.printf("Malformed column join: You need an alias");
            }
        }

        //exprs = exprs.replaceAll("\\s+",""); This and all instances from next line url
        //http://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        //Also spaces and newlines are for losers

        ArrayList<String> conditions = new ArrayList<>();
        while (conds != null && conds.contains(" and ")) {
            int indOfAnd = conds.indexOf(" and ");
            String term = conds.substring(0, indOfAnd);
            conds = conds.substring(indOfAnd + 5);
            term = term.replaceAll("\\s+","");
            conditions.add(term);
        }
        if (conds != null) {
            conds = conds.replaceAll("\\s+","");
            conditions.add(conds);
        }



        for (String cond : conditions) {
            if (!(cond.contains(">") || cond.contains("<") || cond.contains("!=")
                    || cond.contains("==") || cond.contains(">="))) {
                System.err.printf("Malformed column condition: No comparator");
            }

        }

        ArrayList<String> tableList = new ArrayList<>();
        while (tables.contains(",")) {
            int indOfComma = tables.indexOf(",");
            String term = tables.substring(0, indOfComma);
            term = term.replaceAll("\\s+","");
            tableList.add(term);
            tables = tables.substring(indOfComma + 1);
        }
        tables = tables.replaceAll("\\s+","");
        tableList.add(tables);

        db.addTable(db.select(name, expressions, tableList, conditions));
        return "";
    }


    private static String loadTable(String name) {
        return name;
    }

    private static String storeTable(String name, Database db) {
        db.store(name);
        return "";
    }

    private static String dropTable(String name) {
        return name;
    }

    private static String insertRow(String expr, Database x) {
        try {
            Matcher m = INSERT_CLS.matcher(expr);
            if (!m.matches()) {
                return "ERROR: Malformed insert: %s\n" + expr + "\n";
            }
            int index1 = expr.indexOf(" ");
            String tablename = expr.substring(0, index1);
            Table t1 = x.getDatabase().get(tablename);
            ArrayList<String> coltypes = t1.getColTypes();
            expr = expr.substring(index1 + 1);
            expr = expr.trim();
            int index2 = expr.indexOf(" ");
            expr = expr.substring(index2 + 1);
            expr = expr.trim();
            ArrayList<TableItem> rowcontent = new ArrayList<>();
            int checkindex = 0;
            boolean isend = false;
            while (!isend) {
                if (checkindex == coltypes.size()) {
                    if (!expr.equals("")) {
                        return "ERROR: Row doesn't match table" + "\n";
                    }
                    break;
                }
                if (expr.substring(0, 1).equals("")) {
                    isend = true;
                    if (checkindex < coltypes.size()) {
                        return "ERROR: Row doesn't match table" + "\n";
                    }
                }
                if (expr.substring(0, 1).equals("'")) {
                    if (!coltypes.get(checkindex).equals("string")) {
                        return "ERROR: Row doesn't match table" + "\n";
                    } else {
                        expr = expr.substring(1);
                        int quoteindex = expr.indexOf("'");
                        String tobeadd = expr.substring(0, quoteindex);
                        TableItem realtobeadd = new TableItem(tobeadd);
                        rowcontent.add(realtobeadd);
                        expr = expr.substring(quoteindex + 1);
                        int commaindex = expr.indexOf(",");
                        expr = expr.substring(commaindex + 1);
                        expr = expr.trim();
                    }
                } else {
                    int commaindex = expr.indexOf(",");
                    String tobeadd = expr.substring(0, commaindex);
                    if (tobeadd.contains(".")) {
                        if (!coltypes.get(checkindex).equals("float")) {
                            return "ERROR: Row doesn't match type" + "\n";
                        }
                        float realthing = Float.valueOf(tobeadd);
                        TableItem realstuff = new TableItem(realthing);
                        rowcontent.add(realstuff);
                    } else {
                        if (!coltypes.get(checkindex).equals("int")) {
                            return "ERROR: Row doesn't match type" + "\n";
                        }
                        int realthing = Integer.valueOf(tobeadd);
                        TableItem realstuff = new TableItem(realthing);
                        rowcontent.add(realstuff);
                    }
                    expr = expr.substring(commaindex + 1);
                    expr = expr.trim();
                }
                checkindex += 1;
            }
            t1.addRow(rowcontent);
            return "";
        } catch (RuntimeException error) {
            return "Error: Malformed Dataentry" + "\n";
        }
    }


    private static String printTable(String name) {
        return name;
    }

    private static String select(String expr, Database db) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            return "ERROR: " + "Malformed select: %s\n" + expr;
        }

        return select(m.group(1), m.group(2), m.group(3), db);
    }

    private static String select(String exprs, String tables, String conds, Database db) {
        ArrayList<String> expressions = new ArrayList<>();
        while (exprs.contains(",")) {
            int indOfComma = exprs.indexOf(",");
            String term = exprs.substring(0, indOfComma);
            if ((term.contains("%") || term.contains("+") || term.contains("-")
                    || (term.contains("*") && term.length() > 1) || exprs.contains("/"))
                    && !(term.contains(" as "))) {
                System.err.printf("Malformed column join: no as");
            }
            term = term.replace(" as ", "[as]");
            term = term.replaceAll("\\s+", "");
            expressions.add(term);
            exprs = exprs.substring(indOfComma + 1);
        }
        if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                && !(exprs.contains(" as "))) {
            System.err.printf("Malformed column join: no as");
        }
        exprs = exprs.replace(" as ", "[as]");
        exprs = exprs.replaceAll("\\s+", "");
        expressions.add(exprs);


        for (String expr : expressions) {
            if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                    || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                    && !(exprs.length() >= (exprs.indexOf("[as]") + 4))) {
                System.err.printf("Malformed column join: You need an alias");
            }
        }

        //exprs = exprs.replaceAll("\\s+",""); This and all instances from next line url
        //http://stackoverflow.com/questions/5455794/removing-whitespace-from-strings-in-java
        //Also spaces and newlines are for losers

        ArrayList<String> conditions = new ArrayList<>();
        while (conds != null && conds.contains(" and ")) {
            int indOfAnd = conds.indexOf(" and ");
            String term = conds.substring(0, indOfAnd);
            conds = conds.substring(indOfAnd + 5);
            term = term.replaceAll("\\s+", "");
            conditions.add(term);
        }
        if (conds != null) {
            conds = conds.replaceAll("\\s+", "");
            conditions.add(conds);
        }


        for (String cond : conditions) {
            if (!(cond.contains(">") || cond.contains("<") || cond.contains("!=")
                    || cond.contains("==") || cond.contains(">="))) {
                System.err.printf("Malformed column condition: No comparator");
            }

        }

        ArrayList<String> tableList = new ArrayList<>();
        while (tables.contains(",")) {
            int indOfComma = tables.indexOf(",");
            String term = tables.substring(0, indOfComma);
            term = term.replaceAll("\\s+", "");
            tableList.add(term);
            tables = tables.substring(indOfComma + 1);
        }
        tables = tables.replaceAll("\\s+", "");
        tableList.add(tables);

        return db.select("dummy", expressions, tableList, conditions).printtable();
    }
}
