# language: pt
Funcionalidade: Gestão de Rotas de Coleta de Resíduos
  Como gestor de logística de resíduos
  Quero gerenciar as rotas de coleta
  Para otimizar a eficiência energética e reduzir emissões de carbono

  Cenário: Criar uma rota de coleta com sucesso
    Dado que eu tenho dados válidos de uma rota com veículo "Caminhão 01" e capacidade 2000.0
    Quando eu envio uma requisição POST para "/api/rota" com os dados da rota
    Então o status de resposta da rota deve ser 201
    E o corpo da resposta da rota deve conter o campo "id"
    E o corpo da resposta da rota deve conter o campo "veiculo" com valor "Caminhão 01"

  Cenário: Listar todas as rotas cadastradas
    Dado que existem rotas cadastradas no sistema
    Quando eu envio uma requisição GET para "/api/rotas"
    Então o status de resposta da rota deve ser 200
    E o corpo da resposta da rota deve ser uma lista JSON

  Cenário: Buscar rota por ID inexistente retorna 404
    Dado que nenhuma rota existe com o ID 99999
    Quando eu envio uma requisição GET para "/api/rota/99999"
    Então o status de resposta da rota deve ser 404
    E o corpo da resposta da rota deve conter o campo "error" com valor "Recurso não encontrado"

  Cenário: Atualizar uma rota existente
    Dado que existe uma rota com ID 1 cadastrada no sistema
    Quando eu envio uma requisição PUT para "/api/rota/1" com novo veículo "Caminhão 02"
    Então o status de resposta da rota deve ser 200
    E o corpo da resposta da rota deve conter o campo "veiculo" com valor "Caminhão 02"

  Cenário: Excluir uma rota inexistente retorna 404
    Dado que nenhuma rota existe com o ID 99999
    Quando eu envio uma requisição DELETE para "/api/rota/99999"
    Então o status de resposta da rota deve ser 404
