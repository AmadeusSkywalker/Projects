package db;

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

    static String parse(String line, Database x) throws IOException {  //put in the Old_db package and rename the main function
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
                return x.load(name, x);
            } catch (IOException y) {
                return "ERROR: Load failed";
            }

        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1), x);

        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            String name = dropTable(m.group(1));
            return x.droptable(name);
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow1(m.group(1), x);
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            String name = printTable(m.group(1));
            return x.print(name);
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1), x);
        } else {
            System.err.printf("Malformed query: %s\n", query);
            return "ERROR: Malformed query";
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


    private static String createSelectedTable(String name, String exprs, String tables, String conds, Database dab) {
        ArrayList<String> expressions = new ArrayList<>();
        while (exprs.contains(",")) {
            int indOfComma = exprs.indexOf(",");
            String term = exprs.substring(0, indOfComma);
            if ((term.contains("%") || term.contains("+") || term.contains("-")
                    || (term.contains("*") && term.length() > 1) || term.contains("/"))
            && !(term.contains(" as "))){
                return "ERROR: Malformed column join: no as";
            }
            term = term.replace(" as ", "[as]");
            term = term.replaceAll("\\s+","");
            expressions.add(term);
            exprs = exprs.substring(indOfComma + 1);
        }
        if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                && !(exprs.contains(" as "))){
            return "ERROR: Malformed column join: no as";
        }
        exprs = exprs.replace(" as ", "[as]");
        exprs = exprs.replaceAll("\\s+","");
        expressions.add(exprs);


        for (String expr : expressions) {
            if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                    || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                    && !(exprs.length() >= (exprs.indexOf("[as]") + 4))) {
                return "ERROR: Malformed column join: You need an alias";
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
                return "ERROR: Malformed column condition: No comparator";
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

        try {
            dab.addTable(dab.select(name, expressions, tableList, conditions));
        } catch (RuntimeException x) {
            return x.getMessage();
        }
        return "";
    }


    private static String loadTable(String name) {
        return name;
    }

    private static String storeTable(String name, Database db) {
        return db.store(name);
    }

    private static String dropTable(String name) {
        return name;
    }


    private static String insertRow1(String expr,Database x){
        try {
            Matcher m = INSERT_CLS.matcher(expr);
            if (!m.matches()) {
                return "ERROR: Malformed insert";
            }
            int index1 = expr.indexOf(" ");
            String tablename = expr.substring(0, index1);
            Table t1 = x.getDatabase().get(tablename);
            ArrayList<String> coltypes = t1.colTypes;
            expr = expr.substring(index1 + 1);
            expr = expr.trim();
            int index2 = expr.indexOf(" ");
            expr = expr.substring(index2 + 1);
            expr = expr.trim();
            ArrayList<TableItem> rowcontent = new ArrayList<>();
            int checkindex = 0;
            boolean isend = false;
            while(!isend){
                if (checkindex == coltypes.size()) {
                    if (!expr.equals("")) {
                        return "ERROR: You have too many newlines";
                    }
                    break;
                }
                if (expr.substring(0, 1).equals("")) {
                    isend = true;
                    if (checkindex < coltypes.size()) {
                        return "ERROR: You don't have enough newlines";
                    }
                }
                else {
                    int commaindex=expr.indexOf(",");
                    if(commaindex==-1){
                        commaindex=expr.length();
                        isend=true;
                    }
                    String tobeadd=expr.substring(0,commaindex);
                    tobeadd=tobeadd.trim();
                    if (coltypes.get(checkindex).equals("string")) {
                        if (tobeadd.equals("NaN")) {
                            return "ERROR: String cannot be NaN";
                        } else if (tobeadd.equals("NOVALUE")) {
                            TableItem newitem = new TableItem(tobeadd);
                            newitem.NOVALUE = true;
                            newitem.item = "";
                            rowcontent.add(newitem);
                        }
                        else {
                            if (tobeadd.charAt(0) != '\''
                                    || tobeadd.charAt(tobeadd.length() - 1) != '\'') {
                                return "ERROR: Incorrect String format";
                            } else {
                                tobeadd = tobeadd.substring(1, tobeadd.length() - 1);
                                TableItem newitem = new TableItem(tobeadd);
                                rowcontent.add(newitem);
                            }
                        }
                    }else if (coltypes.get(checkindex).equals("float")) {
                        boolean isnovalue=false;
                        try{
                            float temp=Float.parseFloat(tobeadd);
                        }catch(NumberFormatException ex){
                            if(!tobeadd.equals("NOVALUE")){
                                return "ERROR: Should have a float here";
                            }
                            else{
                                TableItem newItem=new TableItem(new Float(0.0));
                                newItem.NOVALUE = true;
                                newItem.item = new Float(0.0);
                                rowcontent.add(newItem);
                                isnovalue=true;
                            }
                        }
                        if(!isnovalue) {
                            TableItem newItem = new TableItem(Float.valueOf(tobeadd));
                            if (tobeadd.equals("NaN")) {
                                newItem.NaN = true;
                            }
                            rowcontent.add(newItem);
                        }
                    }else if (coltypes.get(checkindex).equals("int")) {
                        boolean isnovalue = false;
                        try {
                            int temp = Integer.parseInt(tobeadd);
                        } catch (NumberFormatException ex) {
                            if (!tobeadd.equals("NOVALUE")) {
                                return "ERROR: Should have a int here";
                            } else {
                                TableItem newItem = new TableItem(new Integer(0));
                                newItem.NOVALUE = true;
                                newItem.item = new Integer(0);
                                rowcontent.add(newItem);
                                isnovalue = true;
                            }
                        }
                        if (!isnovalue) {
                            TableItem newItem = new TableItem(Integer.valueOf(tobeadd));
                            if (tobeadd.equals("NaN")) {
                                return "ERROR: String cannot be NaN";
                            }
                            rowcontent.add(newItem);
                        }
                    }
                    if(commaindex!=expr.length()){
                        expr = expr.substring(commaindex + 1);
                        expr = expr.trim();
                    }
                }
                checkindex=checkindex+1;
            }
            t1.addRow(rowcontent);
            return " ";
        }catch (RuntimeException ex){
            return "ERROR: Malformed Data Entry";
        }
    }


    private static String printTable(String name) {
        return name;
    }

    private static String select(String expr, Database db) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            return "ERROR: Malformed select"; // + "Malformed select: %s\n" + expr;
        }
        return select(m.group(1), m.group(2), m.group(3), db);
    }

    private static String select(String exprs, String tables, String conds, Database db) {
        ArrayList<String> expressions = new ArrayList<>();
        while (exprs.contains(",")) {
            int indOfComma = exprs.indexOf(",");
            String term = exprs.substring(0, indOfComma);
            if ((term.contains("%") || term.contains("+") || term.contains("-")
                    || (term.contains("*") && term.length() > 1) || term.contains("/"))
                    && !(term.contains(" as "))) {
                return "ERROR: Malformed column join: no as";
            }
            term = term.replace(" as ", "[as]");
            term = term.replaceAll("\\s+", "");
            expressions.add(term);
            exprs = exprs.substring(indOfComma + 1);
        }
        if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                && !(exprs.contains(" as "))) {
            return "ERROR: Malformed column join: no as";
        }
        exprs = exprs.replace(" as ", "[as]");
        exprs = exprs.replaceAll("\\s+", "");
        expressions.add(exprs);


        for (String expr : expressions) {
            if ((exprs.contains("%") || exprs.contains("+") || exprs.contains("-")
                    || (exprs.contains("*") && exprs.length() > 1) || exprs.contains("/"))
                    && !(exprs.length() >= (exprs.indexOf("[as]") + 4))) {
                return "ERROR: Malformed column join: You need an alias";
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
                return "ERROR: Malformed column condition: No comparator";
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
