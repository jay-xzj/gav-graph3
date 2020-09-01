package uk.ac.newcastle.redhat.gavgraph;

import org.junit.After;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;


@SpringBootTest
@WebAppConfiguration
class GavGraphApplicationTests {

    @Before
    public void init() {
        System.out.println("Test begins-----------------");
    }

    @After
    public void after() {
        System.out.println("Test ends-----------------");
    }
}
