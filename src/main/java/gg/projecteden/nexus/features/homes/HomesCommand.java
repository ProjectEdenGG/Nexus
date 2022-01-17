package gg.projecteden.nexus.features.homes;

import gg.projecteden.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.utils.Utils.MinMaxResult;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Utils.getMin;

public class HomesCommand extends CustomCommand {
	private final HomeService service = new HomeService();
	private HomeOwner homeOwner;

	public HomesCommand(CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			homeOwner = service.get(player());
	}

	@Path
	void list() {
		List<Home> filtered = new ArrayList<>(homeOwner.getHomes());
		if (isPlayer())
			filtered = filtered.stream().filter(home -> home.hasAccess(player())).collect(Collectors.toList());
		if (filtered.isEmpty())
			error(homeOwner.getNickname() + " has no available homes");

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

	@Path("limit [player]")
	void limit(@Arg(value = "self", permission = Group.STAFF) HomeOwner homeOwner) {
		int homes = homeOwner.getHomes().size();
		int max = homeOwner.getHomesLimit();
		int left = Math.max(0, max - homes);
		if (!isSelf(homeOwner))
			send(PREFIX + homeOwner.getNickname() + " has set &e" + homes + " &3of their &e" + max + " &3homes");
		else {
			send(PREFIX + "You have set &e" + homes + " &3of your &e" + max + " &3homes");

			if (left > 0)
				send(PREFIX + "You can set &e" + left + " &3more");
			else
				send(PREFIX + "&cYou have used all of your available homes! &3To set more homes, you will need to either &erank up &3or purchase more from the &c/store");
		}
	}

	@Async
	@Path("near [page]")
	@Permission(Group.STAFF)
	void near(@Arg("1") int page) {
		Map<Home, Double> unsorted = service.getAll().stream()
				.map(HomeOwner::getHomes)
				.flatMap(Collection::stream)
				.filter(home -> world().equals(home.getLocation().getWorld()))
				.collect(Collectors.toMap(home -> home, home -> home.getLocation().distance(location())));
		Map<Home, Double> homes = Utils.sortByValue(unsorted);

		BiFunction<Home, String, JsonBuilder> formatter = (home, index) ->
				json(index + " &e" + home.getOwner().getNickname() + " &7- " + home.getName() + " (" + homes.get(home).intValue() + "m)")
						.command("/home " + home.getOwner().getNickname() + " " + home.getName())
						.hover("&fClick to teleport");
		paginate(homes.keySet(), formatter, "/homes near", page);
	}

	@Async
	@Confirm
	@Path("lockInWorld <world>")
	@Permission(Group.ADMIN)
	void lockInWorld(World world) {
		int count = 0;
		for (HomeOwner owner : service.getAll()) {
			boolean updated = false;
			for (Home home : homeOwner.getHomes()) {
				if (world.equals(home.getLocation().getWorld()) && !home.isLocked()) {
					++count;
					updated = true;
					home.setLocked(true);
				}
			}
			if (updated)
				service.save(owner);
		}
		send(PREFIX + "Locked " + count + " homes");
	}

	@Async
	@Path("nearest [player]")
	void nearest(@Arg(value = "self", permission = Group.STAFF) OfflinePlayer player) {
		MinMaxResult<Home> result = getMin(service.get(player).getHomes(), home -> {
			if (!world().equals(home.getLocation().getWorld())) return null;
			return location().distance(home.getLocation());
		});

		if (result.getObject() == null)
			error("No homes found in this world");

		send(PREFIX + "Nearest home is &e" + result.getObject().getName() + " &3(&e" + result.getValue().intValue() + " &3blocks away)");
	}

	@Path("reload")
	@Permission(Group.SENIOR_STAFF)
	void reload() {
		service.clearCache();
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("deleteFromWorld <world>")
	void deleteFromWorld(World world) {
		HomesFeature.deleteFromWorld(world.getName(), () ->
				send(json(PREFIX + "Deleted &e" + HomesFeature.getDeleted().size() + " &3homes from null worlds or world &e" + world.getName() + "&3. ")
						.next("&eClick here &3to restore them").command("/homes restoreDeleted")));
	}

	@Async
	@Confirm
	@Permission(Group.ADMIN)
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
	@Permission(Group.ADMIN)
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

			send(PREFIX + "Fixing " + collect.size() + " homes for " + homeOwner.getNickname());

			collect.forEach(home -> home.setUuid(homeOwner.getUuid()));
			service.saveSync(homeOwner);
		});

		send(PREFIX + "Fixed " + fixed.get() + " homes");
	}

	@Async
	@Permission(Group.ADMIN)
	@Path("addExtraHomes <player> <amount>")
	void addExtraHomes(HomeOwner homeOwner, int amount) {
		homeOwner.addExtraHomes(amount);
		service.save(homeOwner);
		send(PREFIX + "Added &e" + amount + " &3homes to &e" + homeOwner.getNickname() + "&3. New limit: &e" + homeOwner.getHomesLimit());
	}

	@Async
	@Permission(Group.ADMIN)
	@Path("removeExtraHomes <player> <amount>")
	void removeExtraHomes(HomeOwner homeOwner, int amount) {
		homeOwner.removeExtraHomes(amount);
		service.save(homeOwner);
		send(PREFIX + "Removed &e" + amount + " &3homes from &e" + homeOwner.getNickname() + "&3. New limit: &e" + homeOwner.getHomesLimit());
	}

}
