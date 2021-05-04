package me.pugabyte.nexus.features.crates.crates;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.crates.Crates;
import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.features.crates.models.events.CrateSpawnItemEvent;
import me.pugabyte.nexus.utils.ColorType;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.RandomUtils;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class MysteryCrate extends Crate {

	@Override
	public CrateType getCrateType() {
		return CrateType.MYSTERY;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&3&l--=[+]=--");
			add("&3[+] &e&lMystery Crate &3[+]");
			add("&3&l--=[+]=--");
		}};
	}

	@Override
	public void giveItems() {
		if (loot.getItems().size() > 9) {
			Material material = RandomUtils.randomMaterial(MaterialTag.SHULKER_BOXES);
			ItemStack shulker = new ItemBuilder(material).name(loot.getTitle())
					.shulkerBox(loot.getItems().toArray(new ItemStack[0]))
					.build();
			PlayerUtils.giveItem(player, shulker);
		} else super.giveItems();
	}

	@EventHandler
	public void onSpawnItem(CrateSpawnItemEvent event) {
		if (event.getCrateType() != getCrateType()) return;
		if (!event.getCrateLoot().getItems().contains(new ItemStack(Material.BEACON))) return;
		String message = "&e" + event.getPlayer().getName() + " &3has received a &eBeacon &3from the &eMystery Crate";
		Chat.broadcastIngame(Crates.PREFIX + message, MuteMenuItem.CRATES);
		Chat.broadcastDiscord("**[Crates]** " + message);
	}

	@Override
	public boolean canHoldItems(Player player) {
		if (loot.getItems().size() >= 9) {
			if (!PlayerUtils.hasRoomFor(player, new ItemStack(Material.SHULKER_BOX))) {
				PlayerUtils.send(player, Crates.PREFIX + "You must clear room in your inventory before you can open crates");
				reset();
				return false;
			}
			return true;
		} else return super.canHoldItems(player);
	}

	@Override
	public Color[] getBandColors() {
		return new Color[]{ColorType.CYAN.getBukkitColor(), Color.YELLOW};
	}
}
