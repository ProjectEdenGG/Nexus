package gg.projecteden.nexus.features.chat.events;

import gg.projecteden.nexus.models.nickname.Nickname;
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
}
