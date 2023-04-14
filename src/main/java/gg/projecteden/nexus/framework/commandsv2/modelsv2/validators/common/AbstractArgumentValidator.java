package gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common;

import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.VariableArgumentMetaInstance;
import org.bukkit.command.CommandSender;

public interface AbstractArgumentValidator {

	String validate(VariableArgumentMetaInstance argumentMetaInstance);

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	default boolean canBypass(CommandSender sender) {
		return false;
	}


}
