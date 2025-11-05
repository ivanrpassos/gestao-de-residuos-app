package br.com.fiap.gestao_residuos.config;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public com.fasterxml.jackson.databind.Module hibernateModule() {
        return new Hibernate6Module();
    }

}

