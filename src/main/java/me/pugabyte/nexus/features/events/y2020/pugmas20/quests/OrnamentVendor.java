package me.pugabyte.nexus.features.events.y2020.pugmas20.quests;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20;
import me.pugabyte.nexus.features.events.y2020.pugmas20.menu.AdventMenu;
import me.pugabyte.nexus.features.events.y2020.pugmas20.quests.Trees.PugmasTreeType;
import me.pugabyte.nexus.models.pugmas20.Pugmas20Service;
import me.pugabyte.nexus.models.pugmas20.Pugmas20User;
import me.pugabyte.nexus.utils.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.events.y2020.pugmas20.Pugmas20.isAtPugmas;
import static me.pugabyte.nexus.utils.StringUtils.camelCase;

@NoArgsConstructor
public class OrnamentVendor implements Listener {
	public enum Ornament {
		RED(PugmasTreeType.BLOODWOOD, -3),
		ORANGE(PugmasTreeType.MAHOGANY, -4),
		YELLOW(PugmasTreeType.EUCALYPTUS, -5),
		GREEN(PugmasTreeType.WILLOW, -6),
		CYAN(PugmasTreeType.CRYSTAL, -7),
		BLUE(PugmasTreeType.MAGIC, -8),
		PURPLE(PugmasTreeType.OAK, -9),
		MAGENTA(PugmasTreeType.TEAK, -10),
		GRAY(PugmasTreeType.MAPLE, -11),
		WHITE(PugmasTreeType.BLISTERWOOD, -12);

		@Getter
		private final PugmasTreeType treeType;
		@Getter
		private final ItemStack skull;

		Ornament(PugmasTreeType treeType, int relative) {
			this.treeType = treeType;
			ItemStack itemStack = AdventMenu.origin.getRelative(relative, 0, 0).getDrops().stream().findFirst().orElse(null);
			if (ItemUtils.isNullOrAir(itemStack))
				this.skull = null;
			else
				this.skull = Pugmas20.item(itemStack).name(camelCase(name() + " Ornament")).build();
		}
	}

	public static List<ItemStack> getOrnaments(Player player) {
		List<ItemStack> ornaments = new ArrayList<>();
		for (Ornament ornament : Ornament.values()) {
			if (player.getInventory().containsAtLeast(ornament.getSkull(), 1))
				ornaments.add(ornament.getSkull());
		}

		return ornaments;
	}

	@EventHandler
	public void onItemFrameInteract(PlayerInteractAtEntityEvent event) {
		if (EquipmentSlot.HAND != event.getHand())
			return;

		Player player = event.getPlayer();
		if (!isAtPugmas(player))
			return;

		Entity entity = event.getRightClicked();
		if (entity.getType() != EntityType.ITEM_FRAME)
			return;

		if (!isAtPugmas(entity.getLocation(), "lumberjacksaxe"))
			return;

		event.setCancelled(true);

		Pugmas20Service service = new Pugmas20Service();
		Pugmas20User user = service.get(player);

		if (user.getOrnamentVendorStage() == QuestStage.NOT_STARTED)
			return;

		if (!player.getInventory().contains(Trees.getLumberjacksAxe())) {
			ItemUtils.giveItem(player, Trees.getLumberjacksAxe());
			Quests.sound_obtainItem(player);
			user.send("Message"); // TODO PUGMAS
		}
	}
}
