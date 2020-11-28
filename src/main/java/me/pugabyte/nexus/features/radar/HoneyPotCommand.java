package me.pugabyte.nexus.features.radar;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.nexus.models.honeypot.HoneyPotGriefer;
import me.pugabyte.nexus.models.honeypot.HoneyPotGrieferService;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.WorldEditUtils;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.Set;

@NoArgsConstructor
@Permission("group.staff")
@Aliases({"hp", "honeypots"})
public class HoneyPotCommand extends CustomCommand implements Listener {
	private final HoneyPotGrieferService service = new HoneyPotGrieferService();
	private HoneyPotGriefer griefer;

	private WorldGuardUtils WGUtils;
	private WorldEditUtils WEUtils;
	private RegionManager regionManager;

	public HoneyPotCommand(CommandEvent event) {
		super(event);
		WGUtils = new WorldGuardUtils(player());
		WEUtils = new WorldEditUtils(player());
		regionManager = WGUtils.getManager();
	}

	@Path("check <player>")
	@Permission("group.seniorstaff")
	void check(@Arg("self") OfflinePlayer player) {
		griefer = service.get(player);
		send(PREFIX + "&e" + player.getName() + "&3 has griefed &e" + griefer.getTriggered() + " times");
	}

	@Path("set <player> <int>")
	@Permission("group.seniorstaff")
	void set(OfflinePlayer player, int value) {
		griefer = service.get(player);
		griefer.setTriggered(value);
		service.save(griefer);
		send(PREFIX + "Successfully set grief count of &e" + player.getName() + " &3to &e" + value);
	}

	@Path("(repair|reset) <honeyPot>")
	@Permission("group.seniorstaff")
	void repair(String honeyPot) {
		ProtectedRegion region = WGUtils.getProtectedRegion("hpregen_" + honeyPot);
		if (region == null)
			error("That honey pot does not exist");

		fixHP(region, player().getWorld());
		send(PREFIX + "Successfully repaired the honey pot: &e" + honeyPot);
	}

	@SneakyThrows
	@Path("create <honeypot> [schemSize]")
	void create(String honeyPot, @Arg("10") int expand) {
		honeyPot = honeyPot.toLowerCase();
		Region selection = WEUtils.getPlayerSelection(player());
		ProtectedRegion region = new ProtectedCuboidRegion("hp_" + honeyPot, selection.getMinimumPoint(), selection.getMaximumPoint());
		region.setFlag(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
		region.setFlag(Flags.BUILD, StateFlag.State.ALLOW);
		region.setPriority(1);
		regionManager.addRegion(region);
		WEUtils.expandAll(selection, expand);
		ProtectedRegion schemRegion = new ProtectedCuboidRegion("hpregen_" + honeyPot, selection.getMinimumPoint(), selection.getMaximumPoint());
		regionManager.addRegion(schemRegion);

//		TODO when API saving works again
//		WEUtils.save("hp/" + honeyPot, selection);
		runCommand("nexus schem save " + honeyPot);

		regionManager.save();
		send(PREFIX + "Successfully created the honey pot: &e" + honeyPot);
	}

	@SneakyThrows
	@Path("(delete|remove) <honeypot>")
	void delete(String honeyPot) {
		regionManager.removeRegion("hp_" + honeyPot);
		regionManager.removeRegion("hpregen_" + honeyPot);
		regionManager.save();
		send(PREFIX + "Successfully removed the honey pot: &e" + honeyPot);
	}

	@Path("list")
	void list() {
		Set<ProtectedRegion> regions = WGUtils.getRegionsLike("hp_");
		if (regions.size() == 0)
			error("There are no Honey Pots in your world.");

		send(PREFIX + "Honey Pots in your world:");
		int i = 0;
		for (ProtectedRegion region : regions) {
			json("&3" + ++i + ".&e" + region.getId())
					.command("/honeypots teleport " + getHP(region))
					.hover("&3Click to Teleport");
		}
	}

	@Path("(teleport|tp) <honeypot>")
	void teleport(String honeyPot) {
		Region region = WGUtils.getRegion("hp_" + honeyPot);
		if (region == null)
			error("That is not a valid Honey Pot");

		player().teleport(WEUtils.toLocation(region.getCenter()));
		send(PREFIX + "You have been teleported to Honey Pot:&e " + honeyPot);
	}

	public static String getHP(ProtectedRegion region) {
		return region.getId().replace("hp_", "");
	}

	public static void fixHP(ProtectedRegion region, World world) {
		WorldEditUtils WEUtils = new WorldEditUtils(world);
		String fileName = region.getId().replace("_", "/");
		try {
			WEUtils.paster().file(fileName).at(getSchemRegen(region, world).getMinimumPoint()).paste();
		} catch (InvalidInputException ex) {
			Nexus.log(ex.getMessage());
		}
	}

	public static ProtectedRegion getSchemRegen(ProtectedRegion region, World world) {
		String name = region.getId().replace("hp_", "hpregen_");
		return new WorldGuardUtils(world).getProtectedRegion(name);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission("honeypot.bypass")) return;
		incrementPlayer(event.getPlayer(), event.getBlock().getLocation());
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().hasPermission("honeypot.bypass")) return;
		incrementPlayer(event.getPlayer(), event.getBlock().getLocation());
	}

	@EventHandler
	public void onEntityKill(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player)) return;
		if (!(event.getEntity() instanceof Animals)) return;
		Player player = (Player) event.getDamager();
		if (player.hasPermission("honeypot.bypass")) return;
		incrementPlayer(player, event.getEntity().getLocation());
	}

	public void incrementPlayer(Player player, Location location) {
		WorldGuardUtils WGUtils = new WorldGuardUtils(location);
		Set<ProtectedRegion> regions = WGUtils.getRegionsAt(location);
		for (ProtectedRegion region : regions) {
			if (!region.getId().contains("hp_")) continue;
			HoneyPotGriefer griefer = service.get(player);
			int triggered = griefer.getTriggered() + 1;

			Chat.broadcastIngame(json("&7&l[&cRadar&7&l] &a" + player.getName() + " &fhas triggered a Honey Pot &e(HP: " + getHP(region) + ")")
					.next(" &e[Click to Teleport]")
					.command("mcmd vanish on ;; tp " + player.getName())
					.hover("This will automatically vanish you"), StaticChannel.STAFF);

			Chat.broadcastDiscord("**[Radar]** " + player.getName() + " has triggered a Honey Pot. `HP: " + getHP(region) + "`", StaticChannel.STAFF);

			if (triggered > 9) {
				PlayerUtils.runCommandAsConsole("sudo " + player.getName() + " ticket [HoneyPot] Grief trap triggered! " +
						"Please make sure the area has been fully repaired, and take the blocks from their inventory. " +
						"(HP: " + getHP(region) + ")");
				fixHP(region, player.getWorld());
				triggered = 0;
				PlayerUtils.runCommandAsConsole("ban " + player.getName() + " 10h You have been automatically banend " +
						"by a grief trap. Griefing is not allowed! (HP: " + getHP(region) + ")");
			}

			griefer.setTriggered(triggered);
			service.save(griefer);
		}
	}


}

