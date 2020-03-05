package me.pugabyte.bncore.features.votes;

import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Arg;
import me.pugabyte.bncore.framework.commands.models.annotations.ConverterFor;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.TabCompleterFor;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.vote.VoteService;
import me.pugabyte.bncore.models.vote.Voter;

import java.util.List;

public class JVoteCommand extends CustomCommand {

	public JVoteCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("getVoter [player]")
	void getVoter(@Arg("self") Voter voter) {
		send("Voter: " + voter);
	}

	@ConverterFor(Voter.class)
	Voter convertToVoter(String value) {
		return new VoteService().get(convertToOfflinePlayer(value));
	}

	@TabCompleterFor(Voter.class)
	List<String> tabCompleteVoter(String value) {
		return tabCompletePlayer(value);
	}

}
