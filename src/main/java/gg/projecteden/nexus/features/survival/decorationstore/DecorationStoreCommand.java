package gg.projecteden.nexus.features.survival.decorationstore;

import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfigService;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.List;

@Permission(Group.ADMIN)
public class DecorationStoreCommand extends CustomCommand {
	DecorationStoreConfigService storeConfigService = new DecorationStoreConfigService();
	DecorationStoreConfig storeConfig;

	public DecorationStoreCommand(@NonNull CommandEvent event) {
		super(event);

		if (isPlayerCommandEvent()) {
			storeConfig = storeConfigService.get();
		}
	}

	@Path("refresh")
	void refresh() {
		DecorationStore.refresh();
		send("Refreshed decoration store");
	}

	@Path("setActive <bool>")
	void setActive(boolean bool) {
		DecorationStore.setActive(bool);
		send("Set decoration store to " + (bool ? "&aActive" : "&cInactive"));
	}

	@Path("getTargetBuyable")
	void targetBuyable() {
		ItemStack item = DecorationStore.getTargetedBuyable(player());
		if (Nullables.isNullOrAir(item))
			error("Player does not have a target buyable");

		send(StringUtils.pretty(item));
	}

	@Path("layout list")
	void listLayouts() {
		// get schematic
		File[] files = DecorationStoreUtils.getLayoutFiles();
		send("Layouts: " + files.length);
		for (File file : files) {
			send(" - " + DecorationStoreUtils.getSchematicPath(file));
		}
	}

	@Path("layout paste <id>")
	void pasteLayout(int id) {
		pasteLayout(DecorationStoreUtils.getLayoutPath(id));
	}

	@Path("layout pasteRandom")
	void pasteRandomLayout() {
		pasteLayout(DecorationStoreUtils.getRandomLayoutPath(storeConfig.getCurrentSchematic()));
	}

	private void pasteLayout(String schematicFile) {
		DecorationStore.setActive(false);

		// delete entities in region
		// TODO: Interaction/Display entities
		List<EntityType> decoration = List.of(EntityType.ITEM_FRAME, EntityType.ARMOR_STAND, EntityType.PAINTING, EntityType.GLOW_ITEM_FRAME);
		for (Entity entity : worldguard().getEntitiesInRegion(DecorationStoreUtils.getSchematicStoreRegion())) {
			if (decoration.contains(entity.getType())) {
				entity.remove();
			}
		}

		// paste new schem with entities
		worldedit().paster()
			.file(schematicFile)
			.entities(true)
			.at(new Location(Survival.getWorld(), 362, 64, 15))
			.pasteAsync();

		// save data
		storeConfig.setCurrentSchematic(schematicFile);
		storeConfigService.save(storeConfig);

		DecorationStore.refresh();
	}
}
