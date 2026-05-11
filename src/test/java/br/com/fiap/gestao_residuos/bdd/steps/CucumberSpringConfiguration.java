package br.com.fiap.gestao_residuos.bdd.steps;

import br.com.fiap.gestao_residuos.config.JacksonConfig;
import br.com.fiap.gestao_residuos.controller.ColetaController;
import br.com.fiap.gestao_residuos.controller.ContenedorController;
import br.com.fiap.gestao_residuos.controller.DestinacaoController;
import br.com.fiap.gestao_residuos.controller.LeituraController;
import br.com.fiap.gestao_residuos.controller.RotaController;
import br.com.fiap.gestao_residuos.service.ColetaService;
import br.com.fiap.gestao_residuos.service.ContenedorService;
import br.com.fiap.gestao_residuos.service.DestinacaoService;
import br.com.fiap.gestao_residuos.service.LeituraService;
import br.com.fiap.gestao_residuos.service.RotaService;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@WebMvcTest(controllers = {
        ColetaController.class,
        ContenedorController.class,
        RotaController.class,
        LeituraController.class,
        DestinacaoController.class
})
@Import(JacksonConfig.class)
public class CucumberSpringConfiguration {

    @MockitoBean
    public ColetaService coletaService;

    @MockitoBean
    public ContenedorService contenedorService;

    @MockitoBean
    public RotaService rotaService;

    @MockitoBean
    public LeituraService leituraService;

    @MockitoBean
    public DestinacaoService destinacaoService;
}
