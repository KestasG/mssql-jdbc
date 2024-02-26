package com.microsoft.sqlserver.jdbc;

import java.sql.*;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Test connection and statement retry for configurable retry logic
 */
public class CRL {

    static String tableName = "test_decimal";
    public static void main(String[] args) throws SQLException {

        String connectionString = "jdbc:sqlserver://localhost:1433;database=TestDb;user=sa;password=TestPassword123;" +
                "encrypt=true;trustServerCertificate=true;selectMethod=cursor;loginTimeout=5;" +
                "connectRetryCount=1;";


        try(Connection conn = DriverManager.getConnection(connectionString);
                Statement s = conn.createStatement()) {
            PreparedStatement ps = conn.prepareStatement("create table test_decimal (c1 int, c2 int, c3 int, c4 int, c5 int,);");
            try {
                createTable(s);
            } catch (SQLServerException e) {
                ps.execute();
            }
        }
    }

    // Global
    public void readFromBadPropsFile() {
        // Include multiple props file, badly formatted in props file
    }
    public void inputValidation() {
        // Test empty rules, bad rules, bad values, incomplete rule
        // For now connection only
    }

    // Connection
    //+"retryExec={+4060,4070};"
    public void readConnectionRuleFromConnectionPropAndReplace() {

    }
    public void readConnectionRuleFromConnectionPropAndAppend() {}
    public void readConnectionRuleFromPropertiesFileAndReplace() {}
    public void readConnectionRuleFromPropertiesFileAndAppend() {}

    // Statement
    //+"retryExec={2714,2716:1,2*2:CREATE;2715:1,3;+4060,4070};"

    private static void createTable(Statement stmt) throws SQLException {
        //String sql = "create table " + tableName + " (c1 int, c2 int, c3 int);";
        String sql = "create table " + tableName + " (";
        for (int i = 1; i <= 5; ++i) {
            sql += "c" + i + " int, ";
        }
        sql = sql.substring(0,sql.length() - 1);
        sql += ");";
        stmt.execute(sql);
    }
    private static void dropTable(Statement stmt) throws SQLException {
        if (null != tableName) {
            TestUtils.dropTableIfExists(tableName, stmt);
        }
    }
}
