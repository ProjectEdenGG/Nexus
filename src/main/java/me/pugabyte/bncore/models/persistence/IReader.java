package me.pugabyte.bncore.models.persistence;

import org.apache.commons.lang.NotImplementedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface IReader extends IDatabase {
	default Object read() {
		return read(null);
	}

	default Object read(Object object) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet result = null;
		Object data = null;

		try {
			connection = Persistence.getConnection(getDatabase());
			if (connection != null) {
				if (object == null) {
					statement = prepare(connection);
				} else {
					statement = prepare(connection, object);
				}
				result = statement.executeQuery();
				data = map(result);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			close(result);
			close(statement);
			close(connection);
		}
		return data;
	}

	BearNationDatabase getDatabase();

	default PreparedStatement prepare(Connection connection) throws SQLException {
		throw new NotImplementedException();
	}

	default PreparedStatement prepare(Connection connection, Object object) throws SQLException {
		throw new NotImplementedException();
	}

	Object map(ResultSet result) throws SQLException;

}
