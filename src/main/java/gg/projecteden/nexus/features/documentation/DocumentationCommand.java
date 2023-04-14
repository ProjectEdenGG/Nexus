package gg.projecteden.nexus.features.documentation;

import gg.projecteden.nexus.API;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.NexusCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.parameter.Optional;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Description;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.DoubleSlash;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Permission.Group;
import gg.projecteden.nexus.framework.commandsv2.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta;
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
import java.util.ArrayList;
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

			for (CustomCommandMeta commandMeta : Nexus.getInstance().getCommands().getUniqueCommands()) {
				if (commandMeta.isHideFromWiki())
					continue;

				for (CustomCommandMeta.PathMeta pathMeta : commandMeta.getPaths().stream().sorted(DISPLAY_SORTER).toList()) {
					if (pathMeta.isHideFromWiki())
						continue;

					if ("Help menu".equals(pathMeta.getDescription()))
						continue;

					String feature = getFeature(commandMeta, pathMeta);
					String rank = getRank(commandMeta, pathMeta);
					String commandName = commandMeta.getName().toLowerCase();
					String usage = pathMeta.getUsage();
					boolean doubleSlash = commandMeta.isDoubleSlash();

					if (isNullOrEmpty(usage))
						usage = "!"; // sorting

					final String markup = "<code>/" + (doubleSlash ? "/" : "") + commandName + " " + usage + "</code> - " + pathMeta.getDescription();
					sections.computeIfAbsent(rank, $ -> new LinkedHashMap<>()).computeIfAbsent(feature, $2 -> new ArrayList<>()).add(markup);
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
	private static String getFeature(CustomCommandMeta commandMeta, CustomCommandMeta.PathMeta pathMeta) {
		String feature = commandMeta.getInstance().getClass().getPackageName().replace(NexusCommand.class.getPackageName(), "");
		if (isNullOrEmpty(feature))
			feature = "misc";

		if (feature.startsWith("."))
			feature = trimFirst(feature);

		if (feature.contains("."))
			feature = StringUtils.listFirst(feature, "\\.");

		if (feature.equals("commands"))
			feature = "misc";

		try {
			feature = Nexus.getInstance().getCommands().get(feature).getName().replaceAll("([a-z]{2,})([A-Z][a-z]{2,})", "$1 $2");
		} catch (Exception ignore) {
			feature = StringUtils.camelCase(feature);
		}

		if (commandMeta.isDoubleSlash())
			feature = "World Edit";

		WikiConfig wikiConfig = commandMeta.getWikiConfig();
		if (wikiConfig != null && !isNullOrEmpty(wikiConfig.feature()))
			feature = wikiConfig.feature();

		wikiConfig = pathMeta.getWikiConfig();
		if (wikiConfig != null && !isNullOrEmpty(wikiConfig.feature()))
			feature = wikiConfig.feature();

		return feature;
	}

	private static String getRank(CustomCommandMeta commandMeta, CustomCommandMeta.PathMeta pathMeta) {
		final String commandPermission = commandMeta.getPermission();
		final String methodPermission = pathMeta.getPermission();

		String rank = "guest";
		if (!isNullOrEmpty(methodPermission))
			rank = methodPermission.replaceFirst("group\\.", "").replaceFirst("ladder\\.", "");
		if (!isNullOrEmpty(commandPermission))
			rank = commandPermission.replaceFirst("group\\.", "").replaceFirst("ladder\\.", "");

		if ("staff".equalsIgnoreCase(rank))
			rank = "builder";
		if ("seniorstaff".equalsIgnoreCase(rank))
			rank = "operator";

		if (commandMeta.isDoubleSlash())
			if (rank.startsWith("worldedit"))
				rank = "guest";

		WikiConfig wikiConfig = commandMeta.getWikiConfig();
		if (wikiConfig != null && !isNullOrEmpty(wikiConfig.rank()))
			rank = wikiConfig.rank();

		wikiConfig = pathMeta.getWikiConfig();
		if (wikiConfig != null && !isNullOrEmpty(wikiConfig.rank()))
			rank = wikiConfig.rank();

		return StringUtils.camelCase(rank);
	}

	public static <K, V extends List<?>> LinkedHashMap<K, V> sort(Map<K, V> map) {
		return Utils.reverse(Utils.collect(map.entrySet().stream().sorted(Comparator.comparing(list -> list.getValue().size()))));
	}

	@Permission(Group.MODERATOR)
	@Description("Validate that all commands have documentation")
	void commands_validate(@Optional("1") int page) {
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

	@Permission(Group.MODERATOR)
	@Description("View which paths in a command are undocumented")
	void commands_info(CustomCommand command, @Optional("1") int page) {
		if (!undocumented.containsKey(command))
			error("&c" + command.getName().toLowerCase() + " &ais fully documented");

		send(PREFIX + "Undocumented paths on &c/" + command.getName().toLowerCase());
		final BiFunction<Method, String, JsonBuilder> formatter = (method, index) ->
			json("&3" + index + " &e" + method.getName());

		paginate(undocumented.get(command), formatter, "/documentation commands info " + command.getName(), page);
	}

	@TabCompleterFor(CustomCommand.class)
	List<String> tabCompleteCustomCommand(String filter) {
		return Nexus.getInstance().getCommands().getUniqueCommands().stream()
			.map(CustomCommandMeta::getName)
			.filter(name -> name.toLowerCase().startsWith(filter.toLowerCase()))
			.collect(Collectors.toList());
	}

	@ConverterFor(CustomCommand.class)
	CustomCommand convertToCustomCommand(String value) {
		final CustomCommandMeta command = Nexus.getInstance().getCommands().get(value.toLowerCase());
		if (command == null)
			error("Command /" + value.toLowerCase() + " not found");
		return command.getInstance();
	}

	@Description("Dump command meta to a file")
	void commands_meta_dump() {
		IOUtils.fileWrite("plugins/Nexus/commands-meta.json", (writer, outputs) ->
			outputs.add(API.get().getPrettyPrinter().create().toJson(Nexus.getInstance().getCommands().getUniqueCommands())));
	}

}
