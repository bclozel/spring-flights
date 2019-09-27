package io.spring.sample.flighttracker;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Rob Winch
 */
@Retention(RetentionPolicy.RUNTIME)
@AuthenticationPrincipal(expression = "claims['preferred_username']")
public @interface CurrentUserLogin {
}
