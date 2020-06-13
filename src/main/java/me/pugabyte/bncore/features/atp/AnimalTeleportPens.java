package me.pugabyte.bncore.features.atp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

@NoArgsConstructor
public class AnimalTeleportPens {
	String PREFIX = StringUtils.getPrefix("ATP");
	Player player;
	WorldGuardUtils WGUtils;

	public AnimalTeleportPens(Player player) {
		this.player = player;
		WGUtils = new WorldGuardUtils(player);
	}

	public boolean multiplePlayers() {
		int size = WGUtils.getPlayersInRegion(getRegion(player).getId()).size();
		if (size > 1) return true;
		return false;
	}

	public List<Entity> getEntities() {
		List<Entity> finalEntities = new ArrayList<>();
		for (Entity entity : WGUtils.getEntitiesInRegion(player.getWorld(), getRegion(player).getId())) {
			if (CitizensAPI.getNPCRegistry().isNPC(entity)) continue;
			switch (entity.getType()) {
				case BEE:
				case FOX:
				case CAT:
				case PANDA:
				case TRADER_LLAMA:
				case TURTLE:
				case PIG:
				case COW:
				case SHEEP:
				case CHICKEN:
				case HORSE:
				case DONKEY:
				case MULE:
				case LLAMA:
				case PARROT:
				case WOLF:
				case OCELOT:
				case RABBIT:
				case POLAR_BEAR:
				case VILLAGER:
					finalEntities.add(entity);
					break;
				default:
			}
		}
		return finalEntities;
	}

	public int getPrice(List<Entity> entities) {
		int price = 0;
		for (Entity entity : entities) {
			switch (entity.getType()) {
				case PIG:
				case RABBIT:
				case FOX:
				case TURTLE:
					price += 100;
					break;
				case COW:
				case SHEEP:
				case POLAR_BEAR:
				case PANDA:
					price += 150;
					break;
				case HORSE:
				case DONKEY:
				case MULE:
				case LLAMA:
				case TRADER_LLAMA:
					price += 250;
					break;
				case VILLAGER:
					price += 500;
					break;
				default:
					price += 50;
					break;
			}
		}
		return price;
	}

	public ProtectedRegion getRegion(Player player) {
		for (ProtectedRegion region : WGUtils.getRegionsAt(player.getLocation())) {
			if (region.getId().contains("atp_")) return region;
		}
		return null;
	}

	public void confirm(Player player, Location toLoc) {
		ProtectedRegion region = getRegion(player);
		if (region == null) {
			player.sendMessage(PREFIX + "You are not inside an ATP region");
			return;
		}
		if (multiplePlayers()) {
			player.closeInventory();
			player.sendMessage(PREFIX + colorize("&cDetected multiple players. Cancelling."));
			return;
		}
		List<Entity> entities = getEntities();
		if (entities.size() == 0) {
			player.sendMessage(PREFIX + "&cThere are no entities to teleport");
			return;
		}
		int price = getPrice(entities);
		double balance = BNCore.getEcon().getBalance(player);
		if (balance < price) {
			player.sendMessage(PREFIX + "&cYou do not have enough money to use the ATP");
			return;
		}

		ConfirmationMenu.builder()
				.title(colorize("&3Teleport &e" + entities.size() + " &3entities for &e$" + price + "&3?"))
				.onConfirm((e2) -> Tasks.wait(4, () -> teleportAll(entities, toLoc, price)))
				.open(player);
	}

	public void teleportAll(List<Entity> entities, Location toLoc, int price) {
		if (entities.size() > 0) {
			entities.get(0).teleport(toLoc);
			Tasks.wait(1, () -> {
				entities.remove(0);
				teleportAll(entities, toLoc, price);
			});
		}
		Tasks.wait(4, () -> {
			player.teleport(toLoc);
			BNCore.getEcon().withdrawPlayer(player, price);
		});
	}

}
