package gg.projecteden.nexus.features.documentation;

import gg.projecteden.api.common.annotations.Disabled;
import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.nexus.API;
import gg.projecteden.nexus.features.NexusCommand;
import gg.projecteden.nexus.features.documentation.DocumentationCommand.AllCommands.CommandMeta;
import gg.projecteden.nexus.features.documentation.DocumentationCommand.AllCommands.CommandMeta.PathMeta;
import gg.projecteden.nexus.features.documentation.DocumentationCommand.AllCommands.CommandMeta.PathMeta.ArgumentMeta;
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
import gg.projecteden.nexus.framework.commands.models.annotations.Redirects.Redirect;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.nexus.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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

						String feature = getFeature(command, method);
						String rank = getRank(command, method);
						String commandName = command.getName().toLowerCase();
						String path = method.getAnnotation(Path.class).value();
						boolean doubleSlash = command.getClass().isAnnotationPresent(DoubleSlash.class);

						if (Nullables.isNullOrEmpty(path))
							path = "!"; // sorting

						final String markup = "<code>/" + (doubleSlash ? "/" : "") + commandName + " " + path + "</code> - " + description.value();
						sections.computeIfAbsent(rank, $ -> new LinkedHashMap<>()).computeIfAbsent(feature, $2 -> new ArrayList<>()).add(markup);
					}
				}
			}

			sections.get("Guest").get("McMMO").add(0, "<code>/<skillname></code> - Shows the skill scoreboard and more detailed information about the skill");

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
				outputs.add("== Custom Commands ==");
				sections.keySet().stream().sorted(Comparator.comparing(rank -> {
					try {
						return Rank.valueOf(rank.toUpperCase()).ordinal();
					} catch (Exception ignore) {
						return 99;
					}
				})).forEach(rank -> {
					outputs.add("=== " + rank + " ===");
					Utils.sortByKey(sections.get(rank)).keySet().forEach(feature -> {
						if (sections.get(rank).keySet().size() > 1)
							outputs.add("==== " + feature + " ====");

						outputs.add("{| class=\"wikitable\"");

						sections.get(rank).get(feature).stream().sorted().forEach(command -> {
							final String[] split = command.replaceAll(" !</code>", "</code>").replaceAll("\\|", "<nowiki>|</nowiki>").split(" - ", 2);
							outputs.add("|-");
							outputs.add("| " + split[0]);
							outputs.add("| " + split[1]);
						});

						outputs.add("|}");
					});
				});
			});

			undocumented = sort(undocumented);
			done = true;
		});
	}

	@NotNull
	private static String getFeature(CustomCommand command, Method method) {
		String feature = command.getClass().getPackageName().replace(NexusCommand.class.getPackageName(), "");
		if (Nullables.isNullOrEmpty(feature))
			feature = "misc";

		if (feature.startsWith("."))
			feature = gg.projecteden.api.common.utils.StringUtils.trimFirst(feature);

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

		WikiConfig wikiConfig = command.getClass().getAnnotation(WikiConfig.class);
		if (wikiConfig != null && !Nullables.isNullOrEmpty(wikiConfig.feature()))
			feature = wikiConfig.feature();

		wikiConfig = method.getAnnotation(WikiConfig.class);
		if (wikiConfig != null && !Nullables.isNullOrEmpty(wikiConfig.feature()))
			feature = wikiConfig.feature();

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

		WikiConfig wikiConfig = command.getClass().getAnnotation(WikiConfig.class);
		if (wikiConfig != null && !Nullables.isNullOrEmpty(wikiConfig.rank()))
			rank = wikiConfig.rank();

		wikiConfig = method.getAnnotation(WikiConfig.class);
		if (wikiConfig != null && !Nullables.isNullOrEmpty(wikiConfig.rank()))
			rank = wikiConfig.rank();

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

		new Paginator<CustomCommand>()
			.values(undocumented.keySet())
			.formatter(formatter)
			.command("/documentation commands validate")
			.page(page)
			.send();
	}

	@Path("commands info <command> [page]")
	@Permission(Group.MODERATOR)
	@Description("View which paths in a command are undocumented")
	void commands_info(@Arg CustomCommand command, @Arg("1") int page) {
		if (!undocumented.containsKey(command))
			error("&c" + command.getName().toLowerCase() + " &ais fully documented");

		send(PREFIX + "Undocumented paths on &c/" + command.getName().toLowerCase());
		new Paginator<Method>()
			.values(undocumented.get(command))
			.formatter((method, index) -> json("&3" + index + " &e" + method.getName()))
			.command("/documentation commands info " + command.getName())
			.page(page)
			.send();
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

	@Path("commands meta dump")
	void commands_meta_dump() {
		final AllCommands allCommands = new AllCommands();
		for (CustomCommand command : Commands.getUniqueCommands()) {
			allCommands.getCommands().add(CommandMeta.builder()
				.name(command.getName())
				.aliases(command.getAliases())
				.paths(command.getPathMethods().stream()
					.filter(method -> !Nullables.isNullOrEmpty(method.getAnnotation(Path.class).value()))
					.map(method -> PathMeta.builder()
						.arguments(new ArrayList<>() {{
							for (String argument : method.getAnnotation(Path.class).value().split(" ")) {
								int variableIndex = 0;
								if (argument.startsWith("[") || argument.startsWith("<")) {
									final Parameter parameter = method.getParameters()[variableIndex];
									add(ArgumentMeta.builder()
										.pathName(argument.replaceAll("\\[]<>", ""))
										.parameterName(parameter.getName())
										.required(argument.startsWith("<"))
										.build());
								} else {
									add(ArgumentMeta.builder()
										.pathName(argument)
										.parameterName(null)
										.required(true)
										.build());
								}
							}
						}}).build()).toList())
				.redirects(new HashMap<>() {{
					if (command.getClass().isAnnotationPresent(Redirect.class)) {
						for (Redirect annotation : command.getClass().getAnnotationsByType(Redirect.class))
							for (String from : annotation.from())
								put(from, annotation.to());
					}
				}})
				.build());
		}

		IOUtils.fileWrite("plugins/Nexus/commands-meta.json", (writer, outputs) ->
			outputs.add(API.get().getPrettyPrinter().create().toJson(allCommands)));
	}

	@Data
	static class AllCommands {
		private List<CommandMeta> commands = new ArrayList<>();

		@Data
		@Builder
		@NoArgsConstructor
		@AllArgsConstructor
		static class CommandMeta {
			private String name;
			private List<String> aliases;
			private List<PathMeta> paths;
			private Map<String, String> redirects;

			public List<String> getAllAliases() {
				List<String> aliases = getAliases();
				aliases.add(getName());
				return aliases.stream().map(String::toLowerCase).collect(Collectors.toList());
			}

			@Data
			@Builder
			@NoArgsConstructor
			@AllArgsConstructor
			static class PathMeta {
				private List<ArgumentMeta> arguments;

				@Data
				@Builder
				@NoArgsConstructor
				@AllArgsConstructor
				static class ArgumentMeta {
					private String pathName;
					private String parameterName;
					private boolean required;

					public boolean isLiteral() {
						return !Nullables.isNullOrEmpty(parameterName);
					}
				}
			}

		}

	}

}
