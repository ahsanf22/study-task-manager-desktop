package it.unifi.ast.studytaskmanager.coverage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks infrastructure or UI bootstrap code that is intentionally excluded
 * from coverage metrics because it is not business logic and is impractical
 * to exercise reliably in automated tests.
 */
@Retention(RetentionPolicy.CLASS)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR })
public @interface GeneratedCoverageExclusion {
}
