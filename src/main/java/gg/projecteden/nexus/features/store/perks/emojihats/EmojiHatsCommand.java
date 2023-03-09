package gg.projecteden.nexus.features.store.perks.emojihats;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.LuckPermsUtils.PermissionChange;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

@Aliases("emojihat")
public class EmojiHatsCommand extends CustomCommand {

	public EmojiHatsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		EmojiHat.init();
	}

	@Path("list [page]")
	@Description("List owned emoji hats")
	void list(@Arg("1") int page) {
		final List<EmojiHat> hats = Arrays.stream(EmojiHat.values())
			.filter(type -> type.canBeUsedBy(player()))
			.toList();

		if (hats.isEmpty())
			error("You do not own any emoji hats, purchase with &c/event store");

		final BiFunction<EmojiHat, String, JsonBuilder> formatter = (hat, index) ->
			json("&3" + index + " &a[Start] &e" + camelCase(hat))
				.command("/emojihats " + hat.name().toLowerCase())
				.hover("&eClick to start");

		paginate(hats, formatter, "/emojihats", page);
	}

	@Path("<type>")
	@Description("Activate an emoji hat")
	void run(EmojiHat type) {
		if (!type.canBeUsedBy(player()))
			error("You do not have permission for this emoji hat");

		type.run(player());
	}

	@Path("run <player> <type>")
	@Permission(Group.ADMIN)
	@Description("Activate an emoji hat on another player")
	void run(Player player, EmojiHat type) {
		type.run(player);
	}

	@Path("give <player> <type>")
	@Permission(Group.ADMIN)
	@Description("Give a player access to an emoji hat")
	void give(Player player, EmojiHat type) {
		if (type.canBeUsedBy(player))
			error("&e" + Nickname.of(player) + " &calready owns &e" + camelCase(type));

		PermissionChange.set().player(player).permissions(type.getPermission()).runAsync();
		send(PREFIX + "Gave &e" + camelCase(type) + " &3to &e" + Nickname.of(player));
	}

	@Path("getFrameItems <type>")
	@Permission(Group.ADMIN)
	@Description("Spawn the individual frames of an emoji hat")
	void getFrameItems(EmojiHat type) {
		PlayerUtils.giveItems(player(), type.getFrameItems());
	}

	@TabCompleterFor(EmojiHat.class)
	List<String> tabCompleterForEmojiHat(String filter) {
		return Arrays.stream(EmojiHat.values())
				.filter(type -> type.canBeUsedBy(player()))
				.map(Enum::name)
				.map(String::toLowerCase)
				.toList();
	}

}
