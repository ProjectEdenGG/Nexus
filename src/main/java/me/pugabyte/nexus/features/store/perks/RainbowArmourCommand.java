package me.pugabyte.nexus.features.store.perks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.lexikiq.HasPlayer;
import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.resourcepack.CustomModel;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.UUID;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@NoArgsConstructor
@Permission("rainbowarmour.use")
@Aliases({"rainbowarmor", "rba"})
public class RainbowArmourCommand extends CustomCommand implements Listener {
	@Getter
	private static final HashMap<UUID, RainbowArmourPlayer> enabledPlayers = new HashMap<>();
	private static final int rate = 12;

	public RainbowArmourCommand(CommandEvent event) {
		super(event);
	}

	public static RainbowArmourPlayer getPlayer(HasPlayer hasPlayer) {
		Player player = hasPlayer.getPlayer();
		UUID uuid = player.getUniqueId();
		if (!enabledPlayers.containsKey(uuid))
			enabledPlayers.put(uuid, new RainbowArmourPlayer(player, -1));

		return enabledPlayers.get(uuid);
	}

	public static boolean isEnabled(UUID uuid) {
		return getEnabledPlayers().containsKey(uuid) && getEnabledPlayers().get(uuid).isEnabled();
	}

	public static boolean isEnabled(HasUniqueId uuid) {
		return isEnabled(uuid.getUniqueId());
	}

	@Path
	void toggle() {
		RainbowArmourPlayer rbaPlayer = getPlayer(player());
		if (rbaPlayer.isEnabled()) {
			stopArmour(rbaPlayer);
			send("&cRainbow armour unequipped!");
			rbaPlayer.setEnabled(false);
		} else {
			rbaPlayer.setTaskId(startArmour(rbaPlayer));
			rbaPlayer.setEnabled(true);
			send("&cR&6a&ei&an&bb&5o&dw &earmour equipped!");
			getEnabledPlayers().put(uuid(), rbaPlayer);
		}
	}

	private static boolean isLeatherArmour(ItemStack item) {
		if (isNullOrAir(item))
			return false;
		if (CustomModel.exists(item))
			return false;

		Material material = item.getType();
		return material == Material.LEATHER_HELMET ||
				material == Material.LEATHER_CHESTPLATE ||
				material == Material.LEATHER_LEGGINGS ||
				material == Material.LEATHER_BOOTS;
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (!(event.getWhoClicked() instanceof Player)) return;
		Player player = (Player) event.getWhoClicked();
		if (player.getGameMode() != GameMode.SURVIVAL) return;
		if (isEnabled(player)) {
			ItemStack item = event.getCurrentItem();
			if (event.getSlotType() == InventoryType.SlotType.ARMOR && isLeatherArmour(item)) {
				RainbowArmourCommand.removeColor(item);
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (isEnabled(player)) {
			for (ItemStack itemStack : event.getDrops()) {
				if (isLeatherArmour(itemStack)) {
					LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
					meta.setColor(null);
					itemStack.setItemMeta(meta);
				}
			}
		}
	}

	@EventHandler
	public void onLogout(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (isEnabled(player))
			RainbowArmourCommand.removeColor(player.getInventory());
	}

	static ItemStack removeColor(ItemStack itemStack) {
		LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
		meta.setColor(null);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	static void removeColor(PlayerInventory inv) {
		ItemStack[] armour = inv.getArmorContents();

		int counter = 0;
		for (ItemStack itemStack : armour) {
			if (itemStack != null && itemStack.getType().toString().toLowerCase().startsWith("leather_")) {
				armour[counter] = removeColor(itemStack);
			}
			counter++;
		}

		inv.setArmorContents(armour);
	}

	private static ItemStack getColor(ItemStack itemStack, Color color) {
		LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
		meta.setColor(color);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	private static void stopArmour(RainbowArmourPlayer rbaPlayer) {
		Bukkit.getScheduler().cancelTask(rbaPlayer.getTaskId());
		removeColor(rbaPlayer.getPlayer().getInventory());
	}

	private static int startArmour(RainbowArmourPlayer rbaPlayer) {
		Player player = rbaPlayer.getPlayer();

		return Bukkit.getScheduler().scheduleSyncRepeatingTask(Nexus.getInstance(), () -> {
			if (WorldGroup.MINIGAMES.contains(rbaPlayer.getPlayer().getWorld())) return;

			int r = rbaPlayer.getR();
			int g = rbaPlayer.getG();
			int b = rbaPlayer.getB();

			if (r > 0 && b == 0) {
				if (r != 255 || g >= 255) {
					r -= rate;
				}
				g += rate;
			}
			if (g > 0 && r == 0) {
				if (g != 255 || b >= 255) {
					g -= rate;
				}
				b += rate;
			}
			if (b > 0 && g == 0) {
				if (b != 255 || r >= 255) {
					b -= rate;
				}
				r += rate;
			}

			if (r < 0) r = 0;
			if (r > 255) r = 255;
			if (g < 0) g = 0;
			if (g > 255) g = 255;
			if (b < 0) b = 0;
			if (b > 255) b = 255;

			PlayerInventory inv = player.getInventory();
			ItemStack[] armour = inv.getArmorContents();
			Color color = Color.fromRGB(r, g, b);
			rbaPlayer.setRGB(color);

			int counter = 0;
			for (ItemStack itemStack : armour) {
				if (isLeatherArmour(itemStack))
					armour[counter] = getColor(itemStack, color);
				counter++;
			}

			inv.setArmorContents(armour);
		}, 4, 2);
	}

	@Getter
	@Setter
	public static final class RainbowArmourPlayer {
		private final Player player;
		private int taskId;
		private boolean enabled;
		private int r = 255;
		private int g, b = 0;

		public RainbowArmourPlayer(Player player, int taskId) {
			this.player = player;
			this.taskId = taskId;
			this.enabled = taskId > 0;
		}

		public void setRGB(Color color) {
			r = color.getRed();
			g = color.getGreen();
			b = color.getBlue();
		}
	}
}
