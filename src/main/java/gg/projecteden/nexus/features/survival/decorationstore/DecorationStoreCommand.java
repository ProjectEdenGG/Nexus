package gg.projecteden.nexus.features.survival.decorationstore;

import com.mojang.datafixers.util.Pair;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.features.survival.decorationstore.models.BuyableData;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.List;

@Permission(Group.ADMIN)
public class DecorationStoreCommand extends CustomCommand {

	public DecorationStoreCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("warp")
	void warp() {
		runCommand("sw decorstore");
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

	@Path("debug [enabled]")
	void setDebug(Boolean enabled) {
		if (enabled == null)
			enabled = !DecorationStore.getDebuggers().contains(player());

		if (enabled)
			DecorationStore.getDebuggers().add(player());
		else
			DecorationStore.getDebuggers().remove(player());

		send(PREFIX + "Debug " + (enabled ? "&aEnabled" : "&cDisabled"));

	}

	@Path("getTargetBuyable")
	void targetBuyable() {
		BuyableData buyable = DecorationStore.getTargetBuyable(player());
		if (buyable == null)
			error("That decoration is not buyable");

		Pair<String, Integer> namePrice = buyable.getNameAndPrice();
		if (namePrice == null)
			error("That decoration is not buyable");

		send("&3Buy &e" + namePrice.getFirst() + " &3- &a$" + namePrice.getSecond());
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
		pasteLayout(DecorationStoreUtils.getRandomLayoutPath(DecorationStore.getConfig().getCurrentSchematic()));
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
		DecorationStore.getConfig().setCurrentSchematic(schematicFile);
		DecorationStore.saveConfig();

		DecorationStore.refresh();
	}
}
