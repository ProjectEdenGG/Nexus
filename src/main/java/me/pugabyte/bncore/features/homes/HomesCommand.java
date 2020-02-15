package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class HomesCommand extends CustomCommand {
	HomeService service = new HomeService();
	HomeOwner homeOwner;

	public HomesCommand(CommandEvent event) {
		super(event);
		homeOwner = service.get(player());
	}

	@Path
	void list() {
		List<Home> filtered = homeOwner.getHomes().stream().filter(home -> home.hasAccess(player())).collect(Collectors.toList());
		if (filtered.size() == 0)
			error(homeOwner.getOfflinePlayer().getName() + " has no available homes");

		send(PREFIX + filtered.stream().map(home -> (home.isLocked() ? "&c" : "&3") + home.getName())
				.collect(Collectors.joining("&e, ")));
	}

	@Path("<player>")
	void list(OfflinePlayer player) {
		homeOwner = service.get(player);
		list();
	}

	@Path("edit [home]")
	void edit(Home home) {
		if (home == null)
			HomesMenu.edit(homeOwner);
		else
			HomesMenu.edit(home);
	}

	@Path("allowAll [player]")
	void allowAll(Player player) {
		if (player == null)
			HomesMenu.allowAll(homeOwner, (owner, response) -> {
				if (response[0].length() > 0)
					send(PREFIX + "&e" + Utils.getPlayer(response[0]).getName() + " &3has been granted access to your homes");
			});
		else {
			homeOwner.allowAll(player);
			new HomeService().save(homeOwner);
			send(PREFIX + "&e" + player.getName() + " &3has been granted access to your homes");
		}
	}

	@Path("removeAll [player]")
	void removeAll(Player player) {
		if (player == null)
			HomesMenu.removeAll(homeOwner, (owner, response) -> {
				if (response[0].length() > 0)
					send(PREFIX + "&e" + Utils.getPlayer(response[0]).getName() + " &3no longer has access to your homes");
			});
		else {
			homeOwner.removeAll(player);
			new HomeService().save(homeOwner);
			send(PREFIX + "&e" + player.getName() + " &3no longer has access to your homes");
		}
	}

	@Path("allow <home> [player]")
	void allow(Home home, Player player) {
		if (player == null)
			HomesMenu.allow(home, (owner, response) -> {
				if (response[0].length() > 0)
					send(PREFIX + "&e" + Utils.getPlayer(response[0]).getName() + " &3has been granted access to your home &e" + home.getName());
			});
		else {
			home.allow(player);
			new HomeService().save(homeOwner);
			send(PREFIX + "&e" + player.getName() + " &3has been granted access to your home &e" + home.getName());
		}
	}

	@Path("remove <home> [player]")
	void remove(Home home, Player player) {
		if (player == null)
			HomesMenu.remove(home, (owner, response) -> {
				if (response[0].length() > 0)
					send(PREFIX + "&e" + Utils.getPlayer(response[0]).getName() + " &3no longer has access to your home &e" + home.getName());
			});
		else {
			homeOwner.removeAll(player);
			new HomeService().save(homeOwner);
			send(PREFIX + "&e" + player.getName() + " &3no longer has access to your home &e" + home.getName());
		}
	}

	@Path("limit")
	void limit() {
		int homes = homeOwner.getHomes().size();
		int max = homeOwner.getMaxHomes();
		int left = Math.max(0, max - homes);
		send(PREFIX + "You have set &e" + homes + " &3of your &e" + max + " &3homes");
		if (left > 0)
			send(PREFIX + "You can set &e" + left + " &3more");
		else
			send(PREFIX + "&cYou have used all of your available homes! &3To set more homes, you will need to either &erank up &3or &c/donate");
	}

	@Path("reload")
	@Permission("group.seniorstaff")
	void reload() {
		service.clearCache();
	}

}
