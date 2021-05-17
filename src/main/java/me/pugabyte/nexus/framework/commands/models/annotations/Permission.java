package me.pugabyte.nexus.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Sets the permission node required to run a command
 * <p>
 * View the bottom of the source code to this annotation for a common permission node cheatsheet
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	/**
	 * The permission node required to run this command
	 * <p>
	 * View the bottom of the source code to this annotation for a common permission node cheatsheet
	 * @return a permission node
	 */
	String value();

	/**
	 * For sub-commands, this specifies whether or not the permission node should derive from the parent command's
	 * permission node.
	 * <p>
	 * Example: if a parent has the permission node <i><code>essentials.signs</code></i> and a child has the permission
	 * node <i><code>color</code></i>, the effective permission node of the child if this is <code>false</code> will be
	 * <i><code>essentials.signs.color</code></i>. If this is <code>true</code>, it would be <i><code>color</code></i>.
	 * @return if this permission node is absolute
	 */
	boolean absolute() default false;
}

/*
	Groups:
		group.admin         - admin+
		group.seniorstaff   - operator+
		group.moderator	    - mod+
		group.builder       - builder/architect
		group.staff         - staff + builders
		group.nonstaff      - guest - veteran

	Ladder: test if someone has a rank or greater, according to the below hierarchy
	Rank Exclusive: test if someone has specifically the rank <rankname> with rank.<rankname>

	Ex:
		ladder.trusted = trusted and above
		ladder.builder = builder, architect, operator, admin, owner
		rank.trusted = only trusted, not elite or above
	Rank Ladder:
           ladder.owner
           ladder.admin
           ladder.operator
             /        \
  ladder.moderator   ladder.architect
            |        ladder.builder
             \        /
           ladder.noble
           ladder.veteran
           ladder.elite
           ladder.trusted
           ladder.member
           ladder.guest
 */
