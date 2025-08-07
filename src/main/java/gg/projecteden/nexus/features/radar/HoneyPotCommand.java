package gg.projecteden.nexus.features.radar;

import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.commands.worldedit.ExpandAllCommand;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.honeypot.HoneyPotBans;
import gg.projecteden.nexus.models.honeypot.HoneyPotBansService;
import gg.projecteden.nexus.models.honeypot.HoneyPotGriefer;
import gg.projecteden.nexus.models.honeypot.HoneyPotGrieferService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldEditUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Permission(Group.ADMIN)
@Aliases({"hp", "honeypots"})
@Description("Automatic grief traps")
public class HoneyPotCommand extends CustomCommand implements Listener {
	private final HoneyPotGrieferService grieferService = new HoneyPotGrieferService();
	private HoneyPotGriefer griefer;
	private final HoneyPotBansService bansService = new HoneyPotBansService();
	private final HoneyPotBans honeyPotBans = bansService.get0();

	private WorldGuardUtils worldguard;
	private WorldEditUtils worldedit;
	private RegionManager regionManager;

	public HoneyPotCommand(CommandEvent event) {
		super(event);
		worldguard = new WorldGuardUtils(player());
		worldedit = new WorldEditUtils(player());
		regionManager = worldguard.getManager();
	}

	@Path("check <player>")
	@Description("View how many times a player has griefed")
	void check(@Arg("self") OfflinePlayer player) {
		griefer = grieferService.get(player);
		send(PREFIX + "&e" + player.getName() + "&3 has griefed &e" + griefer.getTriggered() + " times");
	}

	@Path("set <player> <int>")
	@Permission(Group.SENIOR_STAFF)
	@Description("Set a player's grief count")
	void set(OfflinePlayer player, int value) {
		griefer = grieferService.get(player);
		griefer.setTriggered(value);
		grieferService.save(griefer);
		send(PREFIX + "Successfully set grief count of &e" + player.getName() + " &3to &e" + value);
	}

	@Path("fix <honeyPot>")
	@Description("Repair a honeypot")
	void repair(String honeyPot) {
		ProtectedRegion region = worldguard.getProtectedRegion("hp_" + honeyPot);
		if (region == null)
			error("That honey pot does not exist");

		fix(region, world());
		send(PREFIX + "Successfully repaired the honey pot: &e" + honeyPot);
	}

	@SneakyThrows
	@Path("create <honeypot> [schemSize]")
	@Description("Create a honeypot")
	void create(@Arg(regex = "[\\w]+_[\\d]+") String honeyPot, @Arg("10") int expand) {
		honeyPot = honeyPot.toLowerCase();
		if (honeyPot.startsWith("hp_"))
			honeyPot = honeyPot.substring(2);

		Region selection = worldedit.getPlayerSelection(player());
		ProtectedRegion region = new ProtectedCuboidRegion("hp_" + honeyPot, selection.getMinimumPoint(), selection.getMaximumPoint());
		region.setFlag(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
		regionManager.addRegion(region);

		ExpandAllCommand.expandAll(player(), expand);

		ProtectedRegion schemRegion = new ProtectedCuboidRegion("hpregen_" + honeyPot, selection.getMinimumPoint(), selection.getMaximumPoint());
		schemRegion.setFlag(Flags.PASSTHROUGH, StateFlag.State.ALLOW);
		regionManager.addRegion(schemRegion);

//		TODO when API saving works again
//		worldedit.save("hp/" + honeyPot, selection);
		runCommand("nexus schem save hp/" + honeyPot);

		regionManager.save();
		send(PREFIX + "Successfully created the honey pot: &e" + honeyPot);
	}

	@Confirm
	@SneakyThrows
	@Path("(delete|remove) <honeypot>")
	@Description("Delete a honeypot")
	void delete(String honeyPot) {
		regionManager.removeRegion("hp_" + honeyPot);
		regionManager.removeRegion("hpregen_" + honeyPot);
		regionManager.save();
		send(PREFIX + "Successfully removed the honey pot: &e" + honeyPot);
	}

	@Path("list [page]")
	@Description("List honeypots in your current world")
	void list(@Arg("1") int page) {
		List<ProtectedRegion> regions = new ArrayList<>(worldguard.getRegionsLike("hp_.*"));

		if (regions.isEmpty())
			error("There are no Honey Pots in your world");

		send(PREFIX + "Honey Pots in your world:");
		new Paginator<ProtectedRegion>()
			.values(regions)
			.formatter((region, index) -> {
				int bans = honeyPotBans.get(region.getId()).getBans();
				return json(index + " &e" + region.getId() + " &7- " + bans + plural(" ban", bans))
						.command("/honeypots teleport " + getName(region))
						.hover("&3Click to Teleport");
			})
			.command("/honeypots list")
			.page(page)
			.send();
	}

	@Path("(teleport|tp) <honeypot>")
	@Description("Teleport to a honeypot in your current world")
	void teleport(String honeyPot) {
		Region region = worldguard.getRegion("hp_" + honeyPot);
		if (region == null)
			error("That is not a valid Honey Pot");

		player().teleportAsync(worldedit.toLocation(region.getCenter()), TeleportCause.COMMAND);
		send(PREFIX + "You have been teleported to Honey Pot:&e " + honeyPot);
	}

	@Path("removeItems <player>")
	@Description("Remove honey pot items from a player's inventory")
	void removeItems(Player player) {
		send(PREFIX + "Removed " + removeHoneyPotItems(player) + " honey pot items from " + player.getName() + "'s inventory");
	}

	public static String getName(ProtectedRegion region) {
		return region.getId().replace("hp_", "");
	}

	public static void fix(ProtectedRegion region, World world) {
		WorldEditUtils worldedit = new WorldEditUtils(world);
		String fileName = region.getId().replace("hp_", "hp/");
		try {
			worldedit.paster().file(fileName).at(getSchemRegen(region, world).getMinimumPoint()).pasteAsync();
		} catch (InvalidInputException ex) {
			Nexus.log(ex.getMessage());
		}

		for (Entity entity : world.getEntities())
			if (entity instanceof Item)
				if (new WorldGuardUtils(world).isInRegion(entity.getLocation(), region))
					if (isHoneyPotItem(((Item) entity).getItemStack()))
						entity.remove();
	}

	public static ProtectedRegion getSchemRegen(ProtectedRegion region, World world) {
		String name = region.getId().replace("hp_", "hpregen_");
		return new WorldGuardUtils(world).getProtectedRegion(name);
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().hasPermission("honeypot.bypass")) return;
		Block block = event.getBlock();
		double amount = MaterialTag.CROPS.isTagged(event.getBlock().getType()) ? .5 : 1;
		if (incrementPlayer(event.getPlayer(), block.getLocation(), amount)) {
			event.setDropItems(false);
			addHoneyPotItemTag(new ArrayList<>(block.getDrops(event.getPlayer().getInventory().getItemInMainHand())), block.getLocation());
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Tasks.wait(10, () -> removeHoneyPotItems(event.getPlayer()));
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		removeHoneyPotItems(event.getPlayer());
		Tasks.wait(10, () -> removeHoneyPotItems(event.getPlayer()));
	}

	private static final String nbtTag = "honeyPotItem";

	private static boolean isHoneyPotItem(ItemStack item) {
		return !Nullables.isNullOrAir(item) && new NBTItem(item).getBoolean(nbtTag);
	}

	public int removeHoneyPotItems(Player player) {
		int count = 0;
		for (ItemStack item : player.getInventory().getContents()) {
			if (isHoneyPotItem(item)) {
				player.getInventory().remove(item);
				++count;
				break;
			}
		}

		return count;
	}

	public void addHoneyPotItemTag(List<ItemStack> drops, Location location) {
		for (ItemStack drop : drops) {
			NBTItem nbtItem = new NBTItem(drop);
			nbtItem.setBoolean(nbtTag, true);
			drop = nbtItem.getItem();
			location.getWorld().dropItemNaturally(location, drop);
		}
	}

	@EventHandler
	public void onEntityKill(EntityDeathEvent event) {
		if (!(event.getEntity() instanceof Animals)) return;

		Location location = event.getEntity().getLocation();
		WorldGuardUtils worldguard = new WorldGuardUtils(location);
		Set<ProtectedRegion> regions = worldguard.getRegionsAt(location);
		for (ProtectedRegion region : regions) {
			if (!region.getId().contains("hp_"))
				continue;

			addHoneyPotItemTag(event.getDrops(), location);
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().hasPermission("honeypot.bypass")) return;
		double amount = MaterialTag.CROPS.isTagged(event.getBlock().getType()) ? -.5 : .5;
		incrementPlayer(event.getPlayer(), event.getBlock().getLocation(), amount);
	}

	@EventHandler
	public void onCraftItem(CraftItemEvent event) {
		if (!(event.getView().getPlayer() instanceof Player player)) return;
		if (player.hasPermission("honeypot.bypass")) return;

		for (ItemStack ingredient : event.getInventory().getMatrix())
			if (isHoneyPotItem(ingredient)) {
				event.getInventory().setResult(new ItemStack(Material.AIR));
				break;
			}
	}

	@EventHandler
	public void onEntityKill(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player player)) return;
		if (!(event.getEntity() instanceof Animals animal)) return;
		if (player.hasPermission("honeypot.bypass")) return;
		incrementPlayer(player, animal.getLocation(), 1);
	}

	public boolean incrementPlayer(Player player, Location location, double amount) {
		WorldGuardUtils worldguard = new WorldGuardUtils(location);
		Set<ProtectedRegion> regions = worldguard.getRegionsAt(location);
		for (ProtectedRegion region : regions) {
			if (!region.getId().contains("hp_"))
				continue;

			String name = getName(region);
			HoneyPotGriefer griefer = grieferService.get(player);
			double triggered = Math.max(griefer.getTriggered() + amount, 0);

			if (amount > 0 && new CooldownService().check(player, "hp_" + name, TickTime.MINUTE.x(10))) {
				final JsonBuilder message = json("&7&l[&cRadar&7&l] &a" + player.getName() + " &fhas triggered a Honey Pot &e(HP: " + name + ")")
						.next(" &e[Click to Teleport]")
						.command("mcmd vanish on ;; tp " + player.getName())
						.hover("This will automatically vanish you");

				Broadcast.staffIngame().message(message).send();
				Broadcast.staffDiscord().prefix("Radar").message(player.getName() + " has triggered a Honey Pot. `HP: " + name + "`").send();
			}

			if ((triggered == 3 || triggered == 3.5) && !griefer.isWarned()) {
				PlayerUtils.runCommandAsConsole("warn " + player.getName() + " You have been automatically warned by a grief trap. Griefing is not allowed! (HP: " + region.getId() + ")");
				griefer.setWarned(true);
			}

			if (triggered >= 10) {
				final HoneyPotBansService bansService = new HoneyPotBansService();
				final HoneyPotBans honeyPotBans = bansService.get0();
				honeyPotBans.get(region.getId()).addBan();
				bansService.save(honeyPotBans);

				Tasks.wait(TickTime.SECOND, () -> fix(region, player.getWorld()));
				triggered = 0;
				PlayerUtils.runCommandAsConsole("ban " + player.getName() + " 10h You have been automatically banned " +
						"by a grief trap. Griefing is not allowed! (HP: " + name + ")");
			}

			griefer.setTriggered(triggered);
			grieferService.save(griefer);
			return true;
		}

		return false;
	}

	@EventHandler
	public void onRegionExit(PlayerLeftRegionEvent event) {
		if (!event.getRegion().getId().contains("hp_")) return;
		HoneyPotGriefer griefer = grieferService.get(event.getPlayer());
		if (griefer.getTriggered() <= 0) return;
		Tasks.wait(TickTime.SECOND.x(30), () -> {
			if (event.getPlayer().isOnline())
				removeHoneyPotItems(event.getPlayer());
			fix(event.getRegion(), event.getPlayer().getWorld());
		});
	}

}

