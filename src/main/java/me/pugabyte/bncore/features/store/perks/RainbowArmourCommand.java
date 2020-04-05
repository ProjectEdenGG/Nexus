package me.pugabyte.bncore.features.store.perks;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
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

@NoArgsConstructor
@Permission("rainbow.armour")
@Aliases({"rainbowarmor", "rba"})
public class RainbowArmourCommand extends CustomCommand implements Listener {
	@Getter
	private static HashMap<Player, RainbowArmourPlayer> enabledPlayers = new HashMap<>();
	private int rate = 12;
	private int id = 0;

	RainbowArmourCommand(CommandEvent event) {
		super(event);
	}

	public static RainbowArmourPlayer getPlayer(Player player) {
		if (!enabledPlayers.containsKey(player))
			enabledPlayers.put(player, new RainbowArmourPlayer(player, -1));

		return enabledPlayers.get(player);
	}

	@Path
	void toggle() {
		RainbowArmourPlayer rbaPlayer = getPlayer(player());
		if (rbaPlayer.isEnabled()) {
			stopArmour(rbaPlayer);
			send("&cRainbow armour unequipped!");
			rbaPlayer.setEnabled(false);
		} else {
			rbaPlayer.setTaskID(startArmour(rbaPlayer));
			rbaPlayer.setEnabled(true);
			send("&cR&6a&ei&an&bb&5o&dw &earmour equipped!");
			getEnabledPlayers().put(player(), rbaPlayer);
		}
	}

	private boolean isLeatherArmour(Material material) {
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
		if (getEnabledPlayers().containsKey(player) && getEnabledPlayers().get(player).isEnabled()) {
			ItemStack item = event.getCurrentItem();
			if (event.getSlotType() == InventoryType.SlotType.ARMOR && isLeatherArmour(item.getType())) {
				RainbowArmourCommand.removeColor(item);
			}
		}
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		if (getEnabledPlayers().containsKey(player) && getEnabledPlayers().get(player).isEnabled()) {
			for (ItemStack itemStack : event.getDrops()) {
				if (isLeatherArmour(itemStack.getType())) {
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
		if (getEnabledPlayers().containsKey(player) && getEnabledPlayers().get(player).isEnabled())
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

	private ItemStack getColor(ItemStack itemStack, Color color) {
		LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
		meta.setColor(color);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	private void stopArmour(RainbowArmourPlayer rbaPlayer) {
		Bukkit.getScheduler().cancelTask(rbaPlayer.getTaskId());
		removeColor(rbaPlayer.getPlayer().getInventory());
	}

	private int startArmour(RainbowArmourPlayer rbaPlayer) {
		Player player = rbaPlayer.getPlayer();

		return Bukkit.getScheduler().scheduleSyncRepeatingTask(BNCore.getInstance(), () -> {
			int r = rbaPlayer.getR();
			int g = rbaPlayer.getG();
			int b = rbaPlayer.getB();

			if (r > 0 && b == 0) {
				if (r == 255 && g < 255) {
					g += rate;
				} else {
					r -= rate;
					g += rate;
				}
			}
			if (g > 0 && r == 0) {
				if (g == 255 && b < 255) {
					b += rate;
				} else {
					g -= rate;
					b += rate;
				}
			}
			if (b > 0 && g == 0) {
				if (b == 255 && r < 255) {
					r += rate;
				} else {
					b -= rate;
					r += rate;
				}
			}

			if (r < 0) r = 0;
			if (r > 255) r = 255;
			if (g < 0) g = 0;
			if (g > 255) g = 255;
			if (b < 0) b = 0;
			if (b > 255) b = 255;

//			if (r > 0 && b == 0) {
//				r -= rate;
//				if (r < 0)
//					r = 0;
//
//				g += rate;
//				if (g > 255)
//					g = 255;
//			}
//
//			if (g > 0 && r == 0) {
//				g -= rate;
//				if (g < 0)
//					g = 0;
//
//				b += rate;
//				if (b > 255)
//					b = 255;
//			}
//
//			if (b > 0 && g == 0) {
//				b -= rate;
//				if (b < 0)
//					b = 0;
//
//				r += rate;
//				if (r > 255)
//					r = 255;
//			}

			PlayerInventory inv = player.getInventory();
			ItemStack[] armour = inv.getArmorContents();
			Color color = Color.fromRGB(r, g, b);
			rbaPlayer.setRGB(color);

			int counter = 0;
			for (ItemStack itemStack : armour) {
				if (itemStack != null && itemStack.getType().toString().toLowerCase().startsWith("leather_")) {
					armour[counter] = getColor(itemStack, color);
				}
				counter++;
			}

			inv.setArmorContents(armour);
		}, 4, 2);
	}

	public static class RainbowArmourPlayer {
		private final Player player;
		private int taskId;
		private boolean enabled = false;
		@Getter
		@Setter
		private int r = 255;
		@Getter
		@Setter
		private int g, b = 0;

		public RainbowArmourPlayer(Player player, int taskId) {
			this.player = player;
			this.taskId = taskId;
			this.enabled = taskId > 0;
		}

		public Player getPlayer() {
			return player;
		}

		public int getTaskId() {
			return taskId;
		}

		public void setTaskID(int taskId) {
			this.taskId = taskId;
		}

		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public void setRGB(Color color) {
			r = color.getRed();
			g = color.getGreen();
			b = color.getBlue();
		}
	}
}
