package me.pugabyte.bncore.features.homes;

import ch.njol.skript.variables.Variables;
import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;
import com.earth2me.essentials.UserMap;
import lombok.SneakyThrows;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.homes.Home;
import me.pugabyte.bncore.models.homes.HomeOwner;
import me.pugabyte.bncore.models.homes.HomeService;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import net.ess3.api.InvalidWorldException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class HomesCommand extends CustomCommand {
	HomeService service;
	HomeOwner homeOwner;

	public HomesCommand(CommandEvent event) {
		super(event);
		service = new HomeService();
		homeOwner = service.get(player());
	}


	@Path("getHomeOwner")
	void getHomeOwner() {
		send("Home owner: " + homeOwner);
	}

	@Path("getHome <name>")
	void getHome(String name) {
		send("Home: " + homeOwner.getHome(name).get());
	}

	@SneakyThrows
	@Path("migrate")
	void migrate() {
		Tasks.async(() -> {
			long startTime = System.currentTimeMillis();

			Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
			UserMap userMap = essentials.getUserMap();
			userMap.getAllUniqueUsers().forEach(uuid -> {
				try {
					User user = userMap.getUser(uuid);
					if (user.getHomes() == null || user.getHomes().size() == 0)
						return;

					HomeOwner homeOwner = service.get(uuid);

					Object autolock = getSkriptVariable("homes::" + uuid.toString() + "::autolock");

					if (autolock instanceof Boolean)
						homeOwner.setAutoLock((Boolean) autolock);

					Object allowAll = getSkriptVariable("homes::" + uuid.toString() + "::allowAll::*");
					if (allowAll != null)
						((Map) allowAll).keySet().forEach(allowAllUuid ->
								homeOwner.getFullAccessList().add(UUID.fromString((String) allowAllUuid)));

					for (String homeName : user.getHomes()) {
						try {
							Home.HomeBuilder builder = Home.builder()
									.uuid(uuid)
									.name(homeName)
									.location(user.getHome(homeName));

							Object locked = getSkriptVariable("homes::" + uuid.toString() + "::locked::" + homeName);
							if (locked instanceof Boolean)
								builder.locked((Boolean) locked);

							Object allowed = getSkriptVariable("homes::" + uuid.toString() + "::allowed::" + homeName + "::*");
							Set<UUID> accessList = new HashSet<>();
							if (allowed != null)
								((Map) allowed).keySet().stream()
										.map(allowedUuid -> UUID.fromString((String) allowedUuid))
										.filter(allowedUuid -> !homeOwner.getFullAccessList().contains(allowedUuid))
										.forEach(allowedUuid -> accessList.add((UUID) allowedUuid));
							builder.accessList(accessList);

							homeOwner.add(builder.build());
						} catch (InvalidWorldException ignore) {}
					}

					service.save(homeOwner);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			});

			send(PREFIX + "Migration took " + (System.currentTimeMillis() - startTime) + "ms");
		});
	}

	private Object getSkriptVariable(String id) {
		return Variables.getVariable(id, null, false);
	}


	@Path
	void list() {
		send("Homes: " + homeOwner.getNames());
	}

	@Path("<player>")
	void list(Player player) {
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

	@Path("reload")
	@Permission("group.seniorstaff")
	void reload() {
		service.clearCache();
	}

}
