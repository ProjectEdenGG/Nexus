package me.pugabyte.bncore.features.homes;

import me.pugabyte.bncore.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Async;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.home.Home;
import me.pugabyte.bncore.models.home.HomeOwner;
import me.pugabyte.bncore.models.home.HomeService;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Permission("homes.use")
public class HomesCommand extends CustomCommand {
	HomeService service = new HomeService();
	HomeOwner homeOwner;

	public HomesCommand(CommandEvent event) {
		super(event);
		if (isPlayer())
			homeOwner = service.get(player());
	}

	@Path
	void list() {
		List<Home> filtered = homeOwner.getHomes();
		if (isPlayer())
			filtered = filtered.stream().filter(home -> home.hasAccess(player())).collect(Collectors.toList());
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

	@Permission("group.admin")
	@Path("deleteFromWorld <world>")
	void deleteFromWorld(World world) {
		ConfirmationMenu.builder()
				.onConfirm(e ->
						HomesFeature.deleteFromWorld(world.getName(), () ->
								send(json(PREFIX + "Deleted &e" + HomesFeature.getDeleted().size() + " &3homes from null worlds or world &e" + world.getName() + "&3. ")
										.next("&eClick here &3to restore them").command("/homes restoreDeleted"))))
				.open(player());
	}

	@Permission("group.admin")
	@Path("restoreDeleted")
	void restoreDeleted() {
		ConfirmationMenu.builder()
				.onConfirm(e -> Tasks.async(() -> {
					List<Home> deleted = HomesFeature.getDeleted();
					deleted.forEach(home -> {
						home.getOwner().add(home);
						service.save(home.getOwner());
					});

					send(PREFIX + "Restored &e" + deleted.size() + " &3homes");
					deleted.clear();
				}))
				.open(player());
	}

	@Async
	@Permission("group.admin")
	@Path("fixHomeOwner")
	void fixHomeOwner() {
		AtomicInteger fixed = new AtomicInteger(0);
		List<HomeOwner> all = service.getAll();
		all.forEach(homeOwner -> {
			List<Home> collect = homeOwner.getHomes().stream().filter(home ->
					!homeOwner.getUuid().equals(home.getUuid())
			).collect(Collectors.toList());

			if (collect.isEmpty()) return;

			fixed.getAndAdd(collect.size());

			send(PREFIX + "Fixing " + collect.size() + " homes for " + homeOwner.getOfflinePlayer().getName());

			collect.forEach(home -> home.setUuid(homeOwner.getUuid()));
			service.saveSync(homeOwner);
		});

		send(PREFIX + "Fixed " + fixed.get() + " homes");
	}

}
