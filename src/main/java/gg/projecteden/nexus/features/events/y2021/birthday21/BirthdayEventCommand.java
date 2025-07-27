package gg.projecteden.nexus.features.events.y2021.birthday21;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.birthday21.Birthday21User;
import gg.projecteden.nexus.models.birthday21.Birthday21UserService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LocationUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.Vector;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Disabled
@HideFromWiki
@NoArgsConstructor
public class BirthdayEventCommand extends CustomCommand implements Listener {

	public BirthdayEventCommand(@NonNull CommandEvent event) {
		super(event);
	}

	public static final int MAX_CAKES = 24;

	@Path("[player]")
	void run(@Arg("self") Birthday21User user) {
		send(PREFIX + (isSelf(user) ? "You have found" : user.getNickname() + " has found") + " &e" + user.getFound().size() + "/" + MAX_CAKES + plural(" cake", user.getFound().size()));
	}

	@Path("top [page]")
	void top(@Arg("1") int page) {
		List<Birthday21User> all = new Birthday21UserService().getAll().stream()
			.sorted(Comparator.<Birthday21User>comparingInt(user -> user.getFound().size()).reversed())
			.collect(Collectors.toList());

		int sum = all.stream().mapToInt(user -> user.getFound().size()).sum();

		send(PREFIX + "Top cake hunters  &3|  Total: &e" + sum);
		new Paginator<Birthday21User>()
			.values(all)
			.formatter((user, index) -> json(index + " &e" + user.getNickname() + " &7- " + user.getFound().size()))
			.command("/birthdayevent top")
			.page(page)
			.send();
	}

	@Path("topLocations [page]")
	@Permission(Group.ADMIN)
	void topLocations(@Arg("1") int page) {
		Map<Location, Integer> counts = new HashMap<>() {{
			for (Birthday21User user : new Birthday21UserService().getAll())
				for (Location location : user.getFound())
					put(location, getOrDefault(location, 0) + 1);
		}};

		send(PREFIX + "Most found cakes");
		new Paginator<Location>()
			.values(Utils.sortByValueReverse(counts).keySet())
			.formatter((location, index) -> json(index + " &e" + StringUtils.xyz(location) + " &7- " + counts.get(location))
				.command(StringUtils.getTeleportCommand(location))
				.hover("&eClick to teleport")
			)
			.command("/birthdayevent topLocations")
			.page(page)
			.send();
	}

	public static final LocalDateTime END = LocalDate.of(2021, 8, 11).atStartOfDay();

	// i ran out of time to make a good solution for this
	// 1. Head Database API's method to get the ID of a skull block returns null
	// 2. PersistentDataContainer seems to just do nothing for skulls?? (i tried to store a "IsCake" boolean on the skull)
	// 3. warps command had some weird bug and refused to work
	// so enjoy this set of vectors
	private final Set<Vector> CAKES = Set.of(
		// South
		new Vector(-9, 81, 5024),
		new Vector(-11, 90, 5004),
		new Vector(-2, 107, 5034),
		new Vector(30, 63, 5005),
		new Vector(-8, 79, 5011),
		// East
		new Vector(5018, 71, -15),
		new Vector(5019, 71, -26),
		new Vector(4978, 66, -37),
		new Vector(4963, 72, 13),
		new Vector(4991, 77, 6),
		new Vector(5033, 78, 3),
		new Vector(4984, 66, 27),
		new Vector(5007, 63, -11),
		// North
		new Vector(-4, 82, -5005),
		new Vector(6, 67, -4986),
		new Vector(4, 75, -4985),
		new Vector(-2, 65, -5016),
		new Vector(2, 64, -5020),
		new Vector(-1, 62, -4994),
		new Vector(3, 65, -5015),
		// West
		new Vector(-4998, 86, -1),
		new Vector(-5020, 87, -6),
		new Vector(-4974, 75, -30),
		new Vector(-4986, 68, -55)
	);

	@EventHandler
	public void onCakeInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
			return;
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (!"survival".equals(block.getWorld().getName()))
			return;

		if (block.getType() != Material.PLAYER_HEAD && block.getType() != Material.PLAYER_WALL_HEAD)
			return;

		Vector blockVec = block.getLocation().toVector();
		if (CAKES.stream().noneMatch(vector -> LocationUtils.vectorLocationsEqual(vector, blockVec)))
			return;

		event.setCancelled(true);

		if (LocalDateTime.now().isAfter(END)) {
			event.getPlayer().sendMessage(JsonBuilder.fromPrefix("Birthdays").next("You've found a birthday cake from the 2021 Griffin & Wakka birthday party"));
			return;
		}

		Location location = block.getLocation();

		new Birthday21UserService().edit(event.getPlayer(), user -> user.found(location));
	}
}
