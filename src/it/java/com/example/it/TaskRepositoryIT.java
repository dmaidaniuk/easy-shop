package com.example.it;

import com.example.domain.Task;
import com.example.domain.TaskRepository;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(ArquillianExtension.class)
public class TaskRepositoryIT {
    private static final Logger LOGGER = Logger.getLogger(TaskRepositoryIT.class.getName());

    @Deployment()
    public static JavaArchive createDeployment() {
        JavaArchive jar = ShrinkWrap.create(JavaArchive.class)
                .addPackage(Task.class.getPackage())
                //Add JPA persistence configuration.
                //WARN: In a jar archive, persistence.xml should be put into /META-INF
                .addAsManifestResource("META-INF/persistence.xml", "persistence.xml");
        jar.as(ZipExporter.class).exportTo(new File("./target/easy-shop.jar"), true);

        LOGGER.log(Level.INFO, "Deployment unit: {0}", jar.toString(true));

        return jar;
    }

    @Inject
    TaskRepository tasks;

    @PersistenceContext
    EntityManager em;

    Task saved;

    @BeforeEach
    public void setup() {
        saved = tasks.save(Task.of("test task", "desc of test task"));
    }

    @AfterEach
    public void teardown() {
    }

    @Test
    public void shouldCreated() {
        Task found = em.find(Task.class, saved.getId());
        assertEquals("test task", found.getName());
        assertEquals("desc of test task", found.getDescription());
    }
}
