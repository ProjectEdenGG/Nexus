package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.features.listeners.Fetch;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;

import java.util.UUID;

public class FetchCommand extends CustomCommand {

	public FetchCommand(CommandEvent event) {
		super(event);
	}

	@Path("on")
	void on() {
		if (!Fetch.enabled) error("Fetch is not currently enabled");
		if (Fetch.fetchers.contains(player().getUniqueId())) error("You are already playing fetch");
		Fetch.fetchers.add(player().getUniqueId());
		send(PREFIX + "You are now playing fetch");
	}

	@Path("off")
	void off() {
		if (!Fetch.enabled) error("Fetch is not currently enabled");
		if (!Fetch.fetchers.contains(player().getUniqueId())) error("You are not playing fetch");
		Fetch.fetchers.remove(player().getUniqueId());
		send(PREFIX + "You are no longer playing fetch");
	}

	@Permission("group.staff")
	@Path("enable")
	void enable() {
		if (Fetch.enabled) error("Fetch is already enabled");
		Fetch.enabled = true;
		send(PREFIX + "Fetch is now enabled");
	}

	@Permission("group.staff")
	@Path("disable")
	void disable() {
		if (!Fetch.enabled) error("Fetch is not enabled");
		Fetch.enabled = false;
		for (UUID uuid : Fetch.fetchers) {
			Utils.getPlayer(uuid).getPlayer().sendMessage(PREFIX + "Fetch has been disabled");
		}
		Fetch.fetchers.clear();
		Fetch.arrows.clear();
		send(PREFIX + "Fetch is now disabled");
	}

	@Path()
	void usage() {
		send(PREFIX + "&cCorrect usage: on/off" + (player().hasPermission("group.staff") ? "/enable/disable" : ""));
	}
}
