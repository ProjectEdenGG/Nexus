package me.pugabyte.bncore.models.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public interface IDatabase {
	default void close(ResultSet result) {
		try {
			if (result != null) result.close();
		} catch (Exception ex) { /* ignored */ }
	}

	default void close(PreparedStatement statement) {
		try {
			if (statement != null) statement.close();
		} catch (Exception ex) { /* ignored */ }
	}

	default void close(Connection connection) {
		try {
			if (connection != null) connection.close();
		} catch (Exception ex) { /* ignored */ }
	}

}
