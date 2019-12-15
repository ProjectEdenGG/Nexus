package me.pugabyte.bncore.features.rainbowarmour;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.rainbowarmour.models.RainbowArmourPlayer;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

@Aliases({"rainbowarmor", "rba"})
@Permission("rainbow.armour")
public class RainbowArmourCommand extends CustomCommand {
	private int rate = 9;
	private int id = 0;

	RainbowArmourCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void toggle() {
		RainbowArmourPlayer rbaPlayer = BNCore.rainbowArmour.getPlayer(player());
		if (rbaPlayer.isEnabled()) {
			stopArmour(rbaPlayer);
			reply("§cRainbow armour unequipped!");
			rbaPlayer.setEnabled(false);
		} else {
			rbaPlayer.setTaskID(startArmour(rbaPlayer));
			rbaPlayer.setEnabled(true);
			reply("§cR§6a§ei§an§bb§5o§dw §earmour equipped!");
			BNCore.rainbowArmour.enabledPlayers.put(player(), rbaPlayer);
		}
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
