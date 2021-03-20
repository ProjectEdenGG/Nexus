package me.pugabyte.nexus.features.minigames.lobby;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.perks.Perk;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwner;
import me.pugabyte.nexus.features.minigames.models.perks.PerkOwnerService;
import me.pugabyte.nexus.features.minigames.models.perks.common.LoadoutPerk;
import me.pugabyte.nexus.features.minigames.models.perks.common.TickablePerk;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Time;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static me.pugabyte.nexus.utils.CitizensUtils.isNPC;

public class TickPerks implements Listener {
	private static final PerkOwnerService service = new PerkOwnerService();
	private final Set<PerkOwner> loadoutUsers = new HashSet<>();

	public TickPerks() {
		Nexus.registerListener(this);

		Tasks.repeat(5, Minigames.PERK_TICK_DELAY, () -> Minigames.getWorld().getPlayers().forEach(player -> {
			Minigamer minigamer = PlayerManager.get(player);
			if ((minigamer.isPlaying() || isInRegion(player)) && !isNPC(player)) {
				PerkOwner perkOwner = service.get(player);

				perkOwner.getEnabledPerks().forEach(perkType -> {
					Perk perk = perkType.getPerk();

					if (perk instanceof LoadoutPerk) {
						if (!loadoutUsers.contains(perkOwner)) {
							armorTick(player);
							loadoutUsers.add(perkOwner);
						}
					}
					else if (perk instanceof TickablePerk) {
						TickablePerk tickablePerk = (TickablePerk) perk;
						if (minigamer.isPlaying())
							tickablePerk.tick(minigamer);
						else
							tickablePerk.tick(player);
					}
				});
			}
		}));

		// clear legacy loadout perk owners
		Tasks.repeat(5, Time.SECOND.x(1), () -> new HashSet<>(loadoutUsers).forEach(perkOwner -> {
			perkOwner = service.get(perkOwner.getUuid()); // update loadout perks...? not sure if necessary
			OfflinePlayer _player = PlayerUtils.getPlayer(perkOwner.getUuid());
			if (!_player.isOnline() || (!PlayerManager.get((Player) _player).isPlaying() && !isInRegion((Player) _player)) || getLoadoutPerks(perkOwner).isEmpty()) {
				loadoutUsers.remove(perkOwner);
				// send true packets
				Player player = _player.getPlayer();
				assert player != null;
				ItemStack[] items = player.getInventory().getArmorContents();
				for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
					ItemStack item = items[i];
					if (item == null)
						item = new ItemStack(Material.AIR);

					EnumItemSlot slot;
					switch (i) {
						case 3:
							slot = EnumItemSlot.HEAD;
							break;
						case 2:
							slot = EnumItemSlot.CHEST;
							break;
						case 1:
							slot = EnumItemSlot.LEGS;
							break;
						case 0:
							slot = EnumItemSlot.FEET;
							break;
						default:
							throw new IllegalStateException("Unexpected value: " + i);
					}
					LoadoutPerk.sendPackets(player, player.getWorld().getPlayers(), item, slot);
				}
			}
		}));

		// workaround for players being able to remove their fake item (client-side) by clicking on it in their inventory
		//  if they don't have a real item in that slot. could also use Client.WINDOW_CLICK but that would play the equip
		//  sound every time players clicked at all in an inventory (if they had a hat)
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Nexus.getInstance(), PacketType.Play.Client.CLOSE_WINDOW) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PerkOwner perkOwner = service.get(event.getPlayer());
				if (loadoutUsers.contains(perkOwner))
					armorTick(event.getPlayer());
			}

			@Override
			public void onPacketSending(PacketEvent event) {}
		});
	}

	private static Set<LoadoutPerk> getLoadoutPerks(PerkOwner perkOwner) {
		return perkOwner.getEnabledPerks().stream().filter(perkType -> perkType.getPerk() instanceof LoadoutPerk).map(perkType -> (LoadoutPerk) perkType.getPerk()).collect(Collectors.toSet());
	}

	private void armorTick(Player player) {
		Minigamer minigamer = PlayerManager.get(player);
		PerkOwner perkOwner = service.get(player);
		Set<LoadoutPerk> perks = getLoadoutPerks(perkOwner);
		if ((!minigamer.isPlaying() && !isInRegion(player)) || perks.isEmpty()) {
			loadoutUsers.remove(perkOwner);
			return;
		}

		boolean useMinigamer = minigamer.isPlaying();
		Tasks.wait(1, () -> perks.forEach(perk -> {
			if (useMinigamer)
				perk.tick(minigamer);
			else
				perk.tick(player);
		}));
	}

	@EventHandler
	public void onArmorChange(PlayerArmorChangeEvent event) {
		Player player = event.getPlayer();
		PerkOwner perkOwner = service.get(player);
		if (!loadoutUsers.contains(perkOwner)) return;
		armorTick(player);
	}

	public boolean isInRegion(Player player) {
		return Minigames.getWorldGuardUtils().isInRegion(player.getLocation(), Minigames.getLobbyRegion());
	}
}
