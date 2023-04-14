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
import java.math.BigDecimal;
import java.text.DecimalFormat;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.Utils.getMaxValue;
import static gg.projecteden.nexus.utils.Utils.getMinValue;

@Data
public class RangeArgumentValidator implements AbstractArgumentValidator {
	protected Range annotation;

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Range {
		double min() default Short.MIN_VALUE;
		double max() default Short.MAX_VALUE;
		String bypass() default "";
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
		final VariableArgumentMeta argumentMeta = argumentMetaInstance.getArgumentMeta();
		final String input = argumentMetaInstance.getInput();

		double defaultMin = (Double) Range.class.getDeclaredMethod("min").getDefaultValue();
		double defaultMax = (Double) Range.class.getDeclaredMethod("max").getDefaultValue();

		Number classDefaultMin = getMinValue(argumentMeta.getType());
		Number classDefaultMax = getMaxValue(argumentMeta.getType());

		BigDecimal min = (annotation.min() != defaultMin ? BigDecimal.valueOf(annotation.min()) : new BigDecimal(classDefaultMin.toString()));
		BigDecimal max = (annotation.max() != defaultMax ? BigDecimal.valueOf(annotation.max()) : new BigDecimal(classDefaultMax.toString()));

		int maxComparison = new BigDecimal(input).compareTo(max);
		int minComparison = new BigDecimal(input).compareTo(min);

		if (minComparison >= 0 && maxComparison <= 0)
			return input;

		DecimalFormat formatter = StringUtils.getFormatter(argumentMeta.getType());

		boolean usingDefaultMin = defaultMin == annotation.min();
		boolean usingDefaultMax = defaultMax == annotation.max();

		String minFormatted = formatter.format(annotation.min());
		String maxFormatted = formatter.format(annotation.max());

		String error = camelCase(argumentMeta.getName()) + " must be ";
		if (usingDefaultMin && !usingDefaultMax)
			throw new InvalidInputException(error + "&e" + maxFormatted + " &cor less");
		else if (!usingDefaultMin && usingDefaultMax)
			throw new InvalidInputException(error + "&e" + minFormatted + " &cor greater");
		else
			throw new InvalidInputException(error + "between &e" + minFormatted + " &cand &e" + maxFormatted);
	}

}
