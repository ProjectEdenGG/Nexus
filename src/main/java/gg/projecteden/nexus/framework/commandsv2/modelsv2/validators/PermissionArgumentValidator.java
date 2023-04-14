package gg.projecteden.nexus.framework.commandsv2.modelsv2.validators;

import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common.AbstractArgumentValidator;
import lombok.Data;
import org.bukkit.command.CommandSender;

@Data
public class PermissionArgumentValidator implements AbstractArgumentValidator {
	protected Permission annotation;

	@Override
	public String validate(CustomCommandMetaInstance.PathMetaInstance.VariableArgumentMetaInstance argumentMetaInstance) {
		final CommandSender sender = argumentMetaInstance.getEvent().getSender();

		if (sender.hasPermission(annotation.value()))
			return argumentMetaInstance.getInput();

		return argumentMetaInstance.getArgumentMeta().getDefaultValue();
	}

}
