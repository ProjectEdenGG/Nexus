package gg.projecteden.nexus.features.test;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
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

	@NoLiterals
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
