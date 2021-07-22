package gg.projecteden.nexus.features.commands;

import com.google.common.collect.ImmutableSet;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PronounsCommand extends CustomCommand {
	public static final Set<String> PRONOUN_WHITELIST = ImmutableSet.of("she/her", "they/them", "he/him", "it/its", "xe/xem", "no pronouns", "any pronouns");
	public static final Map<String, String> PRONOUN_ALIASES = new HashMap<>();

	static {
		PRONOUN_WHITELIST.forEach(string -> {
			PRONOUN_ALIASES.put(string, string);
			for (String alias : string.split(" ")[0].split("/"))
				PRONOUN_ALIASES.put(alias, string);
		});
	}

	public PronounsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("toggle <pronoun>")
	void toggle(String pronoun) {
		pronoun = pronoun.toLowerCase().trim();
		if (!nerd().getPronouns().contains(pronoun))
			add(pronoun, nerd());
		else
			remove(pronoun, nerd());
	}

	@Path("add <pronoun> [user]")
	void add(String pronoun, @Arg(value = "self", permission = "group.staff") Nerd nerd) {
		boolean isSelf = nerd.getUuid().equals(uuid());
		String user = isSelf ? "your" : nerd.getNickname()+"'s";
		nerd.addPronoun(pronoun, !isPlayer() ? "Console" : nickname());
		send(PREFIX + "Added &e"+getPronoun(pronoun).replace("&", "")+"&3 to "+user+" pronouns");
	}

	@Path("remove <pronoun> [user]")
	void remove(String pronoun, @Arg(value = "self", permission = "group.staff") Nerd nerd) {
		boolean isSelf = nerd.getUuid().equals(uuid());
		String user = isSelf ? "your" : nerd.getNickname()+"'s";
		nerd.removePronoun(pronoun, !isPlayer() ? "Console" : nickname());
		send(PREFIX + "Removed &e"+getPronoun(pronoun).replace("&", "")+"&3 from "+user+" pronouns");
	}

	@Path("list [user]")
	void list(@Arg("self") Nerd nerd) {
		boolean isSelf = nerd.getUuid().equals(uuid());
		List<String> pronouns = new ArrayList<>(nerd.getPronouns());
		if (pronouns.isEmpty())
			error((isSelf ? "You have" : nerd.getNickname()+" has") + " no saved pronouns");
		send(PREFIX + (isSelf ? "Your" : nerd.getNickname()+"'s") + " pronouns are " + pronouns.stream().map(pronoun -> "&e"+pronoun+"&3").collect(Collectors.joining(", ")));
	}

	public static String getPronoun(String pronoun) {
		pronoun = pronoun.toLowerCase().trim();
		return PRONOUN_ALIASES.getOrDefault(pronoun, pronoun);
	}
}
