package br.com.fiap.gestao_residuos;


import br.com.fiap.gestao_residuos.controller.*;
import br.com.fiap.gestao_residuos.service.*;
import br.com.fiap.gestao_residuos.repository.*;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Suite Completa — Gestão de Resíduos (Controller + Service + Repository)")
@SelectClasses({
        ColetaControllerTest.class,
        ContenedorControllerTest.class,
        DestinacaoControllerTest.class,
        LeituraControllerTest.class,
        RotaControllerTest.class,
        ColetaServiceTest.class,
        ContenedorServiceTest.class,
        DestinacaoServiceTest.class,
        LeituraServiceTest.class,
        RotaServiceTest.class,
        ColetaRepositoryTest.class,
        ContenedorRepositoryTest.class,
        DestinacaoRepositoryTest.class,
        LeituraRepositoryTest.class,
        RotaRepositoryTest.class
})
class AppTests {
    // Classe de entrada única para execução de todos os testes unitários.
    // Basta clicar com o botão direito → "Run AppTests" na IDE,
    // ou executar: ./mvnw test -Dtest=AppTests
}
