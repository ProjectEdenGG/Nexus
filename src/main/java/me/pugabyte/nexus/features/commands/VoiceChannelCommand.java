package me.pugabyte.nexus.features.commands;

import lombok.NonNull;
import me.pugabyte.nexus.features.discord.Discord;
import me.pugabyte.nexus.features.discord.DiscordId;
import me.pugabyte.nexus.features.minigames.models.mechanics.multiplayer.teams.TeamMechanic;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VoiceChannelCommand extends CustomCommand {
	private static final List<String> VALID_VOICE_CHANNELS = Arrays.stream(DiscordId.VoiceChannel.values()).map(DiscordId.VoiceChannel::getId).collect(Collectors.toList());

	public VoiceChannelCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("[channel]")
	void run(String channel) {
		Guild guild = Discord.getGuild();
		if (guild == null) {
			error("Could not load the Discord server");
			return;
		}
		VoiceChannel vc = guild.getVoiceChannelById(channel);
		if (vc == null) {
			error("Could not find that voice channel");
			return;
		}
		if (!VALID_VOICE_CHANNELS.contains(channel)) {
			error("You can only move to Minigame voice channels");
			return;
		}
		Member member = TeamMechanic.getVoiceChannelMember(player());
		if (member == null) {
			error("Could not find you in a voice channel");
			return;
		}
		guild.moveVoiceMember(member, vc).complete();
	}
}
