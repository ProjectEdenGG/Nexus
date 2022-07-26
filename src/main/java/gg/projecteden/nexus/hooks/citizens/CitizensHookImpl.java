package gg.projecteden.nexus.hooks.citizens;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class CitizensHookImpl extends CitizensHook {

	@Override
	public NPCRegistry getRegistry() {
		return CitizensAPI.getNPCRegistry();
	}

	@Override
	public NPC getSelectedNPC(CommandSender sender) {
		return CitizensAPI.getDefaultNPCSelector().getSelected(sender);
	}

	@Override
	public NPC createNPC(EntityType type, String name) {
		return CitizensAPI.getNPCRegistry().createNPC(type, name);
	}

	@Override
	public NPC getNPC(int id) {
		return CitizensAPI.getNPCRegistry().getById(id);
	}

	@Override
	public NPC getNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().getNPC(entity);
	}

	@Override
	public boolean isNPC(Entity entity) {
		return CitizensAPI.getNPCRegistry().isNPC(entity);
	}

}
