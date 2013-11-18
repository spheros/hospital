package com.exigen.school.hospital.server.storage.jdbc;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Sergey Kharaborkin
 * @version 1.1 28.10.13
 */


public class JdbcConnection implements JdbcConfig {
    private Logger logger;
    private static JdbcConnection instance = null;
    Connection connection;
    Statement statement;
    PreparedStatement preparedStatement;
    ResultSet resultSet;


    public static JdbcConnection getInstance() throws ClassNotFoundException, SQLException {
        return (instance != null) ? instance :
                new JdbcConnection(CONNECTION_URL, JDBC_DRIVER, USERNAME, PASS);
    }

    private JdbcConnection(String url, String driverName,
                           String user, String passwd) throws ClassNotFoundException, SQLException {
        if (logger == null) {
            logger = Logger.getLogger(this.getClass().getName());
            logger.setLevel(LOG_LEVEL);
        }

        Class.forName(driverName);
        logger.log(Level.INFO, OPEN_DB_CONNECTION);

        connection = DriverManager.getConnection(url, user, passwd);
        statement = connection.createStatement();
        initDatabase();
    }

    private void initDatabase() throws SQLException {
        statement.execute(CREATE_TABLE_DOCTORS_QUERY);
        statement.execute(CREATE_TABLE_PATIENTS_QUERY);
        statement.execute(CREATE_TABLE_REGCARDS_QUERY);
        statement.execute(ADD_REGCARDS_FK_ON_DOCTORS_QUERY);
        statement.execute(ADD_REGCARDS_FK_ON_PATIENTS_QUERY);
        statement.execute(ADD_UNIQUE_REGCARD_CONSTRAINT_QUERY);
        statement.close();
        logger.log(Level.INFO, INIT_DB_TABLES);
    }


    public ResultSet executeQuery(String query) throws SQLException {
        if (connection == null || statement == null) {
            logger.log(Level.INFO, ABSENT_DATABASE);
            return null;
        }
        try {
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
            return resultSet;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw ex;
        }
    }

    public boolean execute(String query) throws SQLException {
        if (connection == null || statement == null) {
            logger.log(Level.INFO, ABSENT_DATABASE);
            return false;
        }

        try {
            statement = connection.createStatement();
            statement.execute(query);
            return true;
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
            throw ex;
        }
    }

    public void close() throws SQLException {
        logger.log(Level.INFO, CLOSE_DB_CONNECTION);
        resultSet.close();
        statement.close();
        preparedStatement.close();
        connection.close();
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }

}
