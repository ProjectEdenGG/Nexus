package me.pugabyte.bncore.features.discord;

import me.pugabyte.bncore.BNCore;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

	@Override
	public void onMessageReceived(MessageReceivedEvent event) {
		BNCore.log(event.getMessage().getContentRaw());
	}
}
