package me.pugabyte.bncore.features.store.perks;

import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.menus.MenuUtils;
import me.pugabyte.bncore.features.minigames.Minigames;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.invisiblearmour.InvisibleArmour;
import me.pugabyte.bncore.models.invisiblearmour.InvisibleArmourService;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor
@Permission("invis.armour")
@Aliases({"ia", "invisarmor", "invisarmour", "invisiblearmor"})
public class InvisibleArmourCommand extends CustomCommand {
	private static final List<ItemSlot> armourSlots = Arrays.asList(ItemSlot.HEAD, ItemSlot.CHEST, ItemSlot.LEGS, ItemSlot.FEET);
	private final InvisibleArmourService service = new InvisibleArmourService();
	private InvisibleArmour invisibleArmour;

	public InvisibleArmourCommand(@NonNull CommandEvent event) {
		super(event);
		invisibleArmour = new InvisibleArmourService().get(event.getPlayer());
	}

	static {
//		Tasks.repeat(Time.SECOND, Time.SECOND.x(5), InvisibleArmourCommand::sendPackets);
	}

	@Path("clearCache")
	void clearCache() {
		service.clearCache();
		send("Cache cleared");
	}

	@Path("[on|off]")
	void run(Boolean enable) {
		error("Temporarily disabled");

		if (enable == null)
			invisibleArmour.setEnabled(!invisibleArmour.isEnabled());
		else
			invisibleArmour.setEnabled(enable);

		service.save(invisibleArmour);
		sendPackets();

		if (invisibleArmour.isEnabled()) {
			send(PREFIX + "&cArmour hidden");
		} else {
			send(PREFIX + "&aArmour shown");
		}
	}

	@Path("menu")
	void menu() {
		error("Temporarily disabled");

		SmartInventory.builder()
				.provider(new InvisibleArmourProvider(invisibleArmour))
				.size(6, 9)
				.title(ChatColor.DARK_AQUA + "Invisible Armour")
				.build()
				.open(player());
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		InvisibleArmour invisibleArmour = new InvisibleArmourService().get(event.getPlayer());
		if (!invisibleArmour.isEnabled())
			return;

		sendPackets();
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		InvisibleArmour invisibleArmour = new InvisibleArmourService().get(event.getPlayer());
		if (!invisibleArmour.isEnabled())
			return;

		sendPackets();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		InvisibleArmour invisibleArmour = new InvisibleArmourService().get((Player) event.getWhoClicked());
		if (!invisibleArmour.isEnabled())
			return;

		if (event.getSlotType() == SlotType.ARMOR) {
			event.setCancelled(true);
			sendPackets();
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		Tasks.wait(1, InvisibleArmourCommand::sendPackets);
	}

	static {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(BNCore.getInstance(), Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				InvisibleArmour invisibleArmour = new InvisibleArmourService().get(event.getPlayer());
				if (!invisibleArmour.isEnabled())
					return;

				for (ItemSlot value : event.getPacket().getItemSlots().getValues())
					if (armourSlots.contains(value)) {
						event.setCancelled(true);
						return;
					}
			}

			// Not sure why this is needed, it kept erroring without it
			@Override
			public void onPacketSending(PacketEvent event) {}
		});

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(BNCore.getInstance(), Client.CLOSE_WINDOW) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Tasks.wait(1, InvisibleArmourCommand::sendPackets);
			}
		});
	}

	private static void sendPackets() {
		AtomicInteger wait = new AtomicInteger(0);
		Bukkit.getOnlinePlayers().forEach(player -> Tasks.wait(wait.getAndIncrement(), () -> {
			InvisibleArmourService service = new InvisibleArmourService();
			InvisibleArmour invisibleArmour = service.get(player);

			if (Minigames.isMinigameWorld(player.getWorld())) {
				invisibleArmour.setEnabled(false);
				service.save(invisibleArmour);
				return;
			}

			armourSlots.forEach(slot -> sendPackets(invisibleArmour, slot));
		}));
	}

	private static void sendPackets(InvisibleArmour invisibleArmour, ItemSlot slot) {
		if (true) return;
		Player player = Utils.getPlayer(invisibleArmour.getUuid()).getPlayer();
		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(Server.ENTITY_EQUIPMENT);
		packet.getItemSlots().write(0, slot);
		packet.getEntityModifier(player.getWorld()).write(0, player);
		packet.getItemModifier().write(0, invisibleArmour.isEnabled() ? new ItemStack(Material.AIR) : invisibleArmour.getItem(slot));

		Bukkit.getOnlinePlayers().stream().filter(_player -> player.getWorld() == _player.getWorld()).forEach(_player -> {
			boolean self = player.getUniqueId() == _player.getUniqueId();

			try {
				PacketContainer clone = packet.deepClone();

				// Causes a weird sound effect
				if (self && invisibleArmour.showSelf(slot) && invisibleArmour.show(slot))
					return;

				if (self) {
					if (invisibleArmour.showSelf(slot))
						clone.getItemModifier().write(0, invisibleArmour.getItem(slot));
				} else {
					if (invisibleArmour.show(slot))
						clone.getItemModifier().write(0, invisibleArmour.getItem(slot));
				}

				ProtocolLibrary.getProtocolManager().sendServerPacket(_player, clone);
			} catch (InvocationTargetException ex) {
				BNCore.log("Error trying to send invisible armour packets from " + player.getName() + " to " + _player.getName());
				ex.printStackTrace();
			}
		});
	}

	private class InvisibleArmourProvider extends MenuUtils implements InventoryProvider {
		InvisibleArmourService service = new InvisibleArmourService();
		InvisibleArmour invisibleArmour;

		public InvisibleArmourProvider(InvisibleArmour invisibleArmour) {
			this.invisibleArmour = invisibleArmour;
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			AtomicInteger row = new AtomicInteger(1);
			armourSlots.forEach(slot -> {
				if (!Utils.isNullOrAir(invisibleArmour.getItem(slot))) {
					contents.set(row.get(), 2, ClickableItem.empty(invisibleArmour.getItem(slot)));

					ItemBuilder item;
					if (invisibleArmour.show(slot))
						item = new ItemBuilder(Material.LIME_WOOL).name("&aShown").lore("&cClick to hide");
					else
						item = new ItemBuilder(Material.RED_WOOL).name("&cHidden").lore("&aClick to show");

					String lore = "&eThis will allow you to use things like elytras and depth strider while still hiding your armour from other players";
					ItemBuilder self;
					if (invisibleArmour.showSelf(slot))
						self = new ItemBuilder(Material.LIME_WOOL).name("&aShow-Self: Enabled").lore("&cClick to disable");
					else
						self = new ItemBuilder(Material.RED_WOOL).name("&cShow-Self: Disabled").lore("&aClick to enable");

					contents.set(row.get(), 4, ClickableItem.from(item.build(), e -> {
						invisibleArmour.toggle(slot);
						service.save(invisibleArmour);
						sendPackets(invisibleArmour, slot);
						menu();
					}));

					contents.set(row.get(), 6, ClickableItem.from(self.lore("").lore(lore).build(), e -> {
						invisibleArmour.toggleShowSelf(slot);
						service.save(invisibleArmour);
						sendPackets(invisibleArmour, slot);
						menu();
					}));

					row.getAndIncrement();
				}
			});

			ItemBuilder toggle = new ItemBuilder(Material.LEVER);
			if (invisibleArmour.isEnabled())
				toggle.name("&cArmour hidden").lore("&eClick to show armour");
			else
				toggle.name("&aArmour shown").lore("&eClick to hide armour");
			contents.set(4, 8, ClickableItem.from(toggle.build(), e -> {
				run(null);
				menu();
			}));

			ItemBuilder save = new ItemBuilder(Material.NETHER_STAR).name("&eSave & Close");
			contents.set(5, 8, ClickableItem.from(save.build(), e -> e.getPlayer().closeInventory()));
		}

		@Override
		public void update(Player player, InventoryContents contents) {}
	}

}
