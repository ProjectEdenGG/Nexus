package gg.projecteden.nexus.models.scheduledjobs.jobs;

import gg.projecteden.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.models.scheduledjobs.common.RetryIfInterrupted;
import gg.projecteden.nexus.models.discord.DiscordUserService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.utils.DiscordId.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@RetryIfInterrupted
public class BirthdaysRemoveRoleJob extends AbstractJob {
	private UUID uuid;

	@Override
	protected CompletableFuture<JobStatus> run() {
		final Role role = Nerd.of(uuid).getRank().isStaff() ? Role.STAFF_BIRTHDAY : Role.BIRTHDAY;
		new DiscordUserService().get(uuid).removeRole(role);
		return completed();
	}

}
