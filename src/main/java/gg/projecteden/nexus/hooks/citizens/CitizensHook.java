package gg.projecteden.nexus.hooks.citizens;

import gg.projecteden.nexus.hooks.IHook;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class CitizensHook extends IHook<CitizensHook> {

	public NPCRegistry getRegistry() {
		return null;
	}

	public NPC getSelectedNPC(CommandSender sender) {
		return null;
	}

	public NPC createNPC(EntityType type, String name) {
		return null;
	}

	public NPC getNPC(int id) {
		return null;
	}

	public NPC getNPC(Entity entity) {
		return null;
	}

	public boolean isNPC(Entity entity) {
		return false;
	}

}
