package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import static me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic.MINIGAME_VOICE_CHANNELS;
import static me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic.getVoiceChannelMember;

public class VoiceChannelCommand extends CustomCommand {
	public VoiceChannelCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[channel]")
	@Async
	void run(String channel) {
		Guild guild = Discord.getGuild();
		if (guild == null)
			error("Could not load the Discord server");

		VoiceChannel vc = guild.getVoiceChannelById(channel);
		if (vc == null)
			error("Could not find that voice channel");

		if (!MINIGAME_VOICE_CHANNELS.contains(channel))
			error("You can only move to Minigame voice channels");

		Member member = getVoiceChannelMember(player());
		if (member == null)
			error("Could not find you in a voice channel");

		guild.moveVoiceMember(member, vc).complete();
	}
}
