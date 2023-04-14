package gg.projecteden.nexus.framework.commandsv2.modelsv2.validators;

import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.VariableArgumentMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common.AbstractArgumentValidator;
import lombok.Data;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static gg.projecteden.nexus.utils.StringUtils.stripColor;

@Data
public class StripColorArgumentValidator implements AbstractArgumentValidator {
	protected StripColor annotation;

	@Target(ElementType.PARAMETER)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface StripColor {}

	@Override
	public String validate(VariableArgumentMetaInstance argumentMetaInstance) {
		return stripColor(argumentMetaInstance.getInput());
	}

}
