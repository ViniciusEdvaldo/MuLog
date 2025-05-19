# 🧹 MuLog - Limpeza Automática de Logs em JARs Java

![Java](https://img.shields.io/badge/Java-8+-red?logo=java)
![JavaFX](https://img.shields.io/badge/JavaFX-UI-blue)
![Javassist](https://img.shields.io/badge/Javassist-Bytecode%20Editing-lightgrey)

MuLog é uma ferramenta desktop que **remove automaticamente instruções de log** (como `System.out.println`) de arquivos `.jar`, gerando uma versão **limpa para produção** e mantendo uma versão **completa para desenvolvimento**. Foi criada para evitar o retrabalho manual de apagar logs e melhorar a segurança, desempenho e organização dos sistemas Java.

---

## ✨ Principais Funcionalidades

- 🔍 **Detecção automática** de logs em bytecode Java.
- 🧼 **Remoção precisa** de `System.out.println`, `System.err.println`, `printStackTrace()` e outros logs.
- 🗂️ **Geração de duas versões**:
  - Produção (sem logs)
  - Desenvolvimento (com logs)
- 💻 Interface gráfica moderna (JavaFX) com modo claro/escuro.
- 📁 Suporte a dependências externas (JARs adicionais).

---


## 🚀 Como Executar

### Pré-requisitos

- Nenhum, qualquer Desktop podera rodar a aplicação
  
## 📦 Download

🔽 [Clique aqui para baixar a versão mais recente](https://github.com/ViniciusEdvaldo/MuLog/releases/tag/1.0)


⚙️ Tecnologias Utilizadas

☕ Java 8 — Linguagem principal

📦 Javassist — Manipulação de bytecode

🎨 JavaFX — Interface gráfica

📋 Google Forms — Coleta e validação com desenvolvedores reais

✅ Benefícios
⏱️ Economia de tempo na entrega

📉 Redução de tamanho dos JARs

🔒 Maior segurança no ambiente produtivo

📊 Facilidade para auditoria e controle de qualidade

