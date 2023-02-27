package gg.projecteden.nexus.features.chat.events;

import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.models.freeze.FreezeService;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.Tasks;
import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class MinecraftChatEvent extends ChatEvent implements Identified {

	public abstract boolean wasSeen();

	@Override
	public String getOrigin() {
		return Nickname.of(getChatter());
	}

	@Override
	public @NonNull Identity identity() {
		return getChatter().identity();
	}

	public void checkWasSeen() {
		if (!wasSeen() && !new FreezeService().get(getChatter()).isFrozen())
			Tasks.wait(1, () -> getChatter().sendMessage(Chat.PREFIX + "No one can hear you! Type &c/ch g &3to talk globally"));
	}

}
