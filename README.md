# 🧹 MuLog - Limpeza Automática de Logs em JARs Java

![Java](https://img.shields.io/badge/Java-8+-red?logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-blue)
![Javassist](https://img.shields.io/badge/Javassist-Bytecode%20Editing-lightgrey)

**MuLog** é uma ferramenta desktop leve e eficiente que **remove automaticamente instruções de log** (como `System.out.println`, `System.err.println` e `printStackTrace()`) de arquivos `.jar`. Ela gera uma versão **limpa para produção**, mantendo uma cópia **completa para desenvolvimento**.

Criada para desenvolvedores Java que enfrentam retrabalho com a limpeza de logs, a ferramenta visa melhorar o desempenho, a organização e a segurança dos sistemas.

---

## ✨ Funcionalidades Principais

- 🔍 **Detecção automática** de logs no bytecode de arquivos `.jar`.
- 🧼 **Remoção precisa** de:
  - `System.out.println`
  - `System.err.println`
  - `printStackTrace()`
- 🗂️ **Geração de duas versões** do JAR:
  - ✔️ Produção (sem logs)
  - 🧪 Desenvolvimento (com logs)
- 💻 Interface gráfica moderna feita com JavaFX (modo claro e escuro).
- 📁 Suporte à adição de **dependências externas (JARs)**.

---

## 🚀 Como Usar

### Pré-requisitos

🖥️ Nenhum! Basta ter um sistema operacional de desktop (Windows) e **extrair o `.zip`**.

### Passos:

1. 🔽 [Clique aqui para baixar a versão mais recente](https://github.com/ViniciusEdvaldo/MuLog/releases/tag/1.0)
2. Extraia o conteúdo do `.zip` baixado.
3. Dê um duplo clique no arquivo `ExcluirLogV2.exe`.
4. Selecione o JAR desejado e escolha os tipos de logs que deseja remover.
5. Pronto! Você terá duas versões: uma com e outra sem logs.

---

## ⚙️ Tecnologias Utilizadas

| Tecnologia    | Descrição                              |
|---------------|----------------------------------------|
| ☕ Java 8+     | Linguagem principal                    |
| 📦 Javassist  | Manipulação de bytecode (remoção de logs) |
| 🎨 JavaFX     | Interface gráfica rica e responsiva     |
| 📋 Google Forms | Pesquisa com desenvolvedores para validação |

---

## ✅ Benefícios

- ⏱️ Economia de tempo com limpeza automatizada
- 📉 Redução do tamanho dos arquivos `.jar` em produção
- 🔒 Eliminação de possíveis vazamentos de informação por logs esquecidos
- 🧹 Código de produção mais limpo e organizado
- 🧾 Facilidade para auditoria e controle de qualidade
- 💡 Ideal para projetos legados com muitos logs

---

## 📄 Licença

Este projeto está licenciado sob a **MIT License**.  
Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 🙋‍♂️ Autor

**Bruno Henrique Cachali**  
📧 *Insira aqui seu e-mail ou LinkedIn*

---

