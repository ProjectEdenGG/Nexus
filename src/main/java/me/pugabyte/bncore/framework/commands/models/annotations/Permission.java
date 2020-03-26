package me.pugabyte.bncore.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	String value();

	boolean absolute() default false;
}

/*
	Groups:
		group.admin			- admin+
		group.seniorstaff	- operator+
		group.moderator		- moderator+
		group.builder		- builder/architect
		group.staff			- staff + builders

	Rank Exclusive:
		rank.<rankname>

	Ex: ladder.trusted = trusted and above
	Rank Ladder:
		ladder.guest 		- guest
		ladder.member		- member+
		ladder.trusted		- trusted+
		ladder.elite		- elite+
		ladder.veteran		- veteran+

		ladder.builder		- builder & veteran+
		ladder.architect	- architect+

		ladder.moderator	- moderator & veteran+

		ladder.operator		- operator & architect+ & moderator+
		ladder.admin		- admin+
		ladder.owner		- owner+

 */
