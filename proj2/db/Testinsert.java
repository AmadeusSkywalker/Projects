package db;

import db.Database;

/**
 * Created by vip on 3/3/17.
 */
public class Testinsert {
    public static void main(String[] args) {
        Database sql = new Database();
        /*
        sql.load("fans");
        sql.transact("insert into fans values 'a', 'b' ,  'v'");
        */
        /*
        sql.transact("create table t (x string)");
        sql.transact("create table t1 (a    string   ,  b   string  ,    c    string  ,  d  string)");
        */
        sql.transact("load test4");
        sql.transact("load teams");
        sql.transact("create table t1 (money int)");
        sql.transact("insert into t1 values      -2147483648");
    }
}
