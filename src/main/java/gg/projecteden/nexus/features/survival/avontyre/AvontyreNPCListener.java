package gg.projecteden.nexus.features.survival.avontyre;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.SurvivalNPCShopMenu;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

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
			.products(List.of(
				SurvivalNPCShopMenu.Product.builder()
					.itemStack(CustomMaterial.MOB_NET.getNamedItem())
					.price(5000).build())
			)
			.open(event.getClicker());
	}

}
