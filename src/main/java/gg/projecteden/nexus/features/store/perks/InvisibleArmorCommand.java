package gg.projecteden.nexus.features.store.perks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.invisiblearmour.InvisibleArmor;
import gg.projecteden.nexus.models.invisiblearmour.InvisibleArmorService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

import static gg.projecteden.nexus.features.store.perks.InvisibleArmorCommand.PERMISSION;
import static gg.projecteden.nexus.models.invisiblearmour.InvisibleArmor.SLOTS;

@NoArgsConstructor
@Permission(PERMISSION)
@Aliases({"ia", "invisarmor", "invisarmour", "invisiblearmor"})
public class InvisibleArmorCommand extends CustomCommand implements Listener {
	public static final String PERMISSION = "invisiblearmor.use";
	private final InvisibleArmorService service = new InvisibleArmorService();
	private InvisibleArmor invisibleArmor;

	public InvisibleArmorCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			invisibleArmor = service.get(event.getPlayer());
	}

	@Path("reset")
	void reset() {
		service.deleteAll();
		service.clearCache();
		send(PREFIX + "Reset");
	}

	@Path("[on|off]")
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
	void menu() {
		SmartInventory.builder()
				.provider(new InvisibleArmorProvider(invisibleArmor))
				.size(6, 9)
				.title(ChatColor.DARK_AQUA + "Invisible Armour")
				.build()
				.open(player());
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
	private class InvisibleArmorProvider extends MenuUtils implements InventoryProvider {
		private final InvisibleArmorService service = new InvisibleArmorService();
		private final InvisibleArmor user;

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			AtomicInteger row = new AtomicInteger(1);
			SLOTS.forEach(slot -> {
				ItemStack displayItem = user.getDisplayItem(slot);
				contents.set(row.get(), 2, ClickableItem.empty(displayItem));

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

				contents.set(row.get(), 4, ClickableItem.from(other.build(), e -> {
					user.toggleHide(slot);
					service.save(user);
					menu();
				}));

				contents.set(row.get(), 6, ClickableItem.from(self.build(), e -> {
					user.toggleHideSelf(slot);
					service.save(user);
					menu();
				}));

				row.getAndIncrement();
			});

			ItemBuilder toggle = new ItemBuilder(Material.LEVER);
			if (user.isEnabled())
				toggle.name("&cArmor hidden").lore("&eClick to show");
			else
				toggle.name("&aArmor shown").lore("&eClick to hide");
			contents.set(4, 8, ClickableItem.from(toggle.build(), e -> {
				run(null);
				menu();
			}));

			ItemBuilder save = new ItemBuilder(Material.NETHER_STAR).name("&eSave & Close");
			contents.set(5, 8, ClickableItem.from(save.build(), e -> e.getPlayer().closeInventory()));
		}
	}

}
