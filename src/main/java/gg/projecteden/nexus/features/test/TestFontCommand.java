package gg.projecteden.nexus.features.test;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.NearCommand;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromWiki;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.TitleBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import net.kyori.adventure.text.ComponentLike;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

@Permission(Group.ADMIN)
public class TestFontCommand extends CustomCommand {

	public TestFontCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<displayType> <text...> [--font] [--rows]")
	@HideFromWiki
	void run(DisplayType type, String text, @Arg("minecraft:default") @Switch String font, @Arg("1") @Switch int rows) {
		type.getConsumer().accept(player(), new TestFontArgs(new JsonBuilder(text).font(font), rows));
	}

	@AllArgsConstructor
	private static class TestFontArgs {
		JsonBuilder json;
		int rows;
	}

	@AllArgsConstructor
	private enum DisplayType {
		ACTIONBAR((player, args) -> ActionBarUtils.sendActionBar(player, args.json, TickTime.SECOND.x(3))),
		TITLE((player, args) -> new TitleBuilder().players(player).title(args.json).subtitle("").stay(TickTime.SECOND.x(3)).send()),
		SUBTITLE((player, args) -> new TitleBuilder().players(player).title("").subtitle(args.json).stay(TickTime.SECOND.x(3)).send()),
		GUI_TITLE((player, args) -> new FontGUI(args).open(player)),
		ECHO((player, args) -> PlayerUtils.send(player, args.json)),
		SEND_CHAT((player, args) -> new NearCommand.Near(player).find().forEach(_player -> PlayerUtils.send(_player, args.json))),
		;

		@Getter
		final BiConsumer<Player, TestFontArgs> consumer;
	}

	private static class FontGUI extends InventoryProvider {
		TestFontArgs args;

		public FontGUI(TestFontArgs args) {
			this.args = args;
		}

		@Override
		public ComponentLike getTitleComponent() {
			return args.json;
		}

		@Override
		protected int getRows(Integer page) {
			return args.rows;
		}

		@Override
		public void init() {
		}
	}
}
