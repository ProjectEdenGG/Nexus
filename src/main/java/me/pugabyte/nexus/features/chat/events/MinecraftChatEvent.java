package me.pugabyte.nexus.features.chat.events;

import net.kyori.adventure.identity.Identified;
import net.kyori.adventure.identity.Identity;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class MinecraftChatEvent extends ChatEvent implements Identified {

	public abstract boolean wasSeen();

	@Override
	public String getOrigin() {
		return getChatter().getOfflinePlayer().getName();
	}

	@Override
	public @NonNull Identity identity() {
		return getChatter().identity();
	}
}
