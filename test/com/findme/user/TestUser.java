package com.findme.user;

import models.User;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestUser {

    @Test
    public final void  testEquals(){
        User u1 = new User("Samo Lo");
        User u2 = new User("Samo Lo");
        assertEquals(u1,u2);
    }
}
