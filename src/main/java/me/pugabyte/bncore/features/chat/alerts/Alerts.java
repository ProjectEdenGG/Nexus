package me.pugabyte.bncore.features.chat.alerts;

import me.pugabyte.bncore.models.alerts.AlertsService;

import java.util.List;

public class Alerts {
	public Alerts() {
		new AlertsListener();
		new AlertsCommand();
	}

	public void tryAlerts(List<String> uuids, String message) {
		new AlertsService().getAll(uuids).forEach(alerts -> alerts.tryAlerts(message));
	}

}
