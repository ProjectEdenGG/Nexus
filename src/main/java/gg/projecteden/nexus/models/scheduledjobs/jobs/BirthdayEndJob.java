package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.api.discord.DiscordId.Role;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.api.mongodb.models.scheduledjobs.common.RetryIfInterrupted;
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
		final Role role = Nerd.of(uuid).getRank().isStaff() ? Role.STAFF_BIRTHDAY : Role.BIRTHDAY;
		new DiscordUserService().get(uuid).removeRole(role);
		new BadgeUserService().edit(uuid, user -> {
			user.take(Badge.BIRTHDAY);
			user.setActive(badge);
		});
		return completed();
	}

}
