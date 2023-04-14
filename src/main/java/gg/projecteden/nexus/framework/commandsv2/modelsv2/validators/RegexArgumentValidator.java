package gg.projecteden.nexus.framework.commandsv2.modelsv2.validators;

import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.VariableArgumentMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common.AbstractArgumentValidator;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.regex.Pattern;

@Data
public class RegexArgumentValidator implements AbstractArgumentValidator {
	protected Regex annotation;

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Regex {
		String value();
	}

	@Override
	public String validate(VariableArgumentMetaInstance argumentMetaInstance) {
		final String input = argumentMetaInstance.getInput();

		if (annotation.value() == null)
			return input;

		if (Pattern.compile(annotation.value()).matcher(input).matches())
			return input;

		throw new InvalidInputException("Argument must match regex &e" + annotation.value());
	}

}
