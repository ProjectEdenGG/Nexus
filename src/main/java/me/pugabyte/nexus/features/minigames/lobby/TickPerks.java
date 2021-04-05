package me.pugabyte.nexus.features.minigames.lobby;

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
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;

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

					if (perk instanceof LoadoutPerk)
						loadoutUsers.add(perkOwner);

					if (perk instanceof TickablePerk) {
						if (minigamer.isPlaying() && minigamer.isRespawning()) return;

						TickablePerk tickablePerk = (TickablePerk) perk;
						if (minigamer.isPlaying())
							tickablePerk.tick(minigamer);
						else
							tickablePerk.tick(player);
					}
				});
			}
		}));

		// clear legacy loadout perk owners and send real packets
		Tasks.repeat(5, Time.SECOND.x(1), () -> new HashSet<>(loadoutUsers).forEach(perkOwner -> {
			perkOwner = service.get(perkOwner.getUuid()); // update loadout perks...? not sure if necessary
			OfflinePlayer _player = PlayerUtils.getPlayer(perkOwner.getUuid());
			if (!_player.isOnline() || (!PlayerManager.get((Player) _player).isPlaying() && !isInRegion((Player) _player)) || perkOwner.getEnabledPerksByClass(LoadoutPerk.class).isEmpty()) {
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
	}

	public boolean isInRegion(Player player) {
		return Minigames.getWorldGuardUtils().isInRegion(player.getLocation(), Minigames.getLobbyRegion());
	}
}
