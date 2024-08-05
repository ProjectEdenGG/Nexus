package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.discord.Bot;
import gg.projecteden.nexus.features.discord.Bot.DiscordConnectedEvent;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.models.badge.BadgeUser.Badge;
import gg.projecteden.nexus.models.badge.BadgeUserService;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@RetryIfInterrupted
public class BirthdayEndJob extends AbstractJob {
	@NonNull
	private UUID uuid;
	private Badge badge;

	@Override
	protected CompletableFuture<JobStatus> run() {
		if (Nexus.getEnv() != Env.PROD)
			return completed();

		var future = completable();

		Runnable runnable = () -> {
			final Role role = Nerd.of(uuid).getRank().isStaff() ? Role.STAFF_BIRTHDAY : Role.BIRTHDAY;
			new DiscordUserService().get(uuid).removeRole(role);
			new BadgeUserService().edit(uuid, user -> {
				boolean usingBirthdayBadge = user.getActive() == Badge.BIRTHDAY;
				user.take(Badge.BIRTHDAY);
				if (usingBirthdayBadge)
					user.setActive(badge);
			});

			future.complete(JobStatus.COMPLETED);
		};

		if (Discord.isConnected())
			runnable.run();
		else
			Nexus.registerListener(new Listener() {
				@EventHandler
				public void on(DiscordConnectedEvent event) {
					if (event.getBot() != Bot.KODA)
						return;

					runnable.run();
					Nexus.unregisterListener(this);
				}
			});

		return future;

	}

}
