package gg.projecteden.nexus.features.store.perks.inventory;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.*;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.invisiblearmour.InvisibleArmor;
import gg.projecteden.nexus.models.invisiblearmour.InvisibleArmorService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.ArmorSlot;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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
		sendPackets();

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
	public void onTeleport(PlayerTeleportEvent event) {
		new InvisibleArmorService().get(event.getPlayer()).sendPackets();
	}

	static {
		Tasks.repeat(TickTime.TICK, TickTime.TICK, InvisibleArmorCommand::sendPackets);
	}

	private static void sendPackets() {
		final InvisibleArmorService service = new InvisibleArmorService();
		OnlinePlayers.getAll().forEach(player -> service.get(player).sendPackets());
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		Tasks.wait(1, new InvisibleArmorService().get(event.getPlayer())::sendResetPackets);
	}

	@RequiredArgsConstructor
	@Title("&3Invisible Armour")
	private class InvisibleArmorProvider extends InventoryProvider {
		private final InvisibleArmorService service = new InvisibleArmorService();
		private final InvisibleArmor user;

		@Override
		public void init() {
			addCloseItem();

			for (ArmorSlot slot : ArmorSlot.values()) {
				final int row = slot.ordinal() + 1;
				ItemStack displayItem = user.getDisplayItem(slot);
				contents.set(row, 2, ClickableItem.empty(displayItem));

				final ItemBuilder other;
				if (user.isHidden(slot))
					other = new ItemBuilder(user.getHiddenIcon(slot)).name("&cHidden").lore("&aClick to show");
				else
					other = new ItemBuilder(user.getShownIcon(slot)).name("&aShown").lore("&cClick to hide");

				String lore = "&eThis will allow you to use things like elytras and depth strider while still hiding your armor from other players";
				final ItemBuilder self;
				if (user.isHiddenFromSelf(slot))
					self = new ItemBuilder(user.getHiddenIcon(slot)).name("&cSelf: Hidden").lore("&aClick to show", "", lore);
				else
					self = new ItemBuilder(user.getShownIcon(slot)).name("&aSelf: Shown").lore("&cClick to hide", "", lore);

				contents.set(row, 4, ClickableItem.of(other.build(), e -> {
					user.toggleHide(slot);
					service.save(user);
					menu();
				}));

				contents.set(row, 6, ClickableItem.of(self.build(), e -> {
					user.toggleHideSelf(slot);
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
