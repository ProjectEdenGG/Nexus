package gg.projecteden.nexus.features.test;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.commands.NearCommand;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
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

public class TestFontCommand extends CustomCommand {

	public TestFontCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<displayType> <text...> [--font]")
	void run(DisplayType type, String text, @Arg("minecraft:default") @Switch String font) {
		type.getConsumer().accept(player(), new JsonBuilder(text).font(font));
	}

	@AllArgsConstructor
	private enum DisplayType {
		ACTIONBAR((player, json) -> ActionBarUtils.sendActionBar(player, json, TickTime.SECOND.x(3))),
		TITLE((player, json) -> new TitleBuilder().players(player).title(json).subtitle("").stay(TickTime.SECOND.x(3)).send()),
		SUBTITLE((player, json) -> new TitleBuilder().players(player).title("").subtitle(json).stay(TickTime.SECOND.x(3)).send()),
		GUI_TITLE((player, json) -> new FontGUI(json).open(player)),
		ECHO(PlayerUtils::send),
		SEND_CHAT((player, json) -> new NearCommand.Near(player).find().forEach(_player -> PlayerUtils.send(_player, json))),
		;

		@Getter
		final BiConsumer<Player, JsonBuilder> consumer;
	}

	private static class FontGUI extends InventoryProvider {
		JsonBuilder title;

		public FontGUI(JsonBuilder title) {
			this.title = title;
		}

		@Override
		public ComponentLike getTitleComponent() {
			return title;
		}

		@Override
		protected int getRows(Integer page) {
			return 1;
		}

		@Override
		public void init() {

		}
	}
}
