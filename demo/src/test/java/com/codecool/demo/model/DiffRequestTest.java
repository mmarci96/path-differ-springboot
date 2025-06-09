package com.codecool.demo.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

class DiffRequestTest {

    @Test
    void testConstructorSetsFieldsCorrectly() {
        LocalFile a = new LocalFile();
        a.setName("A.java");

        LocalFile b = new LocalFile();
        b.setName("B.java");

        String user = "testuser";
        DiffRequest request = new DiffRequest(user, a, b);

        assertEquals(user, request.getUsername());
        assertEquals(a, request.getLocalFileA());
        assertEquals(b, request.getLocalFileB());
        assertNotNull(request.getCreatedAt());
        assertTrue(request.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void testNothing() {
        LocalFile a = new LocalFile();
        a.setName("A.java");

        LocalFile b = new LocalFile();
        b.setName("B.java");

        String user = "testuser";
        DiffRequest request = new DiffRequest(user, a, b);
        assertNotNull(request.getCreatedAt());
    }
}
