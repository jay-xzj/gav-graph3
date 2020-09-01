package uk.ac.newcastle.redhat.gavgraph;

import org.junit.After;
import org.junit.Before;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;


//@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
class GavGraphApplicationTests {
/*
    @Autowired
    private ArtifactController artifactController;

    @Test
    void contextLoads() {
        assertThat(artifactController).isNotNull();
    }*/

    @Before
    public void init() {
        System.out.println("开始测试-----------------");
    }

    @After
    public void after() {
        System.out.println("测试结束-----------------");
    }
}
