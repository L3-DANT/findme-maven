package com.findme.test.user;

import models.User;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestUser {

    @Test
    public final void  testEquals(){
        User u1 = new User("Samo Lo","123");
        User u2 = new User("Samo Lo","123");
        assertEquals(u1,u2);
        User u3 = new User("Bob","123");
        assertFalse(u1.equals(u3));
        u1.setPseudo("Bob");
        assertEquals(u1,u3);
    }
}
