package me.pugabyte.bncore.features.rainbowarmour;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.rainbowarmour.models.RainbowArmourPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

public class RainbowArmourCommand implements CommandExecutor {
	private int rate = 9;
	private int id = 0;

	RainbowArmourCommand() {
		BNCore.registerCommand("rainbowarmour", this);
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

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (sender instanceof Player) {
			Player player = (Player) sender;

			if (sender.hasPermission("rainbowarmour.use")) {
				RainbowArmourPlayer rbaPlayer = BNCore.rainbowArmour.getPlayer(player);

				if (rbaPlayer.isEnabled()) {
					stopArmour(rbaPlayer);
					player.sendMessage("§cRainbow armour unequipped!");

					rbaPlayer.setEnabled(false);
					return true;
				} else {
					id = startArmour(rbaPlayer);
					player.sendMessage("§cR§6a§ei§an§bb§5o§dw §earmour equipped!");

					rbaPlayer.setTaskID(id);
					rbaPlayer.setEnabled(true);
					BNCore.rainbowArmour.enabledPlayers.put(player, rbaPlayer);
					return true;
				}
			} else {
				player.sendMessage("§cYou do not have permission to use this command.");
				return false;
			}
		} else {
			sender.sendMessage("You must be a player to execute this command.");
			return false;
		}
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

		int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(BNCore.getInstance(), () -> {
			int r = rbaPlayer.getR();
			int g = rbaPlayer.getG();
			int b = rbaPlayer.getB();

			if (r > 0 && b == 0) {
				r -= rate;
				if (r < 0)
					r = 0;

				g += rate;
				if (g > 255)
					g = 255;
			}

			if (g > 0 && r == 0) {
				g -= rate;
				if (g < 0)
					g = 0;

				b += rate;
				if (b > 255)
					b = 255;
			}

			if (b > 0 && g == 0) {
				b -= rate;
				if (b < 0)
					b = 0;

				r += rate;
				if (r > 255)
					r = 255;
			}

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

		return taskId;
	}
}
