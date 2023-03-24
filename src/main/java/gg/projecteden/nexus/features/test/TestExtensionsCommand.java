package gg.projecteden.nexus.features.test;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.Extensions;
import lombok.NonNull;
import lombok.experimental.ExtensionMethod;
import org.bukkit.entity.EntityType;

@Permission(Group.ADMIN)
@ExtensionMethod(Extensions.class)
public class TestExtensionsCommand extends CustomCommand {

	public TestExtensionsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path
	@Description("Test lombok extensions")
	void run() {
		send("isVanished: " + player().isVanished());
		send("isAFK: " + player().isAFK());
		send("getRank: " + player().getRank());
		send("isStaff: " + player().isStaff());
		send("isSeniorStaff: " + player().isSeniorStaff());

		send("camelCase string: " + "TEST".camelCase());
		send("camelCase enum: " + EntityType.ZOMBIE_VILLAGER.camelCase());
		String nullString = null;
		String emptyString = "";
		send("isNullOrEmpty null: " + nullString.isNullOrEmpty());
		send("isNullOrEmpty empty: " + emptyString.isNullOrEmpty());
	}

}
