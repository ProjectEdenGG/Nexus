package gg.projecteden.nexus.features.minigames.lobby;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.menus.PerkMenu;
import gg.projecteden.nexus.features.minigames.menus.lobby.ArenasMenu;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.events.lobby.MinigamerUseGadgetEvent;
import gg.projecteden.nexus.features.minigames.models.perks.Perk;
import gg.projecteden.nexus.features.minigames.models.perks.PerkType;
import gg.projecteden.nexus.features.minigames.models.perks.common.GadgetPerk;
import gg.projecteden.nexus.features.minigames.models.perks.common.LoadoutPerk;
import gg.projecteden.nexus.features.minigames.models.perks.common.TickablePerk;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.features.minigames.Minigames.isInMinigameLobbyRegion;
import static gg.projecteden.nexus.features.minigames.Minigames.isInMinigameLobbyWorld;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class TickPerks implements Listener {
	private static final PerkOwnerService service = new PerkOwnerService();
	private final Set<PerkOwner> loadoutUsers = new HashSet<>();
	private static final ItemStack MENU_ITEM = new ItemBuilder(Material.NETHER_STAR).name("&3Minigame Collectibles").build();
	private static final Map<CooldownWrapper, Long> cooldowns = new HashMap<>(); // reliability down to the tick
	public static final int PERK_TICK_DELAY = 4;

	public TickPerks() {
		Nexus.registerListener(this);

		Tasks.repeat(5, PERK_TICK_DELAY, () -> OnlinePlayers.where().world(Minigames.getWorld()).get().forEach(player -> {
			Minigamer minigamer = Minigamer.of(player);
			if (minigamer.isPlaying() || Minigames.isInMinigameLobby(player)) {
				//Minigames.debug(Nerd.of(minigamer).getNickname() + " - is in game lobby (world=" + isInMinigameLobbyWorld(player) + ", region=" + isInMinigameLobbyRegion(player) + ")");
				PerkOwner perkOwner = service.get(player);

				AtomicInteger gadgetSlot = new AtomicInteger(8);
				boolean processInventory = player.getGameMode() == GameMode.SURVIVAL && !minigamer.isPlaying() && Minigames.isInMinigameLobby(player);

				if (processInventory) {
					player.getInventory().setItem(8, MENU_ITEM);
					//Minigames.debug(Nerd.of(minigamer).getNickname() + " - setting item in hot bar");

					if (MinigameInviter.canSendInvite(player))
						player.getInventory().setItem(1, ArenasMenu.getInviteItem(player).build());
					else if (CustomMaterial.ENVELOPE_1.is(player.getInventory().getItem(1)))
						player.getInventory().setItem(1, new ItemStack(Material.AIR));
				}

				perkOwner.getEnabledPerks().stream()
					.sorted(Comparator.comparing(PerkType::getName).reversed())
					.forEach(perkType -> {
						Perk perk = perkType.getPerk();
						if (perk instanceof GadgetPerk gadgetPerk && processInventory) {
							int slot = gadgetSlot.decrementAndGet();
							if (slot < 5) return; // only allow 3 gadget slots
							gadgetPerk.tick(player, slot);
							return;
						}

						if (perk instanceof LoadoutPerk)
							loadoutUsers.add(perkOwner);

						if (perk instanceof TickablePerk tickablePerk) {
							if (!tickablePerk.shouldTickFor(player))
								return;

							if (minigamer.isPlaying())
								tickablePerk.tick(minigamer);
							else
								tickablePerk.tick(player);
						}
					});

				if (processInventory)
					while (gadgetSlot.get() > 5) {
						int slot = gadgetSlot.decrementAndGet();
						player.getInventory().setItem(slot, null);
					}
			}
		}));

		// clear legacy loadout perk owners and send real packets
		Tasks.repeat(5, TickTime.SECOND.x(1), () -> new HashSet<>(loadoutUsers).forEach(perkOwner -> {
			if (!perkOwner.isOnline())
				return;

			if (shouldShowLoadout(perkOwner))
				return;

			loadoutUsers.remove(perkOwner);
			// send true packets
			final Player player = perkOwner.getOnlinePlayer();
			for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
				ItemStack item = player.getInventory().getItem(slot);
				if (item == null)
					item = new ItemStack(Material.AIR);

				LoadoutPerk.sendPackets(player, player.getWorld().getPlayers(), item, slot);
			}
		}));

		// tick cooldowns
		Tasks.repeat(5, 1, () -> new HashSet<>(cooldowns.entrySet()).forEach(entry -> {
			long ticks = entry.getValue();
			ticks -= 1;
			if (ticks <= 0)
				cooldowns.remove(entry.getKey());
			else
				cooldowns.put(entry.getKey(), ticks);
		}));
	}

	private boolean shouldShowLoadout(PerkOwner perkOwner) {
		final Player player = perkOwner.getOnlinePlayer();
		final Minigamer minigamer = Minigamer.of(player);

		if (!minigamer.isPlaying() && !Minigames.isInMinigameLobby(player))
			return false;
		if (minigamer.isPlaying())
			if (!minigamer.usesPerk(LoadoutPerk.class) || !minigamer.isAlive() || minigamer.isRespawning())
				return false;
		if (perkOwner.getEnabledPerksByClass(LoadoutPerk.class).isEmpty())
			return false;
		if (Vanish.isVanished(player))
			return false;
		if (player.getGameMode() == GameMode.SPECTATOR)
			return false;

		return true;
	}

	protected static GadgetPerk getGadgetPerk(ItemStack item) {
		if (isNullOrAir(item))
			return null;
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
		final Minigamer minigamer = Minigamer.of(player);
		if (!player.getWorld().equals(Minigames.getWorld())) return;
		if (!Minigames.isInMinigameLobby(player)) return;

		if (event.getItem().equals(MENU_ITEM)) {
			new PerkMenu().open(player);
			return;
		}

		GadgetPerk perk = getGadgetPerk(player, event.getItem());
		if (perk == null) return;

		if (perk.getCooldown() > 0) {
			CooldownWrapper wrapper = CooldownWrapper.of(player, perk);
			long ticks = cooldowns.getOrDefault(wrapper, 0L);
			if (ticks > 0) {
				ActionBarUtils.sendActionBar(player, "&cThat gadget is on cooldown for " + (int) Math.ceil(ticks / 20d) + "s");
				event.setCancelled(true);
				return;
			} else
				cooldowns.put(wrapper, perk.getCooldown());
		}

		if (!new MinigamerUseGadgetEvent(minigamer, perk).callEvent()) {
			ActionBarUtils.sendActionBar(player, "&cYou cannot use that gadget right now!");
			return;
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
		if (event.getMainHandItem().equals(MENU_ITEM) || getGadgetPerk(event.getMainHandItem()) != null || getGadgetPerk(event.getOffHandItem()) != null)
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	public void onDrop(PlayerDropItemEvent event) {
		if (!Minigames.isInMinigameLobby(event.getPlayer()))
			return;
		if (event.getItemDrop().getItemStack().equals(MENU_ITEM))
			event.setCancelled(true);
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
