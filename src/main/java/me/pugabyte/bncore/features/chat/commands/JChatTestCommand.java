package me.pugabyte.bncore.features.chat.commands;

import lombok.NonNull;
import me.pugabyte.bncore.features.chat.ChatManager;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.chat.ChatService;
import me.pugabyte.bncore.models.chat.Chatter;
import me.pugabyte.bncore.models.chat.PrivateChannel;
import me.pugabyte.bncore.utils.Utils;

public class JChatTestCommand extends CustomCommand {

	public JChatTestCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("clearCache")
	void clearCache() {
		new ChatService().clearCache();
		send("Cache cleared");
	}


	@Path("get [chatter]")
	void get(@Arg("self") Chatter chatter) {
		if (chatter == null)
			error("null");
		send(chatter.toString());
	}

	@Path("save [chatter]")
	void save(@Arg("self") Chatter chatter) {
		chatter.setActiveChannel(ChatManager.getMainChannel());
		chatter.join(ChatManager.getChannel("staff"));
		chatter.join(ChatManager.getChannel("local"));
		PrivateChannel blast = new PrivateChannel(chatter, new ChatService().get(Utils.getPlayer("Blast")));
		chatter.setLastPrivateMessage(blast);
		chatter.setActiveChannel(blast);
		new ChatService().save(chatter);
	}


}
