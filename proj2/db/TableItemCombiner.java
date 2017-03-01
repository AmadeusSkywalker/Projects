package db;

/**
 * Created by ErichRathkamp on 2/27/17.
 */
public class TableItemCombiner {
    private String operation;
    String colOne;
    String colTwo = null;
    String resultName;

    public TableItemCombiner(String combiner) {
        if (combiner.contains("%")) {
            operation = "%";
        } else if (combiner.contains("+")) {
            operation = "+";
        } else if (combiner.contains("-")) {
            operation = "-";
        } else if (combiner.contains("*")) {
            operation = "*";
        } else if (combiner.contains("/")) {
            operation = "/";
        } else if (combiner.equals("")) {
            throw new RuntimeException("Invalid combiner");
        } else {
            operation = "identity";
            colOne = combiner;
        }
    }

    private void colCataloguer(String combiner) {

    }

    public Object combiner(Object item1, Object item2) {
        if (item1 instanceof String && !(item2 instanceof String)) {
            throw new RuntimeException("Invalid comparison between non-string and String: " + item1);
        } else if (item2 instanceof String && !(item1 instanceof String)) {
            throw new RuntimeException("Invalid comparison between non-string and String: " + item2);
        } else if (item1 instanceof String && !(operation.equals("+"))) {
            throw new RuntimeException("Invalid operation: you may only add Strings");
        }
        if (item1 == null || item2 == null) {
            return null; //NaN
        }

        if (item1 instanceof String) { //Item 2 will always be String in this condition
            return (String) item1 + item2;
        } else if (item1 instanceof Integer && item2 instanceof Integer) {
            if (operation.equals("+")) {
                return (Integer) item1 + (Integer) item2;
            } else if (operation.equals("-")) {
                return (Integer) item1 - (Integer) item2;
            } else if (operation.equals("*")) {
                return (Integer) item1 * (Integer) item2;
            } else if (operation.equals("/")) {
                return (Integer) item1 / (Integer) item2;
            } else if (operation.equals("%")) {
                return (Integer) item1 / (Integer) item2;
            }

        } else if (item1 instanceof Float && item2 instanceof Float) {
            if (operation.equals("+")) {
                return (Float) item1 + (Float) item2;
            } else if (operation.equals("-")) {
                return (Float) item1 - (Float) item2;
            } else if (operation.equals("*")) {
                return (Float) item1 * (Float) item2;
            } else if (operation.equals("/")) {
                return (Float) item1 / (Float) item2;
            } else if (operation.equals("%")) {
                return (Float) item1 / (Float) item2;
            }
        } else if (item1 instanceof Float && item2 instanceof Integer) {
            if (operation.equals("+")) {
                return (Float) item1 + (Integer) item2;
            } else if (operation.equals("-")) {
                return (Float) item1 - (Integer) item2;
            } else if (operation.equals("*")) {
                return (Float) item1 * (Integer) item2;
            } else if (operation.equals("/")) {
                return (Float) item1 / (Integer) item2;
            } else if (operation.equals("%")) {
                return (Float) item1 / (Integer) item2;
            }
        } else if (item1 instanceof Integer && item2 instanceof Float) {
            if (operation.equals("+")) {
                return (Integer) item1 + (Float) item2;
            } else if (operation.equals("-")) {
                return (Integer) item1 - (Float) item2;
            } else if (operation.equals("*")) {
                return (Integer) item1 * (Float) item2;
            } else if (operation.equals("/")) {
                return (Integer) item1 / (Float) item2;
            } else if (operation.equals("%")) {
                return (Integer) item1 / (Float) item2;
            }
        }
        return "Non-Table Item";
    }


}
