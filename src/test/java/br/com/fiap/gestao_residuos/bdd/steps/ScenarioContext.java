package br.com.fiap.gestao_residuos.bdd.steps;

import io.cucumber.spring.ScenarioScope;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import org.springframework.stereotype.Component;

@Component
@ScenarioScope
public class ScenarioContext {

    private MockMvcResponse lastResponse;

    public MockMvcResponse getLastResponse() {
        return lastResponse;
    }

    public void setLastResponse(MockMvcResponse lastResponse) {
        this.lastResponse = lastResponse;
    }
}
