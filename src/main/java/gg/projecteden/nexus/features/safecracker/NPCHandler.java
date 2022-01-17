package gg.projecteden.nexus.features.safecracker;

import gg.projecteden.annotations.Disabled;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEvent;
import gg.projecteden.nexus.models.safecracker.SafeCrackerEventService;
import gg.projecteden.nexus.models.safecracker.SafeCrackerPlayer;
import gg.projecteden.nexus.models.safecracker.SafeCrackerPlayerService;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.DespawnReason;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.LocalDateTime;

@Disabled
public class NPCHandler implements Listener {

	@EventHandler
	public void onNPCClick(NPCRightClickEvent event) {
		SafeCrackerEventService eventService = new SafeCrackerEventService();
		SafeCrackerEvent.SafeCrackerGame safeCrackerEvent = eventService.getActiveEvent();
		safeCrackerEvent.getNpcs().values().forEach(npc -> {
			if (npc.getId() != event.getNPC().getId()) return;

			SafeCrackerPlayerService playerService = new SafeCrackerPlayerService();
			SafeCrackerPlayer player = playerService.get(event.getClicker());

			if (player.getGames() == null || !player.getGames().containsKey(safeCrackerEvent.getName())) {
				player.sendMessage(SafeCracker.PREFIX + "You must start the event by doing &c/safecracker start");
				return;
			}

			SafeCrackerPlayer.Game game = player.getActiveGame();

			if (game.isFinished()) {
				player.sendMessage(SafeCracker.PREFIX + "&cYou have already correctly solved the riddle and finished the game");
				return;
			}

			if (game.getNpcs().containsKey(npc.getName()) && game.getNpcs().get(npc.getName()).isCorrect()) {
				player.sendMessage(SafeCracker.PREFIX + "You have already solved my question, look for some other NPCs!");
				return;
			}

			player.sendMessage(SafeCracker.PREFIX + npc.getQuestion());
			player.sendMessage(new JsonBuilder("&f[&aClick to answer&f]").suggest("/safecracker answer "));

			SafeCracker.playerClickedNPC.put(event.getClicker(), npc.getName());
			if (!game.getNpcs().containsKey(npc.getName())) {
				game.getNpcs().put(npc.getName(), new SafeCrackerPlayer.SafeCrackerPlayerNPC(npc.getId(), npc.getName(), LocalDateTime.now(), null, false));
				playerService.save(player);
			}
		});
	}

	public static int createNPC(String name, Location loc) {
		NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, StringUtils.colorize("&3SafeCracker: &e" + name));
		CitizensUtils.updateSkin(npc, name);

		npc.spawn(loc);
		npc.despawn(DespawnReason.PLUGIN);

		return npc.getId();
	}

}
