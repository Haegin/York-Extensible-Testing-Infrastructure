package yeti.environments.java;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;

import yeti.YetiCard;

public class YetiBugList {
    
    /**
     * 
     * Class that stores the bugs found when running Yeti on Java code.
     * 
     * @author Harry Mills (hm539@cs.york.ac.uk)
     * @date 22 Feb 2012
     *
     */
    
    private List<YetiBug> bugList;
    
    public YetiBugList() {
        bugList = new LinkedList<YetiBug>();
    }
    
    public void addBug(String className, String methodName, String unitTest, YetiCard[] args, Throwable throwable, Integer lineNumber) {
        bugList.add(new YetiBug(className, methodName, unitTest, args, throwable, lineNumber));
    }
    
    public void addBug(YetiBug bug) {
        bugList.add(bug);
    }
    
    public List<YetiBug> getBugs() {
        return bugList;
    }
    
    public void clearBugs() {
        bugList.clear();
    }
    
    public boolean isEmpty() {
        return bugList.isEmpty();
    }
    
    public boolean contains(YetiBug bug) {
        return bugList.contains(bug);
    }
    
    /**
     * This method saves the bug list to a database as a new session created with the passed in details
     * @param sessionName is the name of the session as it will appear in the database.
     * @param conn is the connection to the database you wish the test session to be stored in
     */
    public void saveToDB(String sessionName) {
        Connection conn = null;
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/YETI", "postgres", "postgres");
            System.out.println("Connected to DB");
            Date currTime = new Date();
            Statement stmt = conn.createStatement();
            /* The table will autogenerate IDs so we need to insert the name and time */
            String insertBugList = "INSERT INTO testsession (sessionname, time) VALUES ('" + sessionName + "', '" + new java.sql.Timestamp(currTime.getTime()) + "');";
            stmt.execute(insertBugList, Statement.RETURN_GENERATED_KEYS);
            System.out.println("Created TestSession in DB");
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int testSessionKey = rs.getInt(3);
                
                /* Insert each bug in the bug tables */
                /* Params: testsession, className, methodName, unitTest, stacktrace */
                String bugSQL = "INSERT INTO bug (testsession, classname, methodname, unittest, stacktrace) VALUES (" + testSessionKey + ", ?, ?, ?, ?);";
                PreparedStatement bugPStmt = conn.prepareStatement(bugSQL, Statement.RETURN_GENERATED_KEYS);
                
                /* Params: bug, position, type, value */
                String argSQL = "INSERT INTO arg (bug, position, argtype, argvalue) VALUES (?, ?, ?, ?);";
                PreparedStatement argPStmt = conn.prepareStatement(argSQL);
                ResultSet bugRS;
                int bugKey;
                int argIdx = 0;
                for (YetiBug bug : bugList) {
                    bugPStmt.setString(1, bug.getClassName());
                    bugPStmt.setString(2, bug.getMethodName());
                    bugPStmt.setString(3, bug.getUnitTest());
                    bugPStmt.setString(4, bug.getStackTraceWithoutYeti());
                    bugPStmt.executeUpdate();
                    bugRS = bugPStmt.getGeneratedKeys();
                    if (bugRS.next()) {
                        bugKey = bugRS.getInt(1);
                        argIdx = 0;
                        for (YetiCard arg : bug.getArgs()) {
                            argPStmt.setInt(1, bugKey);
                            argPStmt.setInt(2, argIdx);
                            argPStmt.setString(3, arg.toStringVariable());
                            argPStmt.setString(4, arg.toStringPrefix());
                            argIdx += 1;
                            argPStmt.execute();
                        }
                    }
                }
            } else {
                System.out.println("ERROR: could not get the primary key for the test session.");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
}
