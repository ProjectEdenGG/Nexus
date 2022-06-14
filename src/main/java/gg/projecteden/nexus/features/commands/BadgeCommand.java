package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.badge.BadgeUser;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import lombok.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Aliases("badges")
public class BadgeCommand extends CustomCommand {
	private final BadgeUserService service = new BadgeUserService();

	public BadgeCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("off [user]")
	void off(@Arg(value = "self", permission = Group.STAFF) BadgeUser user) {
		user.setActive(null);
		service.save(user);
		send(PREFIX + (isSelf(user) ? "Badge" : user.getNickname() + "'s badge") + " &cdisabled");
	}

	@Path("<badge> [user]")
	void set(Badge badge, @Arg(value = "self", permission = Group.STAFF) BadgeUser user) {
		user.setActive(badge);
		service.save(user);
		send(PREFIX + "Set " + (isSelf(user) ? "your" : "&e" + user.getNickname() + "&3's") + " active badge to &e" + camelCase(badge));
	}

	@Permission(Group.SENIOR_STAFF)
	@Path("give <badge> [user]")
	void give(Badge badge, @Arg("self") BadgeUser user) {
		user.give(badge);
		service.save(user);
		send(PREFIX + "Gave " + (isSelf(user) ? "yourself" : "&e" + user.getNickname()) + " &3the &e" + camelCase(badge) + " &3badge");
	}

	@ConverterFor(Badge.class)
	Badge convertToBadge(String value, BadgeUser context) {
		Badge badge = convertToEnum(value, Badge.class);
		if (isSeniorStaff())
			return badge;

		if (context == null)
			context = service.get(player());

		if (!context.owns(badge))
			error("You do not own that badge!");

		return badge;
	}

	@TabCompleterFor(Badge.class)
	List<String> tabCompleteBadge(String filter, BadgeUser context) {
		if (isAdmin())
			return tabCompleteEnum(filter, Badge.class);

		if (context == null)
			context = service.get(player());

		BadgeUser user = context;
		return Arrays.stream(Badge.values())
			.filter(user::owns)
			.filter(value -> value.name().toLowerCase().startsWith(filter.toLowerCase()))
			.map(defaultTabCompleteEnumFormatter())
			.collect(Collectors.toList());
	}

}
