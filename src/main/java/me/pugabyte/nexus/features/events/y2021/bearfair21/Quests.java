package me.pugabyte.nexus.features.events.y2021.bearfair21;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.models.BearFairTalker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.SellCrates;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.farming.RegenCrops;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.fishing.Fishing;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.Merchants;
import me.pugabyte.nexus.features.recipes.functionals.Backpacks;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isAtBearFair;

public class Quests implements Listener {
	public Quests() {
		Nexus.registerListener(this);
		new Fishing();
		new RegenCrops();
		new SellCrates();
	}

	public static void shutdown() {
		RegenCrops.shutdown();
	}

	public static ItemStack getBackPack(Player player) {
		return Backpacks.getBackpack(null, player);
	}

	public static void chime(Player player) {
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
	}

	// NPC Stuff
	@EventHandler
	public void onRightClickNPC(NPCRightClickEvent event) {
		Player player = event.getClicker();
		if (isAtBearFair(player)) {
			CooldownService cooldownService = new CooldownService();
			if (!cooldownService.check(player, "BF21_NPCInteract", Time.SECOND.x(2)))
				return;

			int id = event.getNPC().getId();
			BearFairTalker.startScript(player, id);
			Merchants.openMerchant(player, id);
		}
	}
}
