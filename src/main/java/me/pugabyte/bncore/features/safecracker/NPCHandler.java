package me.pugabyte.bncore.features.safecracker;

import me.pugabyte.bncore.models.safecracker.SafeCrackerEvent;
import me.pugabyte.bncore.models.safecracker.SafeCrackerEventService;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayer;
import me.pugabyte.bncore.models.safecracker.SafeCrackerPlayerService;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;

public class NPCHandler implements Listener {

	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		SafeCrackerEventService eventService = new SafeCrackerEventService();
		SafeCrackerEvent.SafeCrackerGame safeCrackerEvent = eventService.getActiveEvent();
		safeCrackerEvent.getNpcs().values().forEach(npc -> {
			if (npc.getId() != event.getNPC().getId()) return;
			if (((SafeCrackerPlayer) new SafeCrackerPlayerService().get(event.getClicker())).getGames() == null ||
					!((SafeCrackerPlayer) new SafeCrackerPlayerService().get(event.getClicker())).getGames().containsKey(safeCrackerEvent.getName())) {
				event.getClicker().sendMessage(StringUtils.colorize(StringUtils.getPrefix("SafeCracker") + "You must start the event by doing &c/safecracker start"));
				return;
			}
			event.getClicker().sendMessage(StringUtils.colorize(SafeCracker.PREFIX + npc.getQuestion()));
			event.getClicker().sendMessage(new JsonBuilder("&eClick here to answer").suggest("/safecracker answer ").build());
			SafeCracker.playerClickedNPC.put(event.getClicker(), npc.getName());
			SafeCrackerPlayerService playerService = new SafeCrackerPlayerService();
			SafeCrackerPlayer player = playerService.get(event.getClicker());
			SafeCrackerPlayer.Game game = playerService.getActiveGame(event.getClicker().getUniqueId());
			if (!game.getNpcs().containsKey(npc.getName())) {
				game.getNpcs().put(npc.getName(), new SafeCrackerPlayer.SafeCrackerPlayerNPC(npc.getId(), npc.getName(), LocalDateTime.now(), null, false));
				playerService.save(player);
			}
		});
	}

	public static int createNPC(String name, Location loc) {
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, StringUtils.colorize("&3SafeCracker: &e" + name));
		npc.data().setPersistent(NPC.PLAYER_SKIN_UUID_METADATA, name);
		npc.data().setPersistent(NPC.PLAYER_SKIN_USE_LATEST, false);

		Entity npcEntity = npc.getEntity();
		if (npcEntity instanceof SkinnableEntity) {
			((SkinnableEntity) npcEntity).getSkinTracker().notifySkinChange(npc.data().get(NPC.PLAYER_SKIN_USE_LATEST));
		}

		npc.spawn(loc);
		npc.despawn(DespawnReason.PLUGIN);

		return npc.getId();
	}

}
