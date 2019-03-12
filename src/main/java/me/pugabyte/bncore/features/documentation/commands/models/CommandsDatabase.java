package me.pugabyte.bncore.features.documentation.commands.models;

import me.pugabyte.bncore.models.persistence.BearNationDatabase;
import me.pugabyte.bncore.models.persistence.IReader;
import me.pugabyte.bncore.models.persistence.IWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CommandsDatabase {

	public static class CommandsReader implements IReader {
		@Override
		public BearNationDatabase getDatabase() {
			return BearNationDatabase.NAMELESS;
		}

		@Override
		public PreparedStatement prepare(Connection connection) throws SQLException {
			PreparedStatement statement;
			String sql = "SELECT * FROM `nl1_commands`";
			statement = connection.prepareStatement(sql);
			return statement;
		}

		@Override
		public List<Command> map(ResultSet result) throws SQLException {
			List<Command> commands = new ArrayList<>();
			if (result.getMetaData().getColumnCount() > 0) {
				while (result.next()) {
					String name = result.getString("command");
					String plugin = result.getString("plugin");
					Command command = new Command(name, plugin);

					command.setUsage(result.getString("gebruik"));
					command.setDescription(result.getString("description"));
					command.setRank(result.getString("rank"));

					String aliasesString = result.getString("aliases");
					if (aliasesString != null) {
						Set<String> aliases = new HashSet<>(Arrays.asList(aliasesString.split(",")));
						command.setAliases(aliases);
					}

					command.setEnabled(result.getBoolean("enabled"));

					commands.add(command);
				}
			}
			return commands;
		}
	}

	public static class CommandsWriter implements IWriter {
		public BearNationDatabase getDatabase() {
			return BearNationDatabase.NAMELESS;
		}

		@Override
		public PreparedStatement prepare(Connection connection, Object object) throws SQLException {
			Command command = (Command) object;
			PreparedStatement statement;

			String sql = "DELETE FROM `nl1_commands` WHERE `command` = ?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, command.getCommand());
			statement.executeUpdate();

			sql = "INSERT INTO `nl1_commands` (`command`, `plugin`, `gebruik`, `description`, `rank`, `aliases`, `enabled`) VALUES (?, ?, ?, ?, ?, ?, ?)";

			statement = connection.prepareStatement(sql);
			statement.setString(1, command.getCommand());
			statement.setString(2, command.getPlugin());
			statement.setString(3, command.getUsage());
			statement.setString(4, command.getDescription());
			statement.setString(5, command.getRank());
			if (command.getAliases() != null) {
				statement.setString(6, String.join(",", command.getAliases()));
			} else {
				statement.setString(6, null);
			}
			statement.setBoolean(7, command.isEnabled());
			statement.executeUpdate();

			return statement;
		}
	}

}
