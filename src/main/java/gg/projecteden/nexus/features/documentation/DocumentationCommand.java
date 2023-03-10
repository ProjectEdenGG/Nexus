package gg.projecteden.nexus.features.documentation;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
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
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
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
			IOUtils.fileWrite("plugins/Nexus/wiki/commands.txt", (writer, outputs) -> {
				final List<CustomCommand> commands = Commands.getUniqueCommands().stream()
					.sorted(Comparator.comparing(ICustomCommand::getName))
					.toList();

				for (CustomCommand command : commands) {
					if (command.getClass().isAnnotationPresent(HideFromWiki.class))
						continue;
					if (command.getClass().isAnnotationPresent(Disabled.class))
						continue;
					if (!isEnabledInProd(command.getClass().getAnnotation(Environments.class)))
						continue;

					final List<Method> methods = command.getPathMethods().stream()
						.sorted(DISPLAY_SORTER)
						.toList();

					for (Method method : methods) {
						if (method.isAnnotationPresent(HideFromWiki.class))
							continue;
						if (method.isAnnotationPresent(Disabled.class))
							continue;
						if (!isEnabledInProd(method.getAnnotation(Environments.class)))
							continue;

						if (missingDescription(method.getAnnotation(Description.class)))
							undocumented.computeIfAbsent(command, $ -> new ArrayList<>()).add(method);
						else {
							final String description = method.getAnnotation(Description.class).value();
							if ("Help menu".equals(description))
								continue;

							outputs.add("* '''/" + (command.getName().toLowerCase() + " " + method.getAnnotation(Path.class).value()).trim() + "''' - " + description);
						}
					}
				}
			});

			undocumented = sort(undocumented);
			done = true;
		});
	}

	private static boolean isEnabledInProd(Environments annotation) {
		if (annotation == null)
			return true;
		if (Arrays.asList(annotation.value()).contains(Env.PROD))
			return true;

		return false;
	}

	public static <K, V extends List<?>> LinkedHashMap<K, V> sort(Map<K, V> map) {
		return Utils.reverse(Utils.collect(map.entrySet().stream().sorted(Comparator.comparing(list -> list.getValue().size()))));
	}

	@Path("commands validate [page]")
	@Description("Validate that all commands have documentation")
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
	@Description("View which paths in a command are undocumented")
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
