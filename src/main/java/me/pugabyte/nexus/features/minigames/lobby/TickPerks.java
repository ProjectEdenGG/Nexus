package me.pugabyte.nexus.features.minigames.lobby;

import eden.utils.TimeUtils.Time;
import lombok.Data;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.menus.PerkMenu;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkType;
import me.pugabyte.nexus.features.minigames.models.perks.common.GadgetPerk;
import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import me.pugabyte.nexus.features.minigames.models.perks.common.TickablePerk;
import me.pugabyte.nexus.models.perkowner.PerkOwner;
import me.pugabyte.nexus.models.perkowner.PerkOwnerService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static me.pugabyte.nexus.utils.CitizensUtils.isNPC;
import static me.pugabyte.nexus.utils.StringUtils.colorize;

public class TickPerks implements Listener {
	private static final PerkOwnerService service = new PerkOwnerService();
	private final Set<PerkOwner> loadoutUsers = new HashSet<>();
	private static final ItemStack MENU_ITEM = new ItemBuilder(Material.NETHER_STAR).name("&3Minigame Collectibles").build();
	// i'm intentionally not using CooldownService because I need reliability down to the tick to prevent things like
	// infinite height from the SpringGadget and I don't care about preserving cooldowns through reloads (too short for
	// it to matter)
	private static final Map<CooldownWrapper, Integer> cooldowns = new HashMap<>();

	public TickPerks() {
		Nexus.registerListener(this);

		Tasks.repeat(5, Minigames.PERK_TICK_DELAY, () -> Minigames.getWorld().getPlayers().forEach(player -> {
			Minigamer minigamer = PlayerManager.get(player);
			if ((minigamer.isPlaying() || isInRegion(player)) && !isNPC(player)) {
				PerkOwner perkOwner = service.get(player);

				AtomicInteger gadgetSlot = new AtomicInteger(8);
				boolean processInventory = player.getGameMode() == GameMode.SURVIVAL && !minigamer.isPlaying();

				if (processInventory) {
					player.getInventory().setItem(8, MENU_ITEM);
				}

				List<PerkType> enabledPerks = new ArrayList<>(perkOwner.getEnabledPerks());
				// sort alphabetically for consistent lobby gadget order
				enabledPerks.sort(Comparator.comparing(PerkType::getName).reversed());

				enabledPerks.forEach(perkType -> {
					Perk perk = perkType.getPerk();
					if (perk instanceof GadgetPerk && processInventory) {
						int slot = gadgetSlot.decrementAndGet();
						if (slot < 1) return; // don't overwrite first slot (could hold a basketball!)
						((GadgetPerk) perk).tick(player, slot);
						return;
					}

					if (perk instanceof LoadoutPerk)
						loadoutUsers.add(perkOwner);

					if (perk instanceof TickablePerk) {
						if (minigamer.isPlaying() && (minigamer.isRespawning() || !minigamer.usesPerk(perk)) || PlayerUtils.isVanished(player) || player.getGameMode() == GameMode.SPECTATOR) return;

						TickablePerk tickablePerk = (TickablePerk) perk;
						if (minigamer.isPlaying())
							tickablePerk.tick(minigamer);
						else
							tickablePerk.tick(player);
					}
				});

				if (processInventory)
					while (gadgetSlot.get() > 1) {
						int slot = gadgetSlot.decrementAndGet();
						player.getInventory().setItem(slot, null);
					}
			}
		}));

		// clear legacy loadout perk owners and send real packets
		Tasks.repeat(5, Time.SECOND.x(1), () -> new HashSet<>(loadoutUsers).forEach(perkOwner -> {
			perkOwner = service.get(perkOwner.getUuid()); // update loadout perks...? not sure if necessary
			OfflinePlayer _player = PlayerUtils.getPlayer(perkOwner.getUuid());
			Minigamer minigamer = _player.isOnline() ? PlayerManager.get(_player.getPlayer()) : null;
			if (!_player.isOnline() || (!minigamer.isPlaying() && !isInRegion((Player) _player)) || (minigamer.isPlaying() && !minigamer.usesPerk(LoadoutPerk.class))
					|| perkOwner.getEnabledPerksByClass(LoadoutPerk.class).isEmpty() || PlayerUtils.isVanished((Player) _player) || ((Player) _player).getGameMode() == GameMode.SPECTATOR) {
				loadoutUsers.remove(perkOwner);
				// send true packets
				Player player = _player.getPlayer();
				if (player == null)
					return;
				ItemStack[] items = player.getInventory().getArmorContents();
				for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
					ItemStack item = items[i];
					if (item == null)
						item = new ItemStack(Material.AIR);

					EnumItemSlot slot = switch (i) {
						case 3 -> EnumItemSlot.HEAD;
						case 2 -> EnumItemSlot.CHEST;
						case 1 -> EnumItemSlot.LEGS;
						case 0 -> EnumItemSlot.FEET;
						default -> throw new IllegalStateException("Unexpected value: " + i);
					};
					LoadoutPerk.sendPackets(player, player.getWorld().getPlayers(), item, slot);
				}
			}
		}));

		// tick cooldowns
		Tasks.repeat(5, 1, () -> new HashSet<>(cooldowns.entrySet()).forEach(entry -> {
			int ticks = entry.getValue();
			ticks -= 1;
			if (ticks <= 0)
				cooldowns.remove(entry.getKey());
			else
				cooldowns.put(entry.getKey(), ticks);
		}));
	}

	protected static GadgetPerk getGadgetPerk(ItemStack item) {
		return Arrays.stream(PerkType.values()).filter(perkType -> perkType.getPerk() instanceof GadgetPerk).map(perkType -> (GadgetPerk) perkType.getPerk()).filter(perk -> perk.getItem().equals(item)).findAny().orElse(null);
	}

	protected static GadgetPerk getGadgetPerk(Player player, ItemStack item) {
		GadgetPerk perk = getGadgetPerk(item);
		if (perk == null)
			return null;
		PerkOwner owner = service.get(player);
		if (owner.getEnabledPerksByClass(GadgetPerk.class).contains(perk))
			return perk;
		return null;
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onInteract(PlayerInteractEvent event) {
		if (event.getItem() == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		if (Utils.ActionGroup.LEFT_CLICK.applies(event)) return;

		Player player = event.getPlayer();
		if (!player.getWorld().equals(Minigames.getWorld())) return;
		if (!isInRegion(player)) return;

		if (event.getItem().equals(MENU_ITEM)) {
			new PerkMenu().open(player);
			return;
		}

		GadgetPerk perk = getGadgetPerk(player, event.getItem());
		if (perk == null) return;

		if (perk.getCooldown() > 0) {
			CooldownWrapper wrapper = CooldownWrapper.of(player, perk);
			int ticks = cooldowns.getOrDefault(wrapper, 0);
			if (ticks > 0) {
				player.sendActionBar(colorize("&eThat gadget is on cooldown for " + (int) Math.ceil(ticks / 20d) + "s"));
				event.setCancelled(true);
				return;
			} else
				cooldowns.put(wrapper, perk.getCooldown());
		}

		perk.useGadget(player);
		if (perk.cancelEvent())
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onInventoryMove(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player))
			return;
		if (event.getClickedInventory() == null || !(event.getClickedInventory() instanceof PlayerInventory))
			return;
		if (event.getCurrentItem() == null)
			return;
		if (event.getCurrentItem().equals(MENU_ITEM) || getGadgetPerk(event.getCurrentItem()) != null)
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onHandSwap(PlayerSwapHandItemsEvent event) {
		if (event.getMainHandItem() == null)
			return;
		if (event.getMainHandItem().equals(MENU_ITEM) || getGadgetPerk(event.getMainHandItem()) != null)
			event.setCancelled(true);
	}

	public boolean isInRegion(Player player) {
		return Minigames.getWorldGuardUtils().isInRegion(player.getLocation(), Minigames.getLobbyRegion());
	}

	@Data
	private static class CooldownWrapper {
		private final UUID uuid;
		private final GadgetPerk perk;
		public static CooldownWrapper of(HasUniqueId player, GadgetPerk perk) {
			return new CooldownWrapper(player.getUniqueId(), perk);
		}
	}
}
