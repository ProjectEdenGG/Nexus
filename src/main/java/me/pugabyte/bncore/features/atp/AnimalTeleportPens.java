package me.pugabyte.bncore.features.atp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class AnimalTeleportPens {

	public AnimalTeleportPens(Player player) {
		this.player = player;
		WGUtils = new WorldGuardUtils(player.getWorld());
	}

	String PREFIX = StringUtils.getPrefix("ATP");
	Player player;
	WorldGuardUtils WGUtils;

	public boolean multiplePlayers(ProtectedRegion region) {
		int size = WGUtils.getPlayersInRegion(region.getId()).size();
		if (size > 1) return true;
		return false;
	}

	public List<Entity> getEntities(World world, ProtectedRegion region) {
		List<Entity> finalEntities = new ArrayList<>();
		for (Entity entity : WGUtils.getEntitiesInRegion(world, region.getId())) {
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
		if (multiplePlayers(region)) {
			player.closeInventory();
			player.sendMessage(PREFIX + StringUtils.colorize("&cDetected multiple players. Cancelling."));
			return;
		}
		List<Entity> entities = getEntities(player.getWorld(), region);
		int price = getPrice(entities);
		double balance = BNCore.getEcon().getBalance(player);
		if (balance < price) {
			player.sendMessage(PREFIX + "&eYou do not have enough money to use the ATP.");
			return;
		}
		MenuUtils.confirmMenu(player, MenuUtils.ConfirmationMenu.builder().title(StringUtils.colorize("&3Teleport &e" +
				entities.size() + " &3entities for &e$" + price + "&3?")).onConfirm((e2) -> {
			Tasks.wait(4, () -> teleportAll(player, entities, toLoc, price));
		}).build());
	}

	public void teleportAll(Player player, List<Entity> entities, Location toLoc, int price) {
		if (entities.size() > 0) {
			entities.get(0).teleport(toLoc);
			Tasks.wait(1, () -> {
				entities.remove(0);
				teleportAll(player, entities, toLoc, price);
			});
		}
		Tasks.wait(4, () -> {
			player.teleport(toLoc);
			BNCore.getEcon().withdrawPlayer(player, price);
		});
	}

}
