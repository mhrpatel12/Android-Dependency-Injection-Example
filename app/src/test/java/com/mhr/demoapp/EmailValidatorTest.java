package com.mhr.demoapp;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Created by patelmih on 9/6/2017.
 */

public class EmailValidatorTest {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Test
    public void emailValidator_CorrectEmailSimple_ReturnsTrue() {

        assertThat(isValidEmail("name@email.com"), is(true));
    }

    public boolean isValidEmail(String email) {
        return VALID_EMAIL_ADDRESS_REGEX.matcher(email).find();
    }
}
