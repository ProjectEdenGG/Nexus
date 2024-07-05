package gg.projecteden.nexus.features.survival.avontyre;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu;
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

		NPCShopMenu.builder()
			.title("Hunter Shop")
			.npcId(AvontyreNPCs.HUNTER__NULL.getNPCId())
			.products(List.of(
					NPCShopMenu.Product.builder()
					.itemStack(CustomMaterial.MOB_NET.getNamedItem())
							.price(5000.0).build())
			)
			.open(event.getClicker());
	}

}
