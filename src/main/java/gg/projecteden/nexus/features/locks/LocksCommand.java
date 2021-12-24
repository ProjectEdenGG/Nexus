package gg.projecteden.nexus.features.locks;

import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.locks.blocks.BlockLock;
import gg.projecteden.nexus.models.locks.blocks.BlockLockService;
import gg.projecteden.nexus.models.locks.common.LockPermission;
import gg.projecteden.nexus.models.locks.common.LockType;
import gg.projecteden.nexus.models.locks.users.LockUser;
import gg.projecteden.nexus.models.locks.users.LockUserService;
import lombok.NonNull;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@Aliases("lock")
public class LocksCommand extends CustomCommand {
	private final BlockLockService blockService = new BlockLockService();
	private final LockUserService userService = new LockUserService();
	private LockUser user;

	public LocksCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			user = userService.get(player());
	}

	@Path("info")
	void info() {
		send(toPrettyString(getTargetLockRequired()));
	}

	@Path("settings defaultType <type>")
	void settings_defaultType(LockType type) {
		user.setDefaultLockType(type);
		userService.save(user);
		send(PREFIX + "Set default lock type to &e" + camelCase(type));
	}

	@Path("modify <type>")
	void modify(LockType type) {
		final BlockLock lock = getTargetModifiableLockRequired();

		lock.setLockType(type);
		blockService.save(lock);
		send(PREFIX + "Lock type set to &e" + camelCase(type));
	}

	@Path("password set <password>")
	void password_set(String password) {
		final BlockLock lock = getTargetModifiableLockRequired();

		if (lock.getLockType() != LockType.PASSWORD)
			error("Lock type must be password");

		if (!lock.getPassword().equals(password))
			lock.getPermissions().clear();

		lock.setPassword(password);
		blockService.save(lock);
		send(PREFIX + "Lock password set to &e" + password);
	}

	@Path("password remove")
	void password_remove() {
		final BlockLock lock = getTargetModifiableLockRequired();

		if (lock.getLockType() != LockType.PASSWORD)
			error("Lock type must be password");

		lock.setPassword(null);
		lock.getPermissions().clear();
		blockService.save(lock);

		send(PREFIX + "Password access removed");
	}

	@Path("password <password>")
	void password(String password) {
		final BlockLock lock = getTargetLockRequired();
		if (lock.getLockType() != LockType.PASSWORD || isNullOrEmpty(lock.getPassword()))
			error("Lock cannot be opened with a password");

		if (!lock.getPassword().equals(password))
			error("Incorrect password");

		lock.trust(uuid(), LockPermission.MEMBER);
		blockService.save(lock);
	}

	@NotNull
	private BlockLock getTargetLockRequired() {
		final Block block = getTargetBlockRequired();
		final BlockLock lock = blockService.get(block.getLocation());

		if (lock == null)
			error("That " + camelCase(block.getType()).toLowerCase() + " is not locked");

		return lock;
	}

	@NotNull
	private BlockLock getTargetModifiableLockRequired() {
		final BlockLock lock = getTargetLockRequired();
		verifyModification(lock);
		return lock;
	}

	private void verifyModification(BlockLock lock) {
		verifyModification(lock, uuid());
	}

	private void verifyModification(BlockLock lock, UUID uuid) {
		if (!lock.canBeModifiedBy(uuid))
			error("You do not have permission to modify that lock");
	}

}
