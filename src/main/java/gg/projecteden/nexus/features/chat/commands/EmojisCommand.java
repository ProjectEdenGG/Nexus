package gg.projecteden.nexus.features.chat.commands;

import gg.projecteden.nexus.features.resourcepack.FontFile.CustomCharacter;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;

import java.util.LinkedHashMap;
import java.util.Map;

@Aliases("emoji")
@Permission("group.staff")
public class EmojisCommand extends CustomCommand {

	public EmojisCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("picker")
	void picker() {
		send(PREFIX);

		final JsonBuilder picker = json();

		EMOJIS.forEach((name, emoji) -> {
			if (picker.isInitialized())
				picker.group().next(" ");

			picker.initialize();
			picker.group().next(emoji).insert(emoji).hover("Shift+Click to insert");
		});

		send(picker);
	}

	private static final Map<String, String> EMOJIS = new LinkedHashMap<>();

	private static final String EMOJI_ROOT = "projecteden/font/emojis/";

	static {
		ResourcePack.getLoader().thenRun(() -> {
			for (CustomCharacter character : ResourcePack.getFontFile().getProviders()) {
				if (!character.getFile().contains(EMOJI_ROOT))
					continue;

				final String name = StringUtils.listLast(character.getFile(), "/").replace(".png", "");
				final String emoji = character.getChars().iterator().next();
				EMOJIS.put(name, emoji);
			}
		});
	}

}
