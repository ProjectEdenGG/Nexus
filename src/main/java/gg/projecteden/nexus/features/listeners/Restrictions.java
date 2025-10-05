package gg.projecteden.nexus.features.listeners;

import com.destroystokyo.paper.event.player.PlayerAdvancementCriterionGrantEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.features.chat.Censor;
import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent.Cause;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.world.PortalCreateEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class Restrictions implements Listener {
	private static final List<WorldGroup> ALLOWED_WORLD_GROUPS = Arrays.asList(WorldGroup.SURVIVAL, WorldGroup.CREATIVE, WorldGroup.SKYBLOCK);
	private static final List<WorldGroup> BLOCKED_WORLD_GROUPS = Arrays.asList(WorldGroup.LEGACY, WorldGroup.SERVER);
	private static final List<String> BLOCKED_WORLDS = Arrays.asList("safepvp", "events");

	public static boolean isPerkAllowedAt(HasUniqueId player, Location location) {
		if (Rank.of(player).isAdmin())
			return true;

		WorldGroup worldGroup = WorldGroup.of(location);
		if (!ALLOWED_WORLD_GROUPS.contains(worldGroup))
			return false;

		if (BLOCKED_WORLD_GROUPS.contains(worldGroup))
			return false;

		if (BLOCKED_WORLDS.contains(location.getWorld().getName()))
			return false;

		WorldGuardUtils worldGuardUtils = new WorldGuardUtils(location);
		if (!worldGuardUtils.getRegionsAt(location).isEmpty())
			return false;

		return true;
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() != TeleportCause.SPECTATE)
			return;

		final Player player = event.getPlayer();
		if (Rank.of(player).isStaff())
			return;

		event.setCancelled(true);
		player.setGameMode(GameMode.SPECTATOR);
	}

	private void spawnShoulderParrot(Location location, Parrot original) {
		location.getWorld().spawn(location, Parrot.class, parrot -> {
			parrot.setAI(original.hasAI());
			parrot.setAge(original.getAge());
			parrot.setBreed(original.canBreed());
			parrot.setOwner(original.getOwner());
			parrot.setTamed(original.isTamed());
			parrot.setVariant(original.getVariant());
			parrot.customName(original.customName());
			parrot.setCustomNameVisible(original.isCustomNameVisible());
			if (original.isAdult())
				parrot.setAdult();
			else
				parrot.setBaby();
		});
	}

	@EventHandler
	@SuppressWarnings("deprecation")
	public void onParrotTeleport(PlayerTeleportEvent event) {
		Player player = event.getPlayer();

		if (CitizensUtils.isNPC(player))
			return;

		boolean perkAllowed = Restrictions.isPerkAllowedAt(player, event.getTo());
		boolean sameWorldGroup = WorldGroup.of(player) == WorldGroup.of(event.getTo());
		if (perkAllowed && sameWorldGroup)
			return;

		if (player.getShoulderEntityLeft() instanceof Parrot leftParrot) {
			spawnShoulderParrot(event.getFrom(), leftParrot);
			player.setShoulderEntityLeft(null);
		}

		if (player.getShoulderEntityRight() instanceof Parrot rightParrot) {
			spawnShoulderParrot(event.getFrom(), rightParrot);
			player.setShoulderEntityRight(null);
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onWorldChange(PlayerTeleportEvent event) {
		if (event.getFrom().getWorld().equals(event.getTo().getWorld()))
			return;

		if (Nerd.of(event.getPlayer()).getRank().isStaff())
			return;

		if (CooldownService.isNotOnCooldown(event.getPlayer(), "world-change", TickTime.SECOND))
			return;

		event.setCancelled(true);
		PlayerUtils.send(event.getPlayer(), StringUtils.getPrefix("Restrictions") + "&cYou cannot change worlds that fast");
	}

	@EventHandler
	public void onCommandMinecartSpawn(EntitySpawnEvent event) {
		if (event.getEntity() instanceof CommandMinecart) {
			event.setCancelled(true);
			Tasks.wait(1, () -> event.getEntity().remove());
		}
	}

	@EventHandler
	public void onCommandMinecartInteract(PlayerInteractEvent event) {
		if (Nullables.isNullOrAir(event.getItem()))
			return;

		if (event.getItem().getType() == Material.COMMAND_BLOCK_MINECART)
			event.setCancelled(true);
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		if (Rank.of(player).isStaff())
			return;

		String[] lines = event.getLines();
		boolean censored = false;

		for (int i = 0; i < lines.length; i++) {
			String line = StringUtils.stripColor(lines[i]);
			if (Censor.isCensored(player, line)) {
				event.setLine(i, "");
				censored = true;
			}
		}

		if (!censored)
			return;

		PlayerUtils.send(player, "&cInappropriate sign content");
		String location = "(" + StringUtils.xyzw(event.getBlock().getLocation()) + ")";
		String message = "&cSign content by " + Nickname.of(player) + " was censored: &e" + String.join(", ", lines) + " " + location;
		Broadcast.staff().prefix("Censor").message(message).send();
	}

	@EventHandler
	public void onAnvilRenameItem(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player player))
			return;

		Inventory inventory = event.getClickedInventory();
		if (inventory == null || inventory.getType() != InventoryType.ANVIL)
			return;

		if (event.getSlotType() != SlotType.RESULT)
			return;

		ItemStack item = event.getCurrentItem();

		if (Nullables.isNullOrAir(item))
			return;

		ItemMeta meta = item.getItemMeta();

		String input = meta.getDisplayName();
		if (!Censor.isCensored(player, input))
			return;

		meta.setDisplayName(null);
		item.setItemMeta(meta);

		PlayerUtils.send(player, "&cInappropriate item name");
		String message = "&cAnvil name by " + Nickname.of(player) + " was censored: &e" + input;
		Broadcast.staff().prefix("Censor").message(message).send();
	}

	@EventHandler
	public void onPortalEvent(PlayerPortalEvent event) {
		if (Arrays.asList(WorldGroup.SKYBLOCK, WorldGroup.CREATIVE).contains(WorldGroup.of(event.getPlayer())))
			event.setCancelled(true);
	}

	@EventHandler
	public void onEndPortalCreate(PortalCreateEvent event) {
		final WorldGroup worldGroup = WorldGroup.of(event.getWorld());
		if (worldGroup == WorldGroup.SURVIVAL)
			return;

		// Vanilla mechanic portals
		if (worldGroup == WorldGroup.MINIGAMES && !Minigames.getWorld().equals(event.getWorld()))
			return;

		event.setCancelled(true);
	}

	@EventHandler
	public void onWitherRoseEffect(EntityPotionEffectEvent event) {
		if (event.getCause() == Cause.WITHER_ROSE)
			if (event.getEntity() instanceof Player)
				event.setCancelled(true);
	}

	@EventHandler
	public void onSkyBlockFallingCommand(PlayerCommandPreprocessEvent event) {
		Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.SKYBLOCK)
			return;

		if (Vanish.isVanished(player))
			return;

		if (player.getLocation().getY() < -1000)
			return;

		if (player.getFallDistance() > 5 && !player.isFlying()) {
			event.setCancelled(true);
			PlayerUtils.send(player, "&cYou cannot run commands while falling (try moving onto a solid block)");
		}
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		event.getPlayer().closeInventory();
		if (event.getFrom().getWorld().getEnvironment() == Environment.THE_END || event.getTo().getWorld().getEnvironment() != Environment.THE_END)
			return;

		if (Rank.of(event.getPlayer()).gte(Rank.TRUSTED))
			return;

		AdvancementProgress progress = event.getPlayer().getAdvancementProgress(PlayerUtils.getAdvancement("story/follow_ender_eye"));
		if (!progress.isDone()) {
			event.setCancelled(true);
			PlayerUtils.send(event.getPlayer(), "&cYou must enter an end portal before you can enter The End!");
		}
	}

	@EventHandler
	public void onInteractHoldingSpawnEgg(PlayerInteractEvent event) {
		if (Nullables.isNullOrAir(event.getItem())) return;
		if (!MaterialTag.SPAWN_EGGS.isTagged(event.getItem().getType())) return;
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (Nullables.isNullOrAir(event.getClickedBlock())) return;
		if (!event.getClickedBlock().getType().equals(Material.SPAWNER)) return;

		if (WorldGroup.STAFF.contains(event.getClickedBlock().getWorld()))
			return;

		if (!Rank.of(event.getPlayer()).isSeniorStaff())
			event.setCancelled(true);
	}

	@EventHandler
	public void onNPCInvOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();
		NPC selectedNPC = CitizensUtils.getSelectedNPC(player);
		if (selectedNPC == null)
			return;

		net.citizensnpcs.api.trait.trait.Inventory inventory = selectedNPC.getTraitNullable(net.citizensnpcs.api.trait.trait.Inventory.class);
		if (inventory == null)
			return;

		InventoryHolder npcHolder = inventory.getInventoryView().getHolder();
		if (npcHolder == null)
			return;

		if (!npcHolder.equals(event.getInventory().getHolder()))
			return;

		World world = player.getWorld();
		if (selectedNPC.getStoredLocation().getWorld().equals(world))
			return;

		event.setCancelled(true);
		PlayerUtils.send(player, StringUtils.getPrefix("NPC") + "&cYou must be in the same world as your selected NPC (" + world.getName() + ")");
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onVanillaAchievement(PlayerAdvancementCriterionGrantEvent event) {
		if (!WorldGroup.SURVIVAL.contains(event.getPlayer().getWorld()) || event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			event.setCancelled(true);
	}

}
