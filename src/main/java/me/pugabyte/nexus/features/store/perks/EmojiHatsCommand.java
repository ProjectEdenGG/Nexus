package me.pugabyte.nexus.features.store.perks;

import lombok.NonNull;
import me.pugabyte.nexus.features.store.perks.emojihats.EmojiHat;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.entity.Player;

@Permission("emojihats.use")
public class EmojiHatsCommand extends CustomCommand {

	public EmojiHatsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		EmojiHat.init();
	}

	@Path("<type>")
	void run(EmojiHat type) {
		type.run(player());
	}

	@Path("run <player> <type>")
	@Permission("group.admin")
	void run(Player player, EmojiHat type) {
		type.run(player);
	}

	@Path("getFrameItems <type>")
	@Permission("group.admin")
	void getFrameItems(EmojiHat type) {
		PlayerUtils.giveItems(player(), type.getFrameItems());
	}

}
