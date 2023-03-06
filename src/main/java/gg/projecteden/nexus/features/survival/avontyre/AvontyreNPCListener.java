package gg.projecteden.nexus.features.survival.avontyre;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.SurvivalNPCShopMenu;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Map;

// TODO: JOBS - Temporary listener until jobs are complete
public class AvontyreNPCListener implements Listener {

	public AvontyreNPCListener() {
		Nexus.registerListener(this);
	}

	@EventHandler
	public void on(NPCRightClickEvent event) {
		if (!AvontyreNPCs.HUNTER__NULL.is(event.getNPC()))
			return;

		SurvivalNPCShopMenu.builder()
			.title("Hunter Shop")
			.npcId(AvontyreNPCs.HUNTER__NULL.getNPCId())
			.products(Map.of(CustomMaterial.MOB_NET.getNamedItem(), 5000d))
			.open(event.getClicker());
	}

}
