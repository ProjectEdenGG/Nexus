package me.pugabyte.bncore.utils;

import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;

import static me.pugabyte.bncore.utils.Utils.colorize;

public class CitizensUtils {
	public static void updateNameAndSkin(NPC npc, String name) {
		npc.setName(name);
		updateSkin(npc, name);
	}

	public static void updateSkin(NPC npc, String name) {
		updateSkin(npc, name, false);
	}

	public static void updateSkin(NPC npc, String name, boolean useLatest) {
		npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, ChatColor.stripColor(colorize(name)));
		npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, useLatest);

		Entity npcEntity = npc.getEntity();
		if (npcEntity instanceof SkinnableEntity) {
			((SkinnableEntity) npcEntity).getSkinTracker().notifySkinChange(npc.data().get(NPC.PLAYER_SKIN_USE_LATEST));
		}
	}

}
