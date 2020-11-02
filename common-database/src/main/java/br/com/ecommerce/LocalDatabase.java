package br.com.ecommerce;

import java.sql.*;

public class LocalDatabase {

    private final Connection connection;

    public LocalDatabase(String database) throws SQLException {
        String url ="jdbc:sqlite:" + database + ".db";
        this.connection = DriverManager.getConnection(url);
    }

    public void createTableIfNotExists(String sql) throws SQLException {
        PreparedStatement preparedStatement = this.connection.prepareStatement(sql);
        preparedStatement.execute();
    }

    public void update(String sql, Object... params) throws SQLException {
        PreparedStatement preparedStatement = prepare(sql, params);
        preparedStatement.execute();
    }

    public ResultSet query(String sql, Object... params) throws SQLException {
        PreparedStatement preparedStatement = prepare(sql, params);
        return preparedStatement.executeQuery();
    }

    private PreparedStatement prepare(String sql, Object[] params) throws SQLException {
        var preparedStatement = this.connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setObject(i + 1, i);
        }
        return preparedStatement;
    }
}
