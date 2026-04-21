## CI/CD com GitHub Actions

Este projeto utiliza **GitHub Actions** para automatizar o processo de integração contínua e entrega contínua (CI/CD).

### Pipeline

O pipeline é executado automaticamente a cada *push* na branch `main`/`master` e realiza as seguintes etapas:

* **Build da aplicação**

  * Compilação do projeto utilizando Maven Wrapper (`mvnw`)
* **Testes**

  * (Opcionalmente ignorados para evitar falhas por dependências externas)
* **Deploy em Staging (simulado)**

  * Execução de etapa simulada para ambiente de homologação
* **Deploy em Produção (simulado)**

  * Execução de etapa simulada para ambiente de produção

### Estrutura

O pipeline está definido no arquivo:

```
.github/workflows/pipeline.yml
```

### Tecnologias utilizadas

* Java 17
* Spring Boot
* Maven Wrapper
* GitHub Actions

### Como funciona

Sempre que um novo código é enviado para o repositório:

1. O GitHub Actions inicia automaticamente o pipeline
2. O projeto é compilado
3. As etapas de deploy são executadas (simuladas)
4. O status da execução pode ser acompanhado na aba **Actions** do repositório

### Execução

![Pipeline](docs/images/workflow%20em%20andamento.png)

### Status esperado

![Sucesso](docs/images/workflow%20com%20sucesso.png)

* ✔ Build concluído com sucesso
* ✔ Pipeline executado automaticamente
* ✔ Todas as etapas finalizadas sem erro
