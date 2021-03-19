package me.pugabyte.nexus.features.chat.events;

public abstract class MinecraftChatEvent extends ChatEvent {

	public abstract boolean wasSeen();

	public abstract void setOriginalMessage(String message);

	public abstract String getOriginalMessage();

	@Override
	public String getOrigin() {
		return getChatter().getOfflinePlayer().getName();
	}

}
