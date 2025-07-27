package gg.projecteden.nexus.features.events.y2021.easter21;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.features.warps.commands._WarpSubCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.easter21.Easter21User;
import gg.projecteden.nexus.models.easter21.Easter21UserService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.models.warps.Warps.Warp;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
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
import java.util.stream.Collectors;

@Disabled
@HideFromWiki
@NoArgsConstructor
public class Easter21Command extends _WarpSubCommand implements Listener {
	public static final LocalDateTime END = LocalDate.of(2021, 4, 12).atStartOfDay();

	public Easter21Command(@NonNull CommandEvent event) {
		super(event);
	}

	@Override
	public WarpType getWarpType() {
		return WarpType.EASTER21;
	}

	@Path("[player]")
	void run(@Arg("self") Easter21User user) {
		send(PREFIX + (isSelf(user) ? "You have found" : user.getNickname() + " has found") + " &e" + user.getFound().size() + "/35" + plural(" easter egg", user.getFound().size()));
	}

	@Path("top [page]")
	void top(@Arg("1") int page) {
		List<Easter21User> all = new Easter21UserService().getAll().stream()
				.sorted(Comparator.<Easter21User>comparingInt(user -> user.getFound().size()).reversed())
				.collect(Collectors.toList());

		int sum = all.stream().mapToInt(user -> user.getFound().size()).sum();

		send(PREFIX + "Top egg hunters  &3|  Total: &e" + sum);
		new Paginator<Easter21User>()
			.values(all)
			.formatter((user, index) -> json(index + " &e" + user.getNickname() + " &7- " + user.getFound().size()))
			.command("/easter top")
			.page(page)
			.send();
	}

	@Path("topLocations [page]")
	@Permission(Group.ADMIN)
	void topLocations(@Arg("1") int page) {
		Map<Location, Integer> counts = new HashMap<>() {{
			for (Easter21User user : new Easter21UserService().getAll())
				for (Location location : user.getFound())
					put(location, getOrDefault(location, 0) + 1);
		}};

		send(PREFIX + "Most found eggs");
		BiFunction<Location, String, JsonBuilder> formatter = (location, index) ->
				json(index + " &e" + StringUtils.xyz(location) + " &7- " + counts.get(location))
						.command(StringUtils.getTeleportCommand(location))
						.hover("&eClick to teleport");
		new Paginator<Location>()
			.values(Utils.sortByValueReverse(counts).keySet())
			.formatter(formatter)
			.command("/easter topLocations")
			.page(page)
			.send();
	}

	@Path("start")
	@Permission(Group.ADMIN)
	void start() {
		List<Warp> locations = WarpType.EASTER21.getAll();
		for (Warp warp : locations)
			warp.getLocation().getBlock().setType(Material.DRAGON_EGG);

		send(PREFIX + "Created " + locations.size() + " easter eggs");
	}

	@Path("end")
	@Permission(Group.ADMIN)
	void end() {
		List<Warp> locations = WarpType.EASTER21.getAll();
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

		new Easter21UserService().edit(event.getPlayer(), user -> user.found(location));
	}
}
