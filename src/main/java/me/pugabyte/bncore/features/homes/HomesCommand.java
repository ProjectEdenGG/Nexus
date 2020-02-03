package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;
import org.bukkit.entity.Player;

public class HomesCommand extends CustomCommand {
	HomeService service;
	HomeOwner homeOwner;

	public HomesCommand(CommandEvent event) {
		super(event);
		service = new HomeService();
		homeOwner = service.get(player());
	}

	@Path("reload")
	@Permission("group.seniorstaff")
	void reload() {
		service.clearCache();
	}

	@Path("getHomeOwner")
	void getHomeOwner() {
		send("Home owner: " + homeOwner);
	}

	@Path("getHome <name>")
	void getHome(String name) {
		send("Home: " + homeOwner.getHome(name));
	}

	@Path("edit [home]")
	void edit(Home home) {
		if (home == null)
			HomesMenu.edit(homeOwner);
		else
			HomesMenu.edit(home);
	}

	@Path
	void run() {
		list();
	}

	@Path("list")
	void list() {
		send("Homes: " + homeOwner.getNames());
	}

	@Path("allowAll [player]")
	void allowAll(Player player) {
		if (player == null)
			HomesMenu.allowAll(homeOwner);
		else
			homeOwner.allowAll(player);
	}

	@Path("removeAll [player]")
	void removeAll(Player player) {
		if (player == null)
			HomesMenu.removeAll(homeOwner);
		else
			homeOwner.removeAll(player);
	}

	@Path("allow [home] [player]")
	void allow(Home home, Player player) {
		if (player == null)
			HomesMenu.allow(home);
		else
			home.allow(player);
	}

	@Path("remove [player]")
	void remove(Home home, Player player) {
		if (player == null)
			HomesMenu.remove(home);
		else
			homeOwner.removeAll(player);
	}


}
