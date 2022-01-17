package gg.projecteden.nexus.features.crates.crates;

import gg.projecteden.nexus.features.chat.Chat.Broadcast;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.crates.Crates;
import gg.projecteden.nexus.features.crates.models.Crate;
import gg.projecteden.nexus.features.crates.models.CrateType;
import gg.projecteden.nexus.features.crates.models.events.CrateSpawnItemEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.NoArgsConstructor;
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
					.shulkerBox(loot.getItems().toArray(ItemStack[]::new))
					.build();
			PlayerUtils.giveItem(player, shulker);
		} else super.giveItems();
	}

	@EventHandler
	public void onSpawnItem(CrateSpawnItemEvent event) {
		if (event.getCrateType() != getCrateType()) return;
		if (!event.getCrateLoot().getItems().contains(new ItemStack(Material.BEACON))) return;
		String message = "&e" + Nickname.of(event.getPlayer()) + " &3has received a &eBeacon &3from the &eMystery Crate";
		Broadcast.all().prefix("Crates").message(message).muteMenuItem(MuteMenuItem.CRATES).send();
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
