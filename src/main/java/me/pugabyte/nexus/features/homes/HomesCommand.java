package me.pugabyte.nexus.features.homes;

import eden.utils.Utils.MinMaxResult;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Confirm;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.home.Home;
import me.pugabyte.nexus.models.home.HomeOwner;
import me.pugabyte.nexus.models.home.HomeService;
import me.pugabyte.nexus.utils.JsonBuilder;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.Utils.getMin;

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
		List<Home> filtered = new ArrayList<>(homeOwner.getHomes());
		if (isPlayer())
			filtered = filtered.stream().filter(home -> home.hasAccess(player())).collect(Collectors.toList());
		if (filtered.isEmpty())
			error(homeOwner.getOfflinePlayer().getName() + " has no available homes");

		send(PREFIX + filtered.stream().map(home -> (home.isLocked() ? "&c" : "&3") + home.getName())
				.collect(Collectors.joining("&e, ")));
	}

	@Path("<player>")
	void list(OfflinePlayer player) {
		if (args().size() > 1) {
			runCommand("home " + player.getName() + " " + arg(2));
			return;
		}
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

	@Async
	@Path("near [page]")
	@Permission("group.staff")
	void nearest(@Arg("1") int page) {
		Map<Home, Double> unsorted = service.getAll().stream()
				.map(HomeOwner::getHomes)
				.flatMap(Collection::stream)
				.filter(home -> world().equals(home.getLocation().getWorld()))
				.collect(Collectors.toMap(home -> home, home -> home.getLocation().distance(location())));
		Map<Home, Double> homes = Utils.sortByValue(unsorted);

		BiFunction<Home, String, JsonBuilder> formatter = (home, index) ->
				json("&3" + index + " &e" + home.getOwner().getNickname() + " &7- " + home.getName() + " (" + homes.get(home).intValue() + "m)")
						.command("/home " + home.getOwner().getNickname() + " " + home.getName())
						.hover("&fClick to teleport");
		paginate(homes.keySet(), formatter, "/homes near", page);
	}

	@Async
	@Path("nearest [player]")
	void nearest(@Arg(value = "self", permission = "group.staff") OfflinePlayer player) {
		MinMaxResult<Home> result = getMin(service.get(player).getHomes(), home -> {
			if (!world().equals(home.getLocation().getWorld())) return null;
			return location().distance(home.getLocation());
		});

		if (result.getObject() == null)
			error("No homes found in this world");

		send(PREFIX + "Nearest home is &e" + result.getObject().getName() + " &3(&e" + result.getValue().intValue() + " &3blocks away)");
	}

	@Path("reload")
	@Permission("group.seniorstaff")
	void reload() {
		service.clearCache();
	}

	@Confirm
	@Permission("group.admin")
	@Path("deleteFromWorld <world>")
	void deleteFromWorld(World world) {
		HomesFeature.deleteFromWorld(world.getName(), () ->
				send(json(PREFIX + "Deleted &e" + HomesFeature.getDeleted().size() + " &3homes from null worlds or world &e" + world.getName() + "&3. ")
						.next("&eClick here &3to restore them").command("/homes restoreDeleted")));
	}

	@Async
	@Confirm
	@Permission("group.admin")
	@Path("restoreDeleted")
	void restoreDeleted() {
		List<Home> deleted = HomesFeature.getDeleted();
		deleted.forEach(home -> {
			home.getOwner().add(home);
			service.save(home.getOwner());
		});

		send(PREFIX + "Restored &e" + deleted.size() + " &3homes");
		deleted.clear();
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
