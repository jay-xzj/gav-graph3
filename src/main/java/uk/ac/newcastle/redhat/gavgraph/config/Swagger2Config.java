package uk.ac.newcastle.redhat.gavgraph.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Parameter;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Bean
    public Docket createRestApi() {
        List<ResponseMessage> GETResponseMessageList = new ArrayList<>();
        GETResponseMessageList.add(new ResponseMessageBuilder().code(409).message("Conflict").build());
        GETResponseMessageList.add(new ResponseMessageBuilder().code(422).message("Unprocessable Entity").build());
        GETResponseMessageList.add(new ResponseMessageBuilder().code(500).message("Internal Server Error").build());
        GETResponseMessageList.add(new ResponseMessageBuilder().code(503).message("Service Unavailable").build());

        List<Parameter> pars = new ArrayList<Parameter>();
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.any())
                .build()
                .globalOperationParameters(pars)
                .apiInfo(apiInfo());
                //.globalResponseMessage(RequestMethod.GET, GETResponseMessageList);
    }
 
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("GAVGraph RESTFul API")
                .description("GAV-Graph")
                .version("1.0")
                .build();
    }
 
}