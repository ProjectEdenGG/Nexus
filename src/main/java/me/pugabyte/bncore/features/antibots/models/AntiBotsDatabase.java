package me.pugabyte.bncore.features.antibots.models;

import me.pugabyte.bncore.models.persistence.BearNationDatabase;
import me.pugabyte.bncore.models.persistence.IReader;
import me.pugabyte.bncore.models.persistence.IWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static me.pugabyte.bncore.features.antibots.AntiBotsCommand.getAllowed;
import static me.pugabyte.bncore.features.antibots.AntiBotsCommand.getDenied;

public class AntiBotsDatabase {
	public static class AllowedReader implements IReader {
		@Override
		public BearNationDatabase getDatabase() {
			return BearNationDatabase.ANTIBOTS;
		}

		@Override
		public PreparedStatement prepare(Connection connection) throws SQLException {
			PreparedStatement statement;
			String sql = "SELECT * FROM `allowed`";
			statement = connection.prepareStatement(sql);
			return statement;
		}

		@Override
		public Set<String> map(ResultSet result) throws SQLException {
			Set<String> list = new HashSet<>();
			if (result.getMetaData().getColumnCount() > 0) {
				while (result.next()) {
					String ip = result.getString("ip");
					list.add(ip);
				}
			}
			return list;
		}
	}

	public static class DeniedReader implements IReader {
		public BearNationDatabase getDatabase() {
			return BearNationDatabase.ANTIBOTS;
		}

		@Override
		public PreparedStatement prepare(Connection connection) throws SQLException {
			PreparedStatement statement;
			String sql = "SELECT * FROM `denied`";
			statement = connection.prepareStatement(sql);
			return statement;
		}

		@Override
		public Set<DeniedEntry> map(ResultSet result) throws SQLException {
			Set<DeniedEntry> list = new HashSet<>();
			if (result.getMetaData().getColumnCount() > 0) {
				while (result.next()) {
					String ip = result.getString("ip");
					String uuid = result.getString("uuid");
					String name = result.getString("name");
					LocalDateTime timestamp = result.getTimestamp("timestamp").toLocalDateTime();
					list.add(new DeniedEntry(ip, uuid, name, timestamp));
				}
			}
			return list;
		}
	}

	public static class AllowedWriter implements IWriter {
		public BearNationDatabase getDatabase() {
			return BearNationDatabase.ANTIBOTS;
		}

		@Override
		public PreparedStatement prepare(Connection connection) throws SQLException {
			PreparedStatement statement;
			String sql = "INSERT IGNORE INTO `allowed` (`ip`) VALUES (?)";
			statement = connection.prepareStatement(sql);

			for (String ip : getAllowed()) {
				statement.setString(1, ip);
				statement.executeUpdate();
			}
			return statement;
		}
	}

	public static class DeniedWriter implements IWriter {
		public BearNationDatabase getDatabase() {
			return BearNationDatabase.ANTIBOTS;
		}

		@Override
		public PreparedStatement prepare(Connection connection) throws SQLException {
			PreparedStatement statement = null;
			String sql = "INSERT IGNORE INTO `denied` (`ip`, `uuid`, `name`, `timestamp`) VALUES (?, ?, ?, ?)";
			for (DeniedEntry entry : getDenied()) {
				statement = connection.prepareStatement(sql);
				statement.setString(1, entry.getIp());
				statement.setString(2, entry.getUuid());
				statement.setString(3, entry.getName());
				statement.setTimestamp(4, Timestamp.valueOf(entry.getTimestamp()));
				statement.executeUpdate();
			}
			return statement;
		}
	}

}
