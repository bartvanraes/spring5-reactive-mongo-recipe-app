package guru.springframework.repositories.reactive;

import guru.springframework.domain.UnitOfMeasure;
import guru.springframework.repositories.UnitOfMeasureRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureRepositoryTest {

    @Autowired
    UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    @Before
    public void setUp() throws Exception {
        unitOfMeasureReactiveRepository.deleteAll().block();
    }

    @Test
    public void testSave() throws  Exception {
        // given
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setDescription("foo");

        unitOfMeasureReactiveRepository.save(uom).block();

        // when
        Long count = unitOfMeasureReactiveRepository.findAll().count().block();

        // then
        assertEquals(count, Long.valueOf(1L));
    }

    @Test
    public void testFindByDescription() throws Exception {
        // given
        UnitOfMeasure uom = new UnitOfMeasure();
        uom.setDescription("foo");

        unitOfMeasureReactiveRepository.save(uom).block();

        // when
        UnitOfMeasure foundUom = unitOfMeasureReactiveRepository.findByDescription("foo").block();

        assertEquals(foundUom.getDescription(), "foo");
    }
}
