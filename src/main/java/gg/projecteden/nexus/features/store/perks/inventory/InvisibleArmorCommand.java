package gg.projecteden.nexus.features.store.perks.inventory;

import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.InventoryManager;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.invisiblearmour.InvisibleArmor;
import gg.projecteden.nexus.models.invisiblearmour.InvisibleArmorService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.ArmorSlot;
import io.papermc.paper.event.entity.EntityEquipmentChangedEvent;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
@Permission(InvisibleArmorCommand.PERMISSION)
@Aliases({"ia", "invisarmor", "invisarmour", "invisiblearmor"})
@WikiConfig(rank = "Store", feature = "Inventory")
public class InvisibleArmorCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "invisiblearmor.use";
	private final InvisibleArmorService service = new InvisibleArmorService();
	private InvisibleArmor invisibleArmor;

	public InvisibleArmorCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			invisibleArmor = service.get(event.getPlayer());
	}

	@Path("[on|off]")
	@Description("Toggle invisible armor")
	void run(Boolean enable) {
		if (enable == null)
			invisibleArmor.setEnabled(!invisibleArmor.isEnabled());
		else
			invisibleArmor.setEnabled(enable);

		service.save(invisibleArmor);
		invisibleArmor.updateTextures();

		if (invisibleArmor.isEnabled())
			send(PREFIX + "&cArmor hidden");
		else
			send(PREFIX + "&aArmor shown");
	}

	@Path("menu")
	@Description("Open the invisible armor configuration menu")
	void menu() {
		new InvisibleArmorProvider(invisibleArmor).open(player());
	}

	@EventHandler
	public void onEquipArmor(EntityEquipmentChangedEvent event) {
		if (!(event.getEntity() instanceof Player player))
			return;

		InvisibleArmor invisibleArmor = service.get(player);
		if (!invisibleArmor.isEnabled())
			return;

		event.getEquipmentChanges().keySet().forEach(slot -> {
			if (ArmorSlot.of(slot) != null)
				invisibleArmor.updateTextures();
		});
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) {
		InvisibleArmor invisibleArmor = service.get(event.getPlayer());
		if (!invisibleArmor.isEnabled())
			return;

		ItemBuilder item = new ItemBuilder(event.getItemDrop().getItemStack());
		ArmorSkin.applyEquippableComponent(item, ArmorSkin.of(event.getItemDrop().getItemStack()));
		event.getItemDrop().setItemStack(item.build());
	}

	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		InvisibleArmor invisibleArmor = service.get(event.getPlayer());
		if (!invisibleArmor.isEnabled())
			return;

		invisibleArmor.updateTextures();
	}

	@RequiredArgsConstructor
	@Title("&3Invisible Armour")
	private class InvisibleArmorProvider extends InventoryProvider {
		private final InvisibleArmorService service = new InvisibleArmorService();
		private final InvisibleArmor user;

		@Override
		public void onClose(InventoryManager manager) {
			super.onClose(manager);
			user.updateTextures();
		}

		@Override
		public void init() {
			addCloseItem();

			for (ArmorSlot slot : ArmorSlot.values()) {
				final int row = slot.ordinal() + 1;
				ItemStack displayItem = user.getDisplayItem(slot);
				contents.set(row, 3, ClickableItem.empty(displayItem));

				final ItemBuilder other;
				if (user.isHidden(slot))
					other = new ItemBuilder(user.getHiddenIcon(slot)).name("&cHidden").lore("&aClick to show");
				else
					other = new ItemBuilder(user.getShownIcon(slot)).name("&aShown").lore("&cClick to hide");

				contents.set(row, 5, ClickableItem.of(other.build(), e -> {
					user.toggleHide(slot);
					service.save(user);
					menu();
				}));
			}

			ItemBuilder toggle = new ItemBuilder(Material.LEVER);
			if (user.isEnabled())
				toggle.name("&cArmor hidden").lore("&eClick to show");
			else
				toggle.name("&aArmor shown").lore("&eClick to hide");
			contents.set(4, 8, ClickableItem.of(toggle.build(), e -> {
				run(null);
				menu();
			}));

			ItemBuilder save = new ItemBuilder(Material.NETHER_STAR).name("&eSave & Close");
			contents.set(5, 8, ClickableItem.of(save.build(), e -> e.getPlayer().closeInventory()));
		}
	}

}
