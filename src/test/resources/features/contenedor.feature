# language: pt
Funcionalidade: Gestão de Contenedores de Resíduos
  Como gestor do sistema de gestão de resíduos
  Quero gerenciar os contenedores de coleta
  Para garantir a eficiência na reciclagem e redução de impacto ambiental

  Cenário: Criar um novo contenedor com sucesso
    Dado que eu tenho dados válidos de um contenedor com localização "Av. Paulista, 1000", capacidade 500.0 e material "PLASTICO"
    Quando eu envio uma requisição POST para "/api/contenedor" com os dados do contenedor
    Então o status de resposta deve ser 201
    E o corpo da resposta deve conter o campo "id"
    E o corpo da resposta deve conter o campo "localizacao" com valor "Av. Paulista, 1000"

  Cenário: Listar todos os contenedores cadastrados
    Dado que existem contenedores cadastrados no sistema
    Quando eu envio uma requisição GET para "/api/contenedores"
    Então o status de resposta deve ser 200
    E o corpo da resposta deve ser uma lista JSON

  Cenário: Buscar contenedor por ID inexistente retorna 404
    Dado que nenhum contenedor existe com o ID 99999
    Quando eu envio uma requisição GET para "/api/contenedor/99999"
    Então o status de resposta deve ser 404
    E o corpo da resposta deve conter o campo "error" com valor "Recurso não encontrado"

  Cenário: Atualizar um contenedor existente com sucesso
    Dado que existe um contenedor com ID 1 cadastrado no sistema
    Quando eu envio uma requisição PUT para "/api/contenedor/1" com nova localização "Rua Nova, 200"
    Então o status de resposta deve ser 200
    E o corpo da resposta deve conter o campo "localizacao" com valor "Rua Nova, 200"

  Cenário: Excluir um contenedor inexistente retorna 404
    Dado que nenhum contenedor existe com o ID 99999
    Quando eu envio uma requisição DELETE para "/api/contenedor/99999"
    Então o status de resposta deve ser 404
