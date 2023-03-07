package gg.projecteden.nexus.features.survival.decorationstore;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.decorationstore.DecorationStoreConfig;
import lombok.NonNull;

import java.io.File;
import java.util.List;

@Permission(Group.ADMIN)
public class DecorationStoreCommand extends CustomCommand {

	public DecorationStoreCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("warp")
	void warp() {
		player().teleportAsync(DecorationStore.getWarpLocation());
	}

	@Path("setActive <bool>")
	void setActive(boolean bool) {
		DecorationStore.setActive(bool);
		send(PREFIX + (bool ? "&aActivated" : "&cDeactivated"));
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

	@Path("layout list")
	void listLayouts() {
		List<File> files = DecorationStoreLayouts.getLayoutFiles();
		send(PREFIX + "Total layouts: " + files.size());
		for (File file : files) {
			send(" - " + DecorationStoreLayouts.getSchematicPath(file));
		}
	}

	@Path("layout schem <name>")
	void schemLayout(String name) {
		worldedit().getPlayerSelection(player());
		String schemName = DecorationStoreLayouts.getDirectory() + name;
		runCommandAsOp("worldeditutils schem save " + schemName + " true");
	}

	@Path("layout paste <id>")
	void pasteLayout(int id) {
		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getLayoutSchematic(id));
		DecorationStoreConfig config = DecorationStore.getConfig();
		config.setSchematicId(id);
		DecorationStore.saveConfig();
	}

	@Path("layout paste reset")
	void pasteLayout() {
		DecorationStoreLayouts.pasteLayout(DecorationStoreLayouts.getReset_schematic());
	}

	@Path("layout paste next")
	void nextLayout() {
		DecorationStoreLayouts.pasteNextLayout();
	}
}
