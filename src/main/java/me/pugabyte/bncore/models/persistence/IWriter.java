package me.pugabyte.bncore.models.persistence;

import me.pugabyte.bncore.BNCore;
import org.apache.commons.lang.NotImplementedException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IWriter extends IDatabase {
	default void write() {
		write(null);
	}

	default void write(Object object) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = Persistence.getConnection(getDatabase());
			if (connection != null) {
				connection.setAutoCommit(false);
				if (object == null) {
					statement = prepare(connection);
				} else {
					statement = prepare(connection, object);
				}
				connection.commit();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			rollback(connection);
		} finally {
			close(statement);
			close(connection);
		}
	}

	default void rollback(Connection connection) {
		BNCore bnCore = BNCore.getInstance();
		try {
			bnCore.getLogger().severe("Error saving; rolling back data");
			connection.rollback();
		} catch (SQLException ex) {
			ex.printStackTrace();
			bnCore.getLogger().severe("Error rolling back data");
		}
	}

	BearNationDatabase getDatabase();

	default PreparedStatement prepare(Connection connection) throws SQLException {
		throw new NotImplementedException();
	}

	default PreparedStatement prepare(Connection connection, Object object) throws SQLException {
		throw new NotImplementedException();
	}

}
