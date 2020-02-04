package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashSet;

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
		list(null);
	}

	@Path("list [filter]")
	void list(String filter) {
		send("Homes: " + homeOwner.getNames(filter));
	}

	@Path("allowAll [player]")
	void allowAll(Player player) {
		if (player == null)
			HomesMenu.allowAll(homeOwner, (owner, response) ->
				send(PREFIX + "&e" + Utils.getPlayer(response[0]).getName() + " &3has been granted access to your homes")
			);
		else {
			homeOwner.allowAll(player);
			new HomeService().save(homeOwner);
			send(PREFIX + "&e" + player.getName() + " &3has been granted access to your homes");
		}
	}

	@Path("removeAll [player]")
	void removeAll(Player player) {
		if (player == null)
			HomesMenu.removeAll(homeOwner, (owner, response) ->
				send(PREFIX + "&e" + Utils.getPlayer(response[0]).getName() + " &3no longer has access to your homes")
			);
		else {
			homeOwner.removeAll(player);
			new HomeService().save(homeOwner);
			send(PREFIX + "&e" + player.getName() + " &3no longer has access to your homes");
		}
	}

	@Path("allow [home] [player]")
	void allow(Home home, Player player) {
		if (player == null)
			HomesMenu.allow(home, (owner, response) ->
				send(PREFIX + "&e" + Utils.getPlayer(response[0]).getName() + " &3has been granted access to your home &e" + home.getName())
			);
		else {
			home.allow(player);
			new HomeService().save(homeOwner);
			send(PREFIX + "&e" + player.getName() + " &3has been granted access to your home &e" + home.getName());
		}
	}

	@Path("remove [player]")
	void remove(Home home, Player player) {
		if (player == null)
			HomesMenu.remove(home, (owner, response) ->
				send(PREFIX + "&e" + Utils.getPlayer(response[0]).getName() + " &3no longer has access to your home &e" + home.getName())
			);
		else {
			homeOwner.removeAll(player);
			new HomeService().save(homeOwner);
			send(PREFIX + "&e" + player.getName() + " &3no longer has access to your home &e" + home.getName());
		}
	}


}
