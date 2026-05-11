# language: pt
Funcionalidade: Agendamento e Gestão de Coletas de Resíduos
  Como operador do sistema de gestão de resíduos
  Quero agendar e gerenciar coletas de resíduos
  Para garantir a destinação correta dos materiais e conformidade ambiental

  Cenário: Agendar uma coleta com sucesso
    Dado que eu tenho dados válidos de uma coleta com status "AGENDADO"
    Quando eu envio uma requisição POST para "/api/coleta" com os dados da coleta
    Então o status de resposta da coleta deve ser 201
    E o corpo da resposta da coleta deve conter o campo "id"
    E o corpo da resposta da coleta deve conter o campo "status" com valor "AGENDADO"

  Cenário: Criar coleta sem status retorna erro de validação
    Dado que eu tenho dados de coleta sem o campo status
    Quando eu envio uma requisição POST para "/api/coleta" sem status
    Então o status de resposta da coleta deve ser 400
    E o corpo da resposta da coleta deve conter o campo "error" com valor "Dados inválidos"

  Cenário: Buscar coleta por ID inexistente retorna 404
    Dado que nenhuma coleta existe com o ID 99999
    Quando eu envio uma requisição GET para "/api/coleta/99999"
    Então o status de resposta da coleta deve ser 404
    E o corpo da resposta da coleta deve conter o campo "error" com valor "Recurso não encontrado"

  Cenário: Listar todas as coletas agendadas
    Dado que existem coletas cadastradas no sistema
    Quando eu envio uma requisição GET para "/api/coletas"
    Então o status de resposta da coleta deve ser 200
    E o corpo da resposta da coleta deve ser uma lista JSON

  Cenário: Excluir uma coleta inexistente retorna 404
    Dado que nenhuma coleta existe com o ID 99999
    Quando eu envio uma requisição DELETE para "/api/coleta/99999"
    Então o status de resposta da coleta deve ser 404
