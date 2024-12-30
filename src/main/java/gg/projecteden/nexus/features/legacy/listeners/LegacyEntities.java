package gg.projecteden.nexus.features.legacy.listeners;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.WorldGuardUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import java.util.List;

public class LegacyEntities implements Listener {

	@EventHandler
	public void onClickBook(PlayerInteractEntityEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.LEGACY)
			return;

		if (!(event.getRightClicked() instanceof ItemFrame itemFrame))
			return;

		ItemStack item = itemFrame.getItem();
		if (Nullables.isNullOrAir(item))
			return;

		if (item.getType() == Material.WRITABLE_BOOK) {
			final BookMeta bookMeta = (BookMeta) item.getItemMeta();
			item = new ItemBuilder(Material.WRITTEN_BOOK)
				.bookTitle("Legacy Book")
				.bookAuthor("Server")
				.bookPages(bookMeta.pages())
				.build();
		}

		if (item.getType() != Material.WRITTEN_BOOK)
			return;

		event.setCancelled(true);
		event.getPlayer().openBook(item);
	}

	@EventHandler
	public void on(EntityAddToWorldEvent event) {
		if (!(event.getEntity() instanceof LivingEntity entity))
			return;

		if (WorldGroup.of(entity) != WorldGroup.LEGACY)
			return;

		if (entity instanceof Player)
			return;

		entity.setAI(false);
	}

	private static final List<EntityType> ALLOWED_ENTITY_TYPES = List.of(
		EntityType.ARMOR_STAND,
		EntityType.ITEM_FRAME,
		EntityType.GLOW_ITEM_FRAME,
		EntityType.HORSE,
		EntityType.MULE,
		EntityType.DONKEY,
		EntityType.LLAMA,
		EntityType.CHEST_MINECART,
		EntityType.FURNACE_MINECART,
		EntityType.HOPPER_MINECART
	);

	// TODO 1.19 Fix interacting with item frames
	@EventHandler
	public void onItemFrameInteract(PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.LEGACY)
			return;

		final Entity entity = event.getRightClicked();
		if (!(entity instanceof ItemFrame itemFrame))
			return;

		if (!new WorldGuardUtils(entity).getRegionsAt(entity.getLocation()).isEmpty())
			return;

		final ItemStack mainHand = player.getInventory().getItemInMainHand();
		if (Nullables.isNullOrAir(itemFrame.getItem())) {
			if (!Nullables.isNullOrAir(mainHand)) {
				itemFrame.setItem(mainHand.clone());
				player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
			}
		} else {
			if (Nullables.isNullOrAir(mainHand)) {
				player.getInventory().setItemInMainHand(itemFrame.getItem().clone());
				itemFrame.setItem(new ItemStack(Material.AIR));
			}
		}
	}

	@EventHandler
	public void on(PlayerInteractEntityEvent event) {
		final Player player = event.getPlayer();
		if (WorldGroup.of(player) != WorldGroup.LEGACY)
			return;

		final Entity entity = event.getRightClicked();
		if (ALLOWED_ENTITY_TYPES.contains(entity.getType()))
			return;

		event.setCancelled(true);

		if (!new CooldownService().check(player, "legacy_no_interact", TickTime.SECOND.x(3)))
			return;

		PlayerUtils.send(player, "&c&lHey! &7You cannot interact with that in the legacy world");
	}

	@EventHandler
	public void on(EntityDamageByEntityEvent event) {
		if (WorldGroup.of(event.getEntity()) != WorldGroup.LEGACY)
			return;

		event.setCancelled(true);
	}

}
