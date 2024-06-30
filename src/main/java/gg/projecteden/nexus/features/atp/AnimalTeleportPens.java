package gg.projecteden.nexus.features.atp;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.models.banker.Banker;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import lombok.NoArgsConstructor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.CitizensUtils.isNPC;

@NoArgsConstructor
public class AnimalTeleportPens {
	private final String PREFIX = StringUtils.getPrefix("ATP");
	private Player player;
	private WorldGuardUtils worldguard;

	public AnimalTeleportPens(Player player) {
		this.player = player;
		this.worldguard = new WorldGuardUtils(player);
	}

	public boolean multiplePlayers() {
		return worldguard.getPlayersInRegion(getRegion(player)).size() > 1;
	}

	public List<Entity> getEntities() {
		List<Entity> finalEntities = new ArrayList<>();
		for (Entity entity : worldguard.getEntitiesInRegion(getRegion(player))) {
			if (isNPC(entity))
				continue;

			switch (entity.getType()) {
				case PIG, RABBIT, FOX, TURTLE, COW, SHEEP, MUSHROOM_COW, POLAR_BEAR,
					PANDA, GOAT, HORSE, DONKEY, MULE, LLAMA, TRADER_LLAMA, VILLAGER,
					SHULKER ->
					finalEntities.add(entity);
			}
		}
		return finalEntities;
	}

	public int getPrice(List<Entity> entities) {
		int price = 0;
		for (Entity entity : entities) {
			switch (entity.getType()) {
				case PIG, RABBIT, FOX, TURTLE ->
						price += 100;
				case COW, SHEEP, MUSHROOM_COW, POLAR_BEAR, PANDA, GOAT ->
						price += 150;
				case HORSE, DONKEY, MULE, LLAMA, TRADER_LLAMA ->
						price += 250;
				case VILLAGER ->
						price += 500;
				case SHULKER ->
						price += 2000;
				default ->
						price += 50;
			}
		}
		return price;
	}

	public ProtectedRegion getRegion(Player player) {
		return worldguard.getRegionsLikeAt("atp_.*", player.getLocation()).stream().findFirst().orElse(null);
	}

	public void confirm(Player player, Location toLoc) {
		ProtectedRegion region = getRegion(player);
		if (region == null) {
			PlayerUtils.send(player, PREFIX + "&cYou are not inside an ATP region");
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
		if (!Banker.of(player).has(price, ShopGroup.of(player))) {
			PlayerUtils.send(player, PREFIX + "&cYou do not have enough money to use the ATP");
			return;
		}

		ConfirmationMenu.builder()
				.title("&3Teleport &e" + entities.size() + " &3entities for &e$" + price + "&3?")
				.onConfirm(e -> Tasks.wait(4, () -> teleportAll(entities, toLoc, price)))
				.open(player);
	}

	public void teleportAll(List<Entity> entities, Location toLoc, int price) {
		if (entities.size() > 0) {
			entities.get(0).teleportAsync(toLoc);
			Tasks.wait(1, () -> {
				entities.remove(0);
				teleportAll(entities, toLoc, price);
			});
		}
		Tasks.wait(4, () -> {
			player.teleportAsync(toLoc, TeleportCause.COMMAND);
			new BankerService().withdrawal(player, -price, ShopGroup.SURVIVAL, TransactionCause.ANIMAL_TELEPORT_PEN);
		});
	}

}
