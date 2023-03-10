package gg.projecteden.nexus.features.documentation;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.nexus.features.NexusCommand;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.ICustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DoubleSlash;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.api.common.utils.StringUtils.trimFirst;

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
			Map<String, Map<String, List<String>>> sections = new LinkedHashMap<>();

			for (CustomCommand command : Commands.getUniqueCommands()) {
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

					final Description description = method.getAnnotation(Description.class);
					if (missingDescription(description))
						undocumented.computeIfAbsent(command, $ -> new ArrayList<>()).add(method);
					else {
						if ("Help menu".equals(description.value()))
							continue;

						final String feature = getFeature(command);
						final String rank = getRank(command, method);
						final String commandName = command.getName().toLowerCase();
						final String path = method.getAnnotation(Path.class).value();
						final boolean doubleSlash = command.getClass().isAnnotationPresent(DoubleSlash.class);

						final String markup = "* <code>/" + (doubleSlash ? "/" : "") + (commandName + " " + path).trim() + "</code> - " + description.value();
						sections.computeIfAbsent(rank, $ -> new LinkedHashMap<>()).computeIfAbsent(feature, $2 -> new ArrayList<>()).add(markup);
					}
				}
			}

			sections.get("Guest").get("McMMO").add(0, "* <code>/<skillname></code> - Shows the skill scoreboard and more detailed information about the skill");

			sections.forEach((rank, features) -> {
				if (features.size() == 1)
					return;

				new HashSet<>(features.keySet()).forEach(feature -> {
					if (features.get(feature).size() > 1)
						return;

					features.computeIfAbsent("Misc", $ -> new ArrayList<>()).addAll(features.remove(feature));
				});
			});

			IOUtils.fileWrite("plugins/Nexus/wiki/commands.txt", (writer, outputs) -> {
				sections.keySet().stream().sorted(Comparator.comparing(rank -> {
					try {
						return Rank.valueOf(rank.toUpperCase()).ordinal();
					} catch (Exception ignore) {
						return 99;
					}
				})).forEach(rank -> {
					outputs.add("== " + rank + " ==");
					Utils.sortByKey(sections.get(rank)).keySet().forEach(feature -> {
						if (sections.get(rank).keySet().size() > 1)
							outputs.add("=== " + feature + " ===");

						outputs.addAll(sections.get(rank).get(feature).stream().sorted().toList());
					});
				});
			});

			undocumented = sort(undocumented);
			done = true;
		});
	}

	@NotNull
	private static String getFeature(CustomCommand command) {
		String feature = command.getClass().getPackageName().replace(NexusCommand.class.getPackageName(), "");
		if (isNullOrEmpty(feature))
			feature = "misc";

		if (feature.startsWith("."))
			feature = trimFirst(feature);

		if (feature.contains("."))
			feature = StringUtils.listFirst(feature, "\\.");

		if (feature.equals("commands"))
			feature = "misc";

		try {
			feature = Commands.get(feature).getName().replaceAll("([a-z]{2,})([A-Z][a-z]{2,})", "$1 $2");
		} catch (Exception ignore) {
			feature = StringUtils.camelCase(feature);
		}

		if (command.getClass().isAnnotationPresent(DoubleSlash.class))
			feature = "World Edit";

		return feature;
	}

	private static String getRank(CustomCommand command, Method method) {
		final Permission commandPermission = command.getClass().getAnnotation(Permission.class);
		final Permission methodPermission = method.getAnnotation(Permission.class);

		String rank = "guest";
		if (methodPermission != null)
			rank = methodPermission.value().replaceFirst("group\\.", "").replaceFirst("ladder\\.", "");
		if (commandPermission != null)
			rank = commandPermission.value().replaceFirst("group\\.", "").replaceFirst("ladder\\.", "");

		if ("staff".equalsIgnoreCase(rank))
			rank = "builder";
		if ("seniorstaff".equalsIgnoreCase(rank))
			rank = "operator";

		if (command.getClass().isAnnotationPresent(DoubleSlash.class))
			if (rank.startsWith("worldedit"))
				rank = "guest";

		return StringUtils.camelCase(rank);
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
	@Permission(Group.MODERATOR)
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
	@Permission(Group.MODERATOR)
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
