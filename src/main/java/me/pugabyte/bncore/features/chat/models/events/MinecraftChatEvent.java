package me.pugabyte.bncore.features.chat.models.events;

import me.pugabyte.bncore.features.chat.models.Chatter;

public abstract class MinecraftChatEvent extends ChatEvent {

	public abstract Chatter getChatter();

	public abstract boolean wasSeen();

	@Override
	public String getOrigin() {
		return getChatter().getPlayer().getName();
	}

}
