package me.pugabyte.bncore.features.chat.alerts.models;

import me.pugabyte.bncore.models.persistence.BearNationDatabase;
import me.pugabyte.bncore.models.persistence.IReader;
import me.pugabyte.bncore.models.persistence.IWriter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlertsDatabase {

	public static class HighlightsReader implements IReader {
		@Override
		public BearNationDatabase getDatabase() {
			return BearNationDatabase.ALERTS;
		}

		@Override
		public PreparedStatement prepare(Connection connection) throws SQLException {
			PreparedStatement statement;
			String sql = "SELECT * FROM `highlights`";
			statement = connection.prepareStatement(sql);
			return statement;
		}

		@Override
		public Map<String, List<Highlight>> map(ResultSet result) throws SQLException {
			Map<String, List<Highlight>> alertsPlayers = new HashMap<>();
			if (result.getMetaData().getColumnCount() > 0) {
				while (result.next()) {
					String uuid = result.getString("uuid");
					String highlight = result.getString("highlight");
					boolean partialMatching = result.getBoolean("partialMatching");
					List<Highlight> highlights;
					if (alertsPlayers.get(uuid) == null) {
						highlights = new ArrayList<>();
					} else {
						highlights = alertsPlayers.get(uuid);
					}
					highlights.add(new Highlight(highlight, partialMatching));
					alertsPlayers.put(uuid, highlights);
				}
			}
			return alertsPlayers;
		}
	}

	public static class HighlightsWriter implements IWriter {
		public BearNationDatabase getDatabase() {
			return BearNationDatabase.ALERTS;
		}

		@Override
		public PreparedStatement prepare(Connection connection, Object object) throws SQLException {
			AlertsPlayer alertsPlayer = (AlertsPlayer) object;
			PreparedStatement statement;

			String sql = "DELETE FROM `highlights` WHERE `uuid` = ?";
			statement = connection.prepareStatement(sql);
			statement.setString(1, alertsPlayer.getUuid());
			statement.executeUpdate();

			sql = "INSERT INTO `highlights` (`uuid`, `highlight`, `partialMatching`) VALUES (?, ?, ?)";
			for (Highlight highlight : alertsPlayer.getHighlights()) {
				statement = connection.prepareStatement(sql);
				statement.setString(1, alertsPlayer.getUuid());
				statement.setString(2, highlight.get());
				statement.setBoolean(3, highlight.isPartialMatching());
				statement.executeUpdate();
			}
			return statement;
		}
	}
}