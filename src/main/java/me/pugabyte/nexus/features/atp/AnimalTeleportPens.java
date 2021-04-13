package me.pugabyte.nexus.features.atp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.menus.MenuUtils.ConfirmationMenu;
import me.pugabyte.nexus.models.banker.BankerService;
import me.pugabyte.nexus.models.banker.Transaction.TransactionCause;
import me.pugabyte.nexus.models.shop.Shop.ShopGroup;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.WorldGuardUtils;
import net.citizensnpcs.api.CitizensAPI;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.utils.StringUtils.colorize;

@NoArgsConstructor
public class AnimalTeleportPens {
	private final String PREFIX = StringUtils.getPrefix("ATP");
	private Player player;
	private WorldGuardUtils WGUtils;

	public AnimalTeleportPens(Player player) {
		this.player = player;
		WGUtils = new WorldGuardUtils(player);
	}

	public boolean multiplePlayers() {
		return WGUtils.getPlayersInRegion(getRegion(player).getId()).size() > 1;
	}

	public List<Entity> getEntities() {
		List<Entity> finalEntities = new ArrayList<>();
		for (Entity entity : WGUtils.getEntitiesInRegion(getRegion(player).getId())) {
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
				case MUSHROOM_COW:
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
				case MUSHROOM_COW:
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
			PlayerUtils.send(player, PREFIX + "You are not inside an ATP region");
			return;
		}
		if (multiplePlayers()) {
			player.closeInventory();
			PlayerUtils.send(player, PREFIX + "&cDetected multiple players. Cancelling.");
			return;
		}
		List<Entity> entities = getEntities();
		if (entities.size() == 0) {
			PlayerUtils.send(player, PREFIX + "&cThere are no entities to teleport");
			return;
		}
		int price = getPrice(entities);
		double balance = new BankerService().getBalance(player, ShopGroup.SURVIVAL);
		if (balance < price) {
			PlayerUtils.send(player, PREFIX + "&cYou do not have enough money to use the ATP");
			return;
		}

		ConfirmationMenu.builder()
				.title(colorize("&3Teleport &e" + entities.size() + " &3entities for &e$" + price + "&3?"))
				.onConfirm(e -> Tasks.wait(4, () -> teleportAll(entities, toLoc, price)))
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
			new BankerService().withdraw(player, price, ShopGroup.SURVIVAL, TransactionCause.ANIMAL_TELEPORT_PEN);
		});
	}

}
