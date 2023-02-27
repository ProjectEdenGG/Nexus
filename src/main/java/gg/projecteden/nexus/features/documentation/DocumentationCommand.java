package gg.projecteden.nexus.features.documentation;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.ICustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DocumentationCommand extends CustomCommand {
	private static Map<CustomCommand, List<Method>> undocumented = new LinkedHashMap<>();
	private static boolean done;

	public DocumentationCommand(@NonNull CommandEvent event) {
		super(event);
	}

	private static boolean missingDescription(Description annotation) {
		return annotation == null || annotation.value().isEmpty();
	}

	static {
		Tasks.async(() -> {
			for (CustomCommand command : Commands.getUniqueCommands()) {
				if (command.getClass().isAnnotationPresent(HideFromWiki.class))
					continue;
				if (command.getClass().isAnnotationPresent(Disabled.class))
					continue;

				final List<Method> methods = command.getPathMethods();
				final Description description = Utils.getAnnotation(command.getClass(), Description.class);

				if (methods.size() == 1) {
					if (missingDescription(description))
						undocumented.computeIfAbsent(command, $ -> new ArrayList<>()).add(methods.get(0));

					continue;
				}

				for (Method method : methods) {
					if (method.isAnnotationPresent(HideFromWiki.class))
						continue;
					if (method.isAnnotationPresent(Disabled.class))
						continue;

					if (missingDescription(method.getAnnotation(Description.class)))
						undocumented.computeIfAbsent(command, $ -> new ArrayList<>()).add(method);
				}
			}

			undocumented = sort(undocumented);
			done = true;
		});
	}

	public static <K, V extends List<?>> LinkedHashMap<K, V> sort(Map<K, V> map) {
		return Utils.reverse(Utils.collect(map.entrySet().stream().sorted(Comparator.comparing(list -> list.getValue().size()))));
	}

	@Path("commands validate [page]")
	void commands_validate(@Arg("1") int page) {
		if (!done)
			error("Processing commands, please wait");

		if (undocumented.isEmpty())
			error("&aAll commands documented");

		line();
		send(PREFIX + "Commands with missing documentation");
		send(PREFIX + "Total: &e" + undocumented.values().stream().mapToLong(Collection::size).sum() + " paths in " + undocumented.keySet().size() + " commands");
		final BiFunction<CustomCommand, String, JsonBuilder> formatter = (command, index) ->
			json("&3" + index + " &e/" + command.getName().toLowerCase() + " &7- " + undocumented.get(command).size() + " undocumented paths");

		paginate(undocumented.keySet(), formatter, "/documentation commands validate", page);
	}

	@Path("commands info <command> [page]")
	void commands_info(@Arg CustomCommand command, @Arg("1") int page) {
		if (!undocumented.containsKey(command))
			error("&c" + command.getName().toLowerCase() + " &ais fully documented");

		send(PREFIX + "Undocumented paths on &c/" + command.getName().toLowerCase());
		final BiFunction<Method, String, JsonBuilder> formatter = (method, index) ->
			json("&3" + index + " &e" + method.getName());

		paginate(undocumented.get(command), formatter, "/documentation commands info " + command.getName(), page);
	}

	@TabCompleterFor(CustomCommand.class)
	List<String> tabCompleteCustomCommand(String filter) {
		return Commands.getCommands().values().stream()
			.map(ICustomCommand::getName)
			.filter(path -> path.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	@ConverterFor(CustomCommand.class)
	CustomCommand convertToCustomCommand(String value) {
		final CustomCommand command = Commands.get(value.toLowerCase());
		if (command == null)
			error("Command /" + value.toLowerCase() + " not found");
		return command;
	}

}
