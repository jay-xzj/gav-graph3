package uk.ac.newcastle.redhat.gavgraph;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
//@EnableNeo4jRepositories//解决说找不到ArtifactRepository bean的问题; 或者在MyConfiguration里面配置repository文件路径
@SpringBootApplication(scanBasePackages = "uk.ac.newcastle.redhat.gavgraph",exclude = DataSourceAutoConfiguration.class)
public class GavGraphApplication {

    public static void main(String[] args) {
        SpringApplication.run(GavGraphApplication.class, args);
    }

}
