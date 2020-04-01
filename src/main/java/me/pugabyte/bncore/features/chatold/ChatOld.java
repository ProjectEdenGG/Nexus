package me.pugabyte.bncore.features.chatold;

import com.dthielke.herochat.Channel;
import com.dthielke.herochat.Chatter;
import com.dthielke.herochat.Herochat;
import me.pugabyte.bncore.BNCore;
import org.bukkit.plugin.Plugin;

public class ChatOld {

	static {
		BNCore.registerPlaceholder("currentchannel", event -> {
			Chatter chatter = Herochat.getChatterManager().getChatter(event.getPlayer());
			if (chatter == null)
				return "&eNone";
			Channel activeChannel = chatter.getActiveChannel();
			if (activeChannel == null)
				return "&eNone";
			if (activeChannel.getName().contains("convo"))
				return "&b" + activeChannel.getName().replace("convo", "DM / ").replace(event.getPlayer().getName(), "");

			return activeChannel.getColor() + activeChannel.getName();
		});
	}

	public ChatOld() {
		Plugin herochat = BNCore.getInstance().getServer().getPluginManager().getPlugin("Herochat");

		if (herochat != null && herochat.isEnabled()) {
			new OldChatListener();
		}
	}

}
