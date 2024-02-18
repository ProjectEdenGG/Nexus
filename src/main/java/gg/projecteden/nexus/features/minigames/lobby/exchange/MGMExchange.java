package gg.projecteden.nexus.features.minigames.lobby.exchange;

import gg.projecteden.parchment.HasPlayer;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MGMExchange implements Listener {

	static final int NPC_ID = 4994;

	@EventHandler
	public void onClickNPC(NPCRightClickEvent event) {
		if (event.getNPC().getId() != NPC_ID)
			return;

		open(event.getClicker());
	}

	public static void open(HasPlayer player) {
		new MGMExchangeMenu().open(player);
	}

}
