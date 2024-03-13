package gg.projecteden.nexus.features.homes;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.utils.Utils.MinMaxResult;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.home.Home;
import gg.projecteden.nexus.models.home.HomeOwner;
import gg.projecteden.nexus.models.home.HomeService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
	@Description("List your homes")
	void list() {
		List<Home> filtered = new ArrayList<>(homeOwner.getHomes());
		if (isPlayer())
			filtered = filtered.stream().filter(home -> home.hasAccess(player())).collect(Collectors.toList());
		if (filtered.isEmpty())
			error(homeOwner.getNickname() + " has no available homes");

		send(PREFIX + filtered.stream().map(home -> (home.isLocked() ? "&c" : "&3") + home.getName())
				.collect(Collectors.joining("&e, ")));
	}

	@Path("help")
	@Override
	@Description("Learn about our Homes system and the commands it offers")
	public void help() {
		line();
		send(json("&3[+] &c/sethome [homename]").hover("&eSet a home.", "&3Excluding a home name will set your default", "&3home. It can be teleported to with &c/h").suggest("/sethome "));
		send(json("&3[+] &c/delhome [homename]").hover("&eDelete a home.").suggest("/delhome "));
		send(json("&3[+] &c/h [homename]").hover("&eTeleport to one of your set homes.", "&3Excluding a home name will teleport", "&3you to your home called '&ehome&3'.").suggest("/h"));
		send(json("&3[+] &c/h <playername> <homename>").hover("&eTeleport to another player's home.", "&3Please be respectful of people's privacy.").suggest("/h "));
		send(json("&3[+] &c/homes edit").hover("&ePrevent people from accessing your", "&ehomes, allow certain people to", "&ebypass the locks, and more!").suggest("/homes edit"));
		send(json("&3[+] &c/homes limit").hover("&eView how many homes", "&eyou are able to set").suggest("/homes limit"));
		line();
		send(json("&3 « &eHelp Menu").command("/help"));
	}

	@Path("<player>")
	@Description("List another player's homes")
	void list(OfflinePlayer player) {
		if (args().size() > 1) {
			runCommand("home " + player.getName() + " " + arg(2));
			return;
		}
		homeOwner = service.get(player);
		list();
	}

	@Path("edit [home]")
	@Description("Open the homes edit menu")
	void edit(Home home) {
		if (home == null)
			HomesMenu.edit(homeOwner);
		else
			HomesMenu.edit(home);
	}

	@Path("limit [player]")
	@Description("View how many homes you can set")
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
	@Description("View all nearby homes")
	void near(@Arg("1") int page) {
		Map<Home, Double> unsorted = service.getAll().stream()
				.map(HomeOwner::getHomes)
				.flatMap(Collection::stream)
				.filter(home -> world().equals(home.getLocation().getWorld()))
				.collect(Collectors.toMap(home -> home, home -> distanceTo(home).getRealDistance()));
		Map<Home, Double> homes = Utils.sortByValue(unsorted);

		BiFunction<Home, String, JsonBuilder> formatter = (home, index) ->
				json(index + " &e" + home.getOwner().getNickname() + " &7- " + home.getName() + " (" + homes.get(home).intValue() + "m)")
						.command("/home " + home.getOwner().getNickname() + " " + home.getName())
						.hover("&fClick to teleport");
		paginate(homes.keySet(), formatter, "/homes near", page);
	}

	@Async
	@Path("nearest [player]")
	@Description("View your nearest home")
	void nearest(@Arg(value = "self", permission = Group.STAFF) OfflinePlayer player) {
		MinMaxResult<Home> result = getMin(service.get(player).getHomes(), home -> {
			if (!world().equals(home.getLocation().getWorld()))
				return null;
			return distanceTo(home.getLocation()).get();
		});

		if (result.getObject() == null)
			error("No homes found in this world");

		int distance = (int) Math.sqrt(result.getValue().intValue());
		send(PREFIX + "Nearest home is &e" + result.getObject().getName() + " &3(&e" + distance + " &3blocks away)");
	}

	@Async
	@Confirm
	@Path("lockInWorld <world>")
	@Permission(Group.ADMIN)
	@Description("Lock all homes in a world")
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

	@Path("reload")
	@Permission(Group.SENIOR_STAFF)
	@Description("Clear service cache")
	void reload() {
		service.clearCache();
	}

	@Confirm
	@Permission(Group.ADMIN)
	@Path("deleteFromWorld <world>")
	@Description("Delete homes from a world")
	void deleteFromWorld(World world) {
		HomesFeature.deleteFromWorld(world.getName(), () ->
				send(json(PREFIX + "Deleted &e" + HomesFeature.getDeleted().size() + " &3homes from null worlds or world &e" + world.getName() + "&3. ")
						.next("&eClick here &3to restore them").command("/homes restoreDeleted")));
	}

	@Async
	@Confirm
	@Permission(Group.ADMIN)
	@Path("restoreDeleted")
	@Description("Restore recently deleted homes in a world")
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
	@Path("addExtraHomes <player> <amount>")
	@Description("Increase a player's home limit")
	void addExtraHomes(HomeOwner homeOwner, int amount) {
		homeOwner.addExtraHomes(amount);
		service.save(homeOwner);
		send(PREFIX + "Added &e" + amount + " &3homes to &e" + homeOwner.getNickname() + "&3. New limit: &e" + homeOwner.getHomesLimit());
	}

	@Async
	@Permission(Group.ADMIN)
	@Path("removeExtraHomes <player> <amount>")
	@Description("Decrease a player's home limit")
	void removeExtraHomes(HomeOwner homeOwner, int amount) {
		homeOwner.removeExtraHomes(amount);
		service.save(homeOwner);
		send(PREFIX + "Removed &e" + amount + " &3homes from &e" + homeOwner.getNickname() + "&3. New limit: &e" + homeOwner.getHomesLimit());
	}

}
