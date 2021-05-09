package me.pugabyte.nexus.features.crates.crates;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.features.crates.Crates;
import me.pugabyte.nexus.features.crates.models.Crate;
import me.pugabyte.nexus.features.crates.models.CrateLoot;
import me.pugabyte.nexus.features.crates.models.CrateType;
import me.pugabyte.nexus.features.crates.models.events.CrateSpawnItemEvent;
import org.bukkit.Color;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
public class VoteCrate extends Crate {

	@Override
	public CrateType getCrateType() {
		return CrateType.VOTE;
	}

	@Override
	public List<String> getCrateHologramLines() {
		return new ArrayList<>() {{
			add("&3&l--=[+]=--");
			add("&3[+] &e&lVote Crate &3[+]");
			add("&3&l--=[+]=--");
		}};
	}

	@Override
	public Color[] getBandColors() {
		return new Color[]{Color.WHITE, Color.WHITE};
	}

	@EventHandler
	public void onItemSpawn(CrateSpawnItemEvent event) {
		CrateLoot loot = event.getCrateLoot();
		if (loot.getItems().contains(CrateType.MYSTERY.getKey())) {
			String message = "&e" + event.getPlayer().getName() + " &3has received a &eMystery Crate Key &3from the &eVote Crate";
			Chat.broadcastIngame(Crates.PREFIX + message, MuteMenuItem.CRATES);
			Chat.broadcastDiscord("**[Crates]** " + message);
		}
	}
}
