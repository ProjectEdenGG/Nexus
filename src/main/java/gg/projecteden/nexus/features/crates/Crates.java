package gg.projecteden.nexus.features.crates;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.crates.menus.CratePreviewProvider;
import gg.projecteden.nexus.features.customboundingboxes.events.CustomBoundingBoxEntityInteractEvent;
import gg.projecteden.nexus.features.menus.MenuUtils.ConfirmationMenu;
import gg.projecteden.nexus.features.survival.avontyre.AvontyreNPCs;
import gg.projecteden.nexus.framework.exceptions.NexusException;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CrateOpeningException;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateGroup;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateConfigService;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NoArgsConstructor;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.Distance.distance;

/*
	TODO
		Group settings
			Title
			Display Item
		Animations:
			Wither
			Mystery
				Models ?
 */

@NoArgsConstructor
public class Crates extends Feature implements Listener {

	public static final String PREFIX = StringUtils.getPrefix("Crates");

	public static List<CrateLoot> getLootByType(CrateType type) {
		if (type == null) return CrateConfigService.get().getLoot();
		return CrateConfigService.get().getLoot().stream().filter(loot -> loot.getType() == type).collect(Collectors.toList());
	}

	public static List<CrateGroup> getGroupsByType(CrateType type) {
		if (type == null) return CrateConfigService.get().getGroups();
		return CrateConfigService.get().getGroups().stream().filter(group -> group.getType() == type).collect(Collectors.toList());
	}

	@Override
	public void onStart() {
		Nexus.registerListener(new CratePinatas());
	}

	@EventHandler
	public void onClickWithKey(CustomBoundingBoxEntityInteractEvent event) {
		try {
			if (!(event.getEntity().getEntity() instanceof ArmorStand armorStand))
				return;

			CrateType crateType = CrateType.fromEntity(armorStand);
			if (crateType == null)
				return;
			event.setCancelled(true);

			if (distance(armorStand, event.getPlayer()).gte(7))
				return;

			if (!event.getHand().equals(EquipmentSlot.HAND)) return;

			if (!CrateConfigService.get().isEnabled())
				throw new CrateOpeningException("Crates are temporarily disabled");
			if (!crateType.isEnabled())
				throw new CrateOpeningException("&3Coming soon!");
			if (Nexus.isMaintenanceQueued())
				throw new CrateOpeningException("Server maintenance is queued, cannot open crates");

			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			CrateType keyType = CrateType.fromKey(item);
			if (crateType != keyType) {
				if (Crates.getLootByType(crateType).stream().noneMatch(CrateLoot::isActive) && crateType != CrateType.MINIGAMES)
					throw new CrateOpeningException("&3Coming soon!");
				else
					new CratePreviewProvider(crateType, null, armorStand).open(event.getPlayer());
			}
			else {
				try {
					int amount = item.getAmount();
					if (amount > 1 && event.getPlayer().isSneaking())
						ConfirmationMenu.builder()
							.title("Open " + amount + " keys?")
							.onConfirm(e -> CrateHandler.openCrate(keyType, armorStand, event.getPlayer(), amount))
							.open(event.getPlayer());
					else
						CrateHandler.openCrate(keyType, armorStand, event.getPlayer(), 1);
				} catch (CrateOpeningException ex) {
					if (ex.getMessage() != null)
						PlayerUtils.send(event.getPlayer(), Crates.PREFIX + ex.getMessage());
					CrateHandler.reset(armorStand);
				}
			}
		} catch (NexusException ex) {
			PlayerUtils.send(event.getPlayer(), ex.withPrefix(Crates.PREFIX));
		}
	}

	@EventHandler
	public void on(NPCRightClickEvent event) {
		if (!AvontyreNPCs.CRATES__BLAST.is(event.getNPC()))
			return;

		PlayerUtils.runCommand(event.getClicker(), "crates info");
	}

}
