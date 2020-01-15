package me.pugabyte.bncore.features.chat.translator;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

@Aliases("translate")
@Permission("translate")
public class TranslateCommand extends CustomCommand {

	Translator translator;
	public TranslateCommand(CommandEvent event){
		super(event);
		translator = BNCore.translator;
	}

	@Path("stop {player}")
	@Permission("use")
	void remove(@Arg Player player){
		if(player != null) {
			translator.translatorMap.get(player.getUniqueId()).remove(player().getUniqueId());
			reply(PREFIX + "You are no longer translating " + player.getDisplayName() + ".");
			return;
		}
		for(UUID uuid : translator.translatorMap.keySet()){
			translator.translatorMap.get(uuid).remove(player().getUniqueId());
		}
		reply(PREFIX + "Stopping all active translations.");
	}

	@Path("{player}")
	@Permission("use")
	void translate(@Arg Player player){
		ArrayList<UUID> uuids = new ArrayList<UUID>(){{
			add(player().getUniqueId());
			try {
				addAll(translator.translatorMap.get(player.getUniqueId()));
			} catch (Exception e) { /**ignoring**/ }
		}};
		translator.translatorMap.put(player.getUniqueId(), uuids);

		reply(PREFIX + "You are now translating messages from " + player.getDisplayName() + ".");
		BNCore.log(player().getDisplayName() + " has started translating " + player.getDisplayName());
	}

}
