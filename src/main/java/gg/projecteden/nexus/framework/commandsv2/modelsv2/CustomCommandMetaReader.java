package gg.projecteden.nexus.framework.commandsv2.modelsv2;

import gg.projecteden.api.common.annotations.Async;
import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commandsv2.Commands;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Aliases;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Fallback;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.ErasureType;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.Confirm;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Context;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Cooldown;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.DescriptionExtra;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.HideFromHelp;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.HideFromWiki;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.NoLiterals;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.StripColorArgumentValidator.StripColor;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Switch;
import gg.projecteden.nexus.framework.commandsv2.annotations.path.TabCompleteIgnore;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.TabCompleter;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Vararg;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common.ArgumentValidator;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.lang.ArrayUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.ReflectionUtils.methodsAnnotatedWith;

/* TODO
	- Validation
		- Look for methods annotated with other annotations but not @Description
		- Cannot be vararg & switch at same time

 */

@Data
public class CustomCommandMetaReader {
	private final Class<? extends CustomCommand> clazz;
	final CustomCommandMeta meta = new CustomCommandMeta();

	public CustomCommandMeta read() {
		meta.setName(Commands.prettyName(clazz));
		meta.setClazz(clazz);
		meta.setInstance(Nexus.singletonOf(clazz));
		meta.setAsync(clazz.isAnnotationPresent(Async.class));
		meta.setHideFromWiki(clazz.isAnnotationPresent(HideFromWiki.class));

		meta.setDoubleSlash(clazz.isAnnotationPresent(DoubleSlash.class));
		if (meta.isDoubleSlash()) {
			meta.setName("/" + meta.getName());
			meta.setAliases(meta.getAliases().stream().map(alias -> "/" + alias).toList());
		}

		if (clazz.isAnnotationPresent(Aliases.class))
			meta.setAliases(List.of(clazz.getAnnotation(Aliases.class).value()));
		if (clazz.isAnnotationPresent(Permission.class))
			meta.setPermission(clazz.getAnnotation(Permission.class).value());
		if (clazz.isAnnotationPresent(Description.class))
			meta.setDescription(clazz.getAnnotation(Description.class).value());
		if (clazz.isAnnotationPresent(DescriptionExtra.class))
			meta.setDescriptionExtra(clazz.getAnnotation(DescriptionExtra.class).value());
		if (clazz.isAnnotationPresent(WikiConfig.class))
			meta.setWikiConfig(clazz.getAnnotation(WikiConfig.class));
		if (clazz.isAnnotationPresent(Fallback.class))
			meta.setFallback(clazz.getAnnotation(Fallback.class));
		if (clazz.isAnnotationPresent(Cooldown.class))
			meta.setCooldown(clazz.getAnnotation(Cooldown.class));

		meta.setPaths(readPaths(clazz));

		return meta;
	}

	private List<CustomCommandMeta.PathMeta> readPaths(Class<? extends CustomCommand> clazz) {
		List<CustomCommandMeta.PathMeta> paths = new ArrayList<>();

		for (Method method : getPathMethods(clazz)) {
			if (method.isAnnotationPresent(Disabled.class))
				continue;

			if (method.isAnnotationPresent(Environments.class)) {
				if (!ArrayUtils.contains(method.getAnnotation(Environments.class).value(), Nexus.getEnv()))
					continue;
			}

			final CustomCommandMeta.PathMeta pathMeta = meta.new PathMeta();

			pathMeta.setMethod(method);
			pathMeta.setHideFromHelp(method.isAnnotationPresent(HideFromHelp.class));
			pathMeta.setHideFromWiki(method.isAnnotationPresent(HideFromWiki.class));

			if (method.isAnnotationPresent(TabCompleteIgnore.class)) {
				pathMeta.setIgnoreTabComplete(true);
				pathMeta.setIgnoreTabCompleteBypass(method.getAnnotation(TabCompleteIgnore.class).bypass());
			}

			pathMeta.setConfirmMenu(method.isAnnotationPresent(Confirm.class));
			if (method.isAnnotationPresent(Permission.class))
				pathMeta.setPermission(method.getAnnotation(Permission.class).value());
			if (method.isAnnotationPresent(Description.class))
				pathMeta.setDescription(method.getAnnotation(Description.class).value());
			if (method.isAnnotationPresent(DescriptionExtra.class))
				pathMeta.setDescriptionExtra(method.getAnnotation(DescriptionExtra.class).value());
			if (method.isAnnotationPresent(Cooldown.class))
				pathMeta.setCooldown(method.getAnnotation(Cooldown.class));

			pathMeta.setArguments(readArguments(pathMeta, method));
		}

		return paths;
	}

	@SneakyThrows
	private List<CustomCommandMeta.PathMeta.ArgumentMeta> readArguments(PathMeta pathMeta, Method method) {
		List<CustomCommandMeta.PathMeta.ArgumentMeta> arguments = new ArrayList<>();

		if (!method.isAnnotationPresent(NoLiterals.class))
			for (String literal : method.getName().split("_")) {
				final CustomCommandMeta.PathMeta.LiteralArgumentMeta argumentMeta = pathMeta.new LiteralArgumentMeta();
				argumentMeta.setName(literal);
				arguments.add(argumentMeta);
			}

		for (Parameter parameter : method.getParameters()) {
			if (Collection.class.isAssignableFrom(parameter.getType()))
				if (!parameter.isAnnotationPresent(ErasureType.class))
					throw new InvalidInputException("Arguments with erasure (ie Collections) must have an @ErasureType annotation");

			final CustomCommandMeta.PathMeta.VariableArgumentMeta argumentMeta = pathMeta.new VariableArgumentMeta();
			argumentMeta.setName(parameter.getName());
			argumentMeta.setType(parameter.getType());
			argumentMeta.setVararg(parameter.isAnnotationPresent(Vararg.class));
			argumentMeta.setStripColor(parameter.isAnnotationPresent(StripColor.class));

			if (parameter.isAnnotationPresent(TabCompleter.class))
				argumentMeta.setTabCompleter(parameter.getAnnotation(TabCompleter.class).value());
			if (parameter.isAnnotationPresent(Permission.class))
				pathMeta.setPermission(parameter.getAnnotation(Permission.class).value());
			if (parameter.isAnnotationPresent(Context.class))
				argumentMeta.setContext(parameter.getAnnotation(Context.class).value());
			if (parameter.isAnnotationPresent(ErasureType.class))
				argumentMeta.setErasureType(parameter.getAnnotation(ErasureType.class).value());

			if (parameter.isAnnotationPresent(Switch.class)) {
				argumentMeta.setSwitchArgument(true);
				final Switch switchAnnotation = parameter.getAnnotation(Switch.class);
				if (switchAnnotation.shorthand() != '-')
					argumentMeta.setSwitchShorthand(switchAnnotation.shorthand());
			}

			if (parameter.isAnnotationPresent(Optional.class)) {
				argumentMeta.setRequired(false);
				argumentMeta.setDefaultValue(parameter.getAnnotation(Optional.class).value());
			} else {
				argumentMeta.setRequired(true);
			}

			for (ArgumentValidator validator : ArgumentValidator.values()) {
				if (parameter.isAnnotationPresent(validator.getAnnotationClass())) {
					final var constructor = validator.getValidatorClass().getConstructor(validator.getAnnotationClass());
					final Annotation annotation = parameter.getAnnotation(validator.getAnnotationClass());
					argumentMeta.getValidators().add(constructor.newInstance(annotation));
				}
			}

			arguments.add(argumentMeta);
		}

		return arguments;
	}

	@NotNull
	public List<Method> getPathMethods(Class<? extends CustomCommand> clazz) {
		final Map<String, Method> overridden = new HashMap<>();

		methodsAnnotatedWith(clazz, Description.class).forEach(method -> {
			String key = getMethodSignature(method);
			if (!overridden.containsKey(key))
				overridden.put(key, method);
			else if (overridden.get(key).getDeclaringClass().isAssignableFrom(method.getDeclaringClass()))
				overridden.put(key, method);
		});

		return new ArrayList<>(overridden.values());
	}

	@NotNull
	private static String getMethodSignature(Method method) {
		return method.getName() + "(" + Arrays.stream(method.getParameterTypes()).map(Class::getName).collect(Collectors.joining(",")) + ")";
	}

}
