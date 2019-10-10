package com.jairaviles.mx.persistence.dao;

import com.jairaviles.mx.persistence.entities.Officer;
import com.jairaviles.mx.persistence.entities.Rank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsInAnyOrder;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Transactional
public class JdbcOfficerDAOTest {
    @Autowired
    private OfficerDAO dao;

    @Test
    public void save() {
        Officer officer = new Officer(Rank.ENSIGN, "Pavel", "Pardo");
        officer = dao.save(officer);
        assertNotNull(officer.getId());
    }

    @Test
    public void findByIdThatExists() {
        Optional<Officer> officer = dao.findById(1);
        assertTrue(officer.isPresent());
        assertEquals(1, officer.get().getId().intValue());
    }

    @Test
    public void findByIdThatDoesNotExists() {
        Optional<Officer> officer = dao.findById(999);
        assertFalse(officer.isPresent());
    }

    @Test
    public void count() {
        assertEquals(5, dao.count());
    }

    @Test
    public void findAll() {
        List<String> dbNames = dao.findAll().stream()
                                    .map(Officer::getLast)
                                    .collect(Collectors.toList());
        assertThat(dbNames, containsInAnyOrder("Aviles", "Perez", "Patiño", "Castro", "Ramos"));
    }

    @Test
    public void delete() {
        IntStream.rangeClosed(1, 5)
                .forEach(id -> {
                    Optional<Officer> officer = dao.findById(id);
                    assertTrue(officer.isPresent());
                    dao.delete(officer.get());
                });
        assertEquals(0, dao.count());
    }

    @Test
    public void existsById() {
        IntStream.rangeClosed(1, 5)
                .forEach(id -> assertTrue(dao.existsById(id)));
    }
}
