package com.microsoft.sqlserver.jdbc;

import java.util.ArrayList;

public class ConfigRetryRule {
    private String retryError;
    private String operand = "*";
    private int initialRetryTime = 10;
    private int retryChange = 2;
    private int retryCount;
    private String retryQueries = "";
    private ArrayList<Integer> waitTimes = new ArrayList<>();
    private boolean isConnection = false;
    private boolean replaceExisting = false;

    public ConfigRetryRule(String s) {
        String[] stArr = parse(s);
        addElements(stArr);
        calcWaitTime();
    }

    public ConfigRetryRule(String rule, ConfigRetryRule r) {
        copyFromCopy(r);
        this.retryError = rule;
    }

    private void copyFromCopy(ConfigRetryRule r) {
        this.retryError = r.getError();
        this.operand = r.getOperand();
        this.initialRetryTime = r.getInitialRetryTime();
        this.retryChange = r.getRetryChange();
        this.retryCount = r.getRetryCount();
        this.retryQueries = r.getRetryQueries();
        this.waitTimes = r.getWaitTimes();
        this.isConnection = r.getConnectionStatus();
    }

    private String[] parse(String s) {
        String temp = s + " ";

        temp = temp.replace(": ",":0");
        temp = temp.replace("{", "");
        temp = temp.replace("}", "");
        temp = temp.trim();

        return temp.split(":");
    }

    private void addElements(String[] s) {
        if (s.length == 1) {
            isConnection = true;
            retryError = appendOrReplace(s[0]); //Not quite, 1st we see if append on replace.
        } else if (s.length == 2 || s.length == 3) {
            // TODO: HANDLE MISSING VALUES
            retryError = s[0];

            //parse timing elements - COUNT AND INITIAL TIME BOTH MANDATORY
            String[] st = s[1].split(",");

            // We have retry count AND timing rules
            retryCount = Integer.parseInt(st[0]); //TODO input validation
            // Second half can either be N, N OP, N OP N
            if (st[1].contains("*")) {
                // We know its either N OP, N OP N
                String[] sss = st[1].split("/*");
                initialRetryTime = Integer.parseInt(sss[0]);
                operand = "*";
                if (sss.length > 2) {
                    retryChange = Integer.parseInt(sss[2]);
                }
            } else if (st[1].contains("+")) {
                // We know its either N OP, N OP N
                String[] sss = st[1].split("/+");
                initialRetryTime = Integer.parseInt(sss[0]);
                operand = "*";
                if (sss.length > 2) {
                    retryChange = Integer.parseInt(sss[2]);
                }
            } else {
                initialRetryTime = Integer.parseInt(st[1]);
                //TODO set defaults
            }
            if (s.length == 3) {
                retryQueries = (s[2].equals("0") ? "" : s[2].toLowerCase());
            }
        } else {
            // TODO: If a different length, then throw an error of some sort.
        }
    }

    private String appendOrReplace(String s) {
        if (s.isEmpty())
            return s;
        if (s.charAt(0) == '+') {
            replaceExisting = false;
            return s.substring(1);
        } else {
            replaceExisting = true;
            return s;
        }
    }

    public void calcWaitTime() {
        for (int i = 0; i < retryCount; ++i) {
            int waitTime = initialRetryTime;
            if (operand.equals("+")) {
                for (int j = 0; j < i; ++j) {
                    waitTime += retryChange;
                }
            } else if (operand.equals("*")) {
                for (int k = 0; k < i; ++k) {
                    waitTime *= retryChange;
                }

            }
            waitTimes.add(waitTime);
        }
    }

    public String getError() {
        return retryError;
    }

    public String getOperand() {
        return operand;
    }

    public int getInitialRetryTime() {
        return initialRetryTime;
    }

    public int getRetryChange() {
        return retryChange;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public boolean getConnectionStatus() {
        return isConnection;
    }

    public String getRetryQueries() {
        return retryQueries;
    }

    public ArrayList<Integer> getWaitTimes() {
        return waitTimes;
    }

    public boolean getReplaceExisting() {
        return replaceExisting;
    }
}