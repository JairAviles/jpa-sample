package com.jairaviles.mx.persistence;

import com.jairaviles.mx.persistence.dao.OfficerRepository;
import com.jairaviles.mx.persistence.entities.Officer;
import com.jairaviles.mx.persistence.entities.Rank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"SqlNoDataSourceInspection", "SqlResolve"})
@SpringBootTest
@Transactional
@ExtendWith(SpringExtension.class)
public class OfficerRepositoryTest {
    @Autowired
    private OfficerRepository repository;

    @Autowired
    private JdbcTemplate template;

    private RowMapper<Integer> idMapper = (rs, num) -> rs.getInt("id");

    @Test
    public void testSave() {
        Officer officer = new Officer(Rank.LIEUTENANT, "Nyota", "Uhuru");
        officer = repository.save(officer);
        assertNotNull(officer.getId());
    }

    @Test
    public void findOneThatExists() {
        template.query("select id from officers", idMapper)
                .forEach(id -> {
                    Optional<Officer> officer = repository.findById(id);
                    assertTrue(officer.isPresent());
                    assertEquals(id, officer.get().getId());
                });
    }

    @Test
    public void findOneThatDoesNotExists() {
        Optional<Officer> officer = repository.findById(999);
        assertFalse(officer.isPresent());
    }

    @Test
    public void findAll() {
        List<String> dbNames = repository.findAll().stream()
                .map(Officer::getLast)
                .collect(Collectors.toList());
        assertThat(dbNames,
                containsInAnyOrder("Aviles", "Perez", "Castro", "Patiño", "Ramos"));
    }

    @Test
    public void count() {
        assertEquals(5, repository.count());
    }

    @Test
    public void delete() {
        template.query("select id from officers", idMapper)
                .forEach(id -> {
                    Optional<Officer> officer = repository.findById(id);
                    assertTrue(officer.isPresent());
                    repository.delete(officer.get());
                });
        assertEquals(0, repository.count());
    }

    @Test
    public void existsById() {
        template.query("select id from officers", idMapper)
                .forEach(id -> assertTrue(repository.existsById(id)));
    }

    @Test
    public void doesNotExistsById() {
        List<Integer> ids = template.query("select id from officers", idMapper);
        assertThat(ids, not(contains(999)));
        assertFalse(repository.existsById(999));
    }

    @Test
    public void findByLast() {
        List<Officer> officers = repository.findByLast("Castro");
        assertEquals(1, officers.size());
        assertEquals("Castro", officers.get(0).getLast());
    }

    @Test
    public void findByRankAndLastLike() {
        List<Officer> officers = repository.findAllByRankAndLastLike(Rank.CAPTAIN, "%a%");
        System.out.println(officers);
        List<String> lastNames = officers.stream()
                .map(Officer::getLast)
                .collect(Collectors.toList());
        assertThat(lastNames, containsInAnyOrder("Castro", "Patiño", "Ramos"));
    }
}
