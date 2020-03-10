package guru.springframework.repositories.reactive;

import guru.springframework.domain.Category;
import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryTest {

    @Autowired
    CategoryReactiveRepository categoryReactiveRepository;

    @Before
    public void setUp() throws Exception {
        categoryReactiveRepository.deleteAll().block();
    }

    @Test
    public void testSave() throws Exception {
        //given
        Category category = new Category();
        category.setDescription("foo");

        categoryReactiveRepository.save(category).block();

        // when
        Long count = categoryReactiveRepository.findAll().count().block();

        // then
        assertEquals(count, Long.valueOf(1L));
    }

    @Test
    public void testFindByDescription() throws Exception {
        // given
        Category category = new Category();
        category.setDescription("foo");

        categoryReactiveRepository.save(category).block();

        // when
        Category foundCategory = categoryReactiveRepository.findByDescription("foo").block();

        assertEquals(category.getDescription(), "foo");
    }
}
