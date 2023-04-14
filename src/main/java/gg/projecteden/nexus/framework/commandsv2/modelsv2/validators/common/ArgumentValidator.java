package gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common;

import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.LengthArgumentValidator;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.RangeArgumentValidator;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.RegexArgumentValidator;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.StripColorArgumentValidator;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.StripColorArgumentValidator.StripColor;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.RangeArgumentValidator.Range;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.RegexArgumentValidator.Regex;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.checkerframework.checker.units.qual.Length;

import java.lang.annotation.Annotation;

@Getter
@AllArgsConstructor
public enum ArgumentValidator {
	REGEX(RegexArgumentValidator.class, Regex.class),
	LENGTH(LengthArgumentValidator.class, Length.class),
	RANGE(RangeArgumentValidator.class, Range.class),
	STRIP_COLOR(StripColorArgumentValidator.class, StripColor.class),
	;

	private final Class<? extends AbstractArgumentValidator> validatorClass;
	private final Class<? extends Annotation> annotationClass;
}
