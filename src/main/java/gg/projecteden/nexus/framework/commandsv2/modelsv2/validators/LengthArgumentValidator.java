package gg.projecteden.nexus.framework.commandsv2.modelsv2.validators;

import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.VariableArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.VariableArgumentMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common.AbstractArgumentValidator;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.SneakyThrows;
import org.bukkit.command.CommandSender;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.text.DecimalFormat;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.nexus.utils.Extensions.camelCase;

@Data
public class LengthArgumentValidator implements AbstractArgumentValidator {
	protected Length annotation;

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Length {
		double min();
		double max();
		String bypass();
	}

	@Override
	public boolean canBypass(CommandSender sender) {
		if (isNullOrEmpty(annotation.bypass()))
			return AbstractArgumentValidator.super.canBypass(sender);

		return sender.hasPermission(annotation.bypass());
	}

	@Override
	@SneakyThrows
	public String validate(VariableArgumentMetaInstance argumentMetaInstance) {
		final VariableArgumentMeta meta = argumentMetaInstance.getArgumentMeta();
		final String input = argumentMetaInstance.getInput();

		if (isNullOrEmpty(input))
			return input;

		if (!(input.length() < annotation.min()) && !(input.length() > annotation.max()))
			return input;

		DecimalFormat formatter = StringUtils.getFormatter(meta.getType());
		String min = formatter.format(annotation.min());
		String max = formatter.format(annotation.max());
		double minDefault = (Double) Length.class.getDeclaredMethod("min").getDefaultValue();
		double maxDefault = (Double) Length.class.getDeclaredMethod("max").getDefaultValue();

		String error = camelCase(meta.getName()) + " length must be ";
		if (annotation.min() == minDefault && annotation.max() != maxDefault)
			throw new InvalidInputException(error + "&e" + max + " &ccharacters or shorter");
		else if (annotation.min() != minDefault && annotation.max() == maxDefault)
			throw new InvalidInputException(error + "&e" + min + " &ccharacters or longer");
		else
			throw new InvalidInputException(error + "between &e" + min + " &cand &e" + max + " &ccharacters");
	}

}
