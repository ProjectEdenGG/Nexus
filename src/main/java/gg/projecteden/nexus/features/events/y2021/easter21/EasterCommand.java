package gg.projecteden.nexus.features.events.y2021.easter21;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.easter21.Easter21User;
import gg.projecteden.nexus.models.easter21.Easter21UserService;
import gg.projecteden.nexus.models.warps.Warp;
import gg.projecteden.nexus.models.warps.WarpService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Utils;
import gg.projecteden.nexus.utils.Utils.ActionGroup;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static gg.projecteden.nexus.utils.StringUtils.getCoordinateString;
import static gg.projecteden.nexus.utils.StringUtils.getTeleportCommand;
import static java.util.stream.Collectors.toList;

@NoArgsConstructor
public class EasterCommand extends CustomCommand implements Listener {

	public EasterCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[player]")
	void run(@Arg("self") Easter21User user) {
		send(PREFIX + (isSelf(user) ? "You have found" : user.getNickname() + " has found") + " &e" + user.getFound().size() + "/35" + plural(" easter egg", user.getFound().size()));
	}

	@Path("top [page]")
	void top(@Arg("1") int page) {
		List<Easter21User> all = new Easter21UserService().getAll().stream()
				.sorted(Comparator.<Easter21User>comparingInt(user -> user.getFound().size()).reversed())
				.collect(toList());

		int sum = all.stream().mapToInt(user -> user.getFound().size()).sum();

		send(PREFIX + "Top egg hunters  &3|  Total: &e" + sum);
		paginate(all, (user, index) -> json("&3" + index + " &e" + user.getNickname() + " &7- " + user.getFound().size()), "/easter top", page);
	}

	@Path("topLocations [page]")
	@Permission("group.admin")
	void topLocations(@Arg("1") int page) {
		Map<Location, Integer> counts = new HashMap<>() {{
			for (Easter21User user : new Easter21UserService().getAll())
				for (Location location : user.getFound())
					put(location, getOrDefault(location, 0) + 1);
		}};

		send(PREFIX + "Most found eggs");
		BiFunction<Location, String, JsonBuilder> formatter = (location, index) ->
				json("&3" + index + " &e" + getCoordinateString(location) + " &7- " + counts.get(location))
						.command(getTeleportCommand(location))
						.hover("&eClick to teleport");
		paginate(Utils.sortByValueReverse(counts).keySet(), formatter, "/easter topLocations", page);
	}

	@Path("start")
	@Permission("group.admin")
	void start() {
		WarpService warpService = new WarpService();
		List<Warp> locations = warpService.getWarpsByType(WarpType.EASTER21);
		for (Warp warp : locations)
			warp.getLocation().getBlock().setType(Material.DRAGON_EGG);

		send(PREFIX + "Created " + locations.size() + " easter eggs");
	}

	@Path("end")
	@Permission("group.admin")
	void end() {
		WarpService warpService = new WarpService();
		List<Warp> locations = warpService.getWarpsByType(WarpType.EASTER21);
		for (Warp warp : locations)
			warp.getLocation().getBlock().setType(Material.AIR);

		send(PREFIX + "Deleted " + locations.size() + " easter eggs");
	}

	@EventHandler
	public void onEggTeleport(BlockFromToEvent event) {
		Block block = event.getBlock();
		if (event.getBlock().getType() != Material.DRAGON_EGG)
			return;

		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(block.getWorld());
		if (!worldGuardUtils.isInRegion(block.getLocation(), "spawn"))
			return;

		event.setCancelled(true);
	}

	public static final LocalDateTime END = LocalDate.of(2021, 4, 12).atStartOfDay();

	@EventHandler
	public void onEggInteract(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event))
			return;

		Block block = event.getClickedBlock();
		if (block == null)
			return;

		if (block.getType() != Material.DRAGON_EGG)
			return;

		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(block.getWorld());
		if (!worldGuardUtils.isInRegion(block.getLocation(), "spawn"))
			return;

		event.setCancelled(true);

		if (LocalDateTime.now().isAfter(END))
			return;

		Location location = block.getLocation();

		Easter21UserService service = new Easter21UserService();
		Easter21User user = service.get(event.getPlayer());
		user.found(location);
		service.save(user);
	}
}
