package me.pugabyte.nexus.features.store.perks;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.PacketType.Play.Client;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import eden.annotations.Environments;
import eden.utils.Env;
import eden.utils.TimeUtils.Time;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.Minigames;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.invisiblearmour.InvisibleArmor;
import me.pugabyte.nexus.models.invisiblearmour.InvisibleArmorService;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import net.md_5.bungee.api.ChatColor;
import net.minecraft.server.v1_16_R3.EnumItemSlot;
import org.bukkit.Bukkit;
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

import static me.pugabyte.nexus.features.store.perks.InvisibleArmorCommand.PERMISSION;

@NoArgsConstructor
@Permission(PERMISSION)
@Aliases({"ia", "invisarmor", "invisarmour", "invisiblearmor"})
@Environments({Env.DEV, Env.TEST})
public class InvisibleArmorCommand extends CustomCommand {
	public static final String PERMISSION = "invisiblearmor.use";
	private static final List<ItemSlot> armourSlots = Arrays.asList(ItemSlot.HEAD, ItemSlot.CHEST, ItemSlot.LEGS, ItemSlot.FEET);
	private final InvisibleArmorService service = new InvisibleArmorService();
	private InvisibleArmor invisibleArmor;

	public InvisibleArmorCommand(@NonNull CommandEvent event) {
		super(event);
		invisibleArmor = new InvisibleArmorService().get(event.getPlayer());
	}

	static {
		Tasks.repeat(Time.SECOND, Time.SECOND.x(5), InvisibleArmorCommand::sendPackets);
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

		if (invisibleArmor.isEnabled()) {
			send(PREFIX + "&cArmour hidden");
		} else {
			send(PREFIX + "&aArmour shown");
		}
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
	public void onWorldChange(PlayerChangedWorldEvent event) {
		InvisibleArmor invisibleArmor = new InvisibleArmorService().get(event.getPlayer());
		if (!invisibleArmor.isEnabled())
			return;

		sendPackets();
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent event) {
		InvisibleArmor invisibleArmor = new InvisibleArmorService().get(event.getPlayer());
		if (!invisibleArmor.isEnabled())
			return;

		sendPackets();
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		InvisibleArmor invisibleArmor = new InvisibleArmorService().get(event.getWhoClicked());
		if (!invisibleArmor.isEnabled())
			return;

		if (event.getSlotType() == SlotType.ARMOR) {
			event.setCancelled(true);
			sendPackets();
		}
	}

	@EventHandler
	public void onPlayerDamage(EntityDamageEvent event) {
		Tasks.wait(1, InvisibleArmorCommand::sendPackets);
	}

	static {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Nexus.getInstance(), Server.ENTITY_EQUIPMENT) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				InvisibleArmor invisibleArmor = new InvisibleArmorService().get(event.getPlayer());
				if (!invisibleArmor.isEnabled())
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

		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Nexus.getInstance(), Client.CLOSE_WINDOW) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				Tasks.wait(1, InvisibleArmorCommand::sendPackets);
			}
		});
	}

	private static void sendPackets() {
		AtomicInteger wait = new AtomicInteger(0);
		Bukkit.getOnlinePlayers().forEach(player -> Tasks.wait(wait.getAndIncrement(), () -> {
			InvisibleArmorService service = new InvisibleArmorService();
			InvisibleArmor invisibleArmor = service.get(player);

			if (Minigames.isMinigameWorld(player.getWorld())) {
				invisibleArmor.setEnabled(false);
				service.save(invisibleArmor);
				return;
			}

			armourSlots.forEach(slot -> sendPackets(invisibleArmor, slot));
		}));
	}

	private static void sendPackets(InvisibleArmor invisibleArmor, ItemSlot slot) {
		Player player = PlayerUtils.getPlayer(invisibleArmor.getUuid()).getPlayer();
		if (player == null)
			return;

		PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.SET_SLOT);
		packet.getIntegers().write(0, 0);
		packet.getIntegers().write(1, 9 - EnumItemSlot.fromName(slot.name().toLowerCase()).getSlotFlag());
		packet.getItemModifier().write(0, invisibleArmor.isEnabled() ? new ItemStack(Material.AIR) : invisibleArmor.getItem(slot));

		Bukkit.getOnlinePlayers().stream().filter(_player -> player.getWorld() == _player.getWorld()).forEach(_player -> {
			boolean self = player.getUniqueId() == _player.getUniqueId();

			try {
				PacketContainer clone = packet.deepClone();

				if (self) {
					if (invisibleArmor.showSelf(slot))
						clone.getItemModifier().write(0, invisibleArmor.getItem(slot));
				} else {
					if (invisibleArmor.show(slot))
						clone.getItemModifier().write(0, invisibleArmor.getItem(slot));
				}

				ProtocolLibrary.getProtocolManager().sendServerPacket(_player, clone);
			} catch (InvocationTargetException ex) {
				Nexus.log("Error trying to send invisible armour packets from " + player.getName() + " to " + _player.getName());
				ex.printStackTrace();
			}
		});
	}

	private class InvisibleArmorProvider extends MenuUtils implements InventoryProvider {
		InvisibleArmorService service = new InvisibleArmorService();
		InvisibleArmor invisibleArmor;

		public InvisibleArmorProvider(InvisibleArmor invisibleArmor) {
			this.invisibleArmor = invisibleArmor;
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);

			AtomicInteger row = new AtomicInteger(1);
			armourSlots.forEach(slot -> {
				if (!ItemUtils.isNullOrAir(invisibleArmor.getItem(slot))) {
					contents.set(row.get(), 2, ClickableItem.empty(invisibleArmor.getItem(slot)));

					ItemBuilder item;
					if (invisibleArmor.show(slot))
						item = new ItemBuilder(Material.LIME_WOOL).name("&aShown").lore("&cClick to hide");
					else
						item = new ItemBuilder(Material.RED_WOOL).name("&cHidden").lore("&aClick to show");

					String lore = "&eThis will allow you to use things like elytras and depth strider while still hiding your armour from other players";
					ItemBuilder self;
					if (invisibleArmor.showSelf(slot))
						self = new ItemBuilder(Material.LIME_WOOL).name("&aShow-Self: Enabled").lore("&cClick to disable");
					else
						self = new ItemBuilder(Material.RED_WOOL).name("&cShow-Self: Disabled").lore("&aClick to enable");

					contents.set(row.get(), 4, ClickableItem.from(item.build(), e -> {
						invisibleArmor.toggle(slot);
						service.save(invisibleArmor);
						sendPackets(invisibleArmor, slot);
						menu();
					}));

					contents.set(row.get(), 6, ClickableItem.from(self.lore("").lore(lore).build(), e -> {
						invisibleArmor.toggleShowSelf(slot);
						service.save(invisibleArmor);
						sendPackets(invisibleArmor, slot);
						menu();
					}));

					row.getAndIncrement();
				}
			});

			ItemBuilder toggle = new ItemBuilder(Material.LEVER);
			if (invisibleArmor.isEnabled())
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
	}

}
