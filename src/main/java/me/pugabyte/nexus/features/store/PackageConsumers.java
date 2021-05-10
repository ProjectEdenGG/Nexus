package me.pugabyte.nexus.features.store;

import me.lexikiq.HasUniqueId;
import me.pugabyte.nexus.models.autotorch.AutoTorchService;
import me.pugabyte.nexus.models.autotorch.AutoTorchUser;

import java.util.function.Consumer;

public enum PackageConsumers implements Consumer<HasUniqueId> {
	AUTO_TORCH {
		// this automatically enables auto torches
		// "you could just have the boolean default to true" i could! but i didn't want to annoy admins,
		//   as that would also turn it on for all admins by default rather than just direct purchasers.
		// yeah it's kinda silly but i don't like being intrusive so fight me
		// -lexi

		@Override
		public void accept(HasUniqueId hasUniqueId) {
			AutoTorchService service = new AutoTorchService();
			AutoTorchUser user = service.get(hasUniqueId);
			user.setEnabled(true);
			service.save(user);
		}
	}
	;

	public void expire(HasUniqueId hasUniqueId) {}
}
