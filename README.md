
# Chat Distribuído com Sockets TCP e UDP

Este projeto é um sistema de **chat distribuído** desenvolvido em **Java**, utilizando **sockets TCP e UDP**. Ele foi construído como solução para um trabalho acadêmico da disciplina de Sistemas Distribuídos.

## 📦 Funcionalidades

### Via TCP:
- Conexão persistente entre cliente e servidor.
- Envio de mensagens públicas: `/all <mensagem>`
- Envio de mensagens privadas: `/msg <usuário> <mensagem>`
- Consulta de usuários conectados: `/list`

### Via UDP:
- Envio de mensagens rápidas para o servidor: `/udp <mensagem>`
- O servidor recebe e registra no log.
- **Não há resposta ou entrega a outros clientes.**

---

## 🧪 Execução

### 1. Compilação

```bash
javac -d out-manual src/common/*.java src/server/*.java src/client/*.java
```

### 2. Rodar o servidor

```bash
java -cp out-manual server.ChatServer
```

### 3. Rodar o cliente (em outro terminal ou máquina)

```bash
java -cp out-manual client.ChatClient
```

Digite seu nome de usuário ao iniciar.

---

## 🔁 Comandos disponíveis no cliente

```txt
/all <mensagem>          Envia para todos os usuários conectados
/msg <usuário> <mensagem> Envia mensagem privada
/list                    Lista todos os usuários conectados
/udp <mensagem>          Envia mensagem via UDP ao servidor (apenas loga)
```

---

## 🌐 Rodando em máquinas diferentes

1. Certifique-se de que as máquinas estão na mesma rede.
2. No `ChatClient.java`, altere:
```java
private static final String SERVER_HOST = "IP_DA_MAQUINA_SERVIDORA";
```
3. Libere as portas **12345 (TCP)** e **12346 (UDP)** no firewall da máquina do servidor.

---

## 📄 Logs

O servidor gera um log completo em `logs/server.log` com:

- Conexões e desconexões
- Mensagens públicas e privadas
- Mensagens UDP recebidas

---

## 📡 Captura de Pacotes (TCPDump / Wireshark)

Para capturar os pacotes (como exigido no trabalho):

### Usando `tcpdump` (Linux/macOS/WSL)

```bash
sudo tcpdump -i lo port 12345 or port 12346 -X -nn -v > capturas/tcpdump-output.txt
```

### Usando Wireshark (Windows)

1. Filtrar por: `tcp.port == 12345 || udp.port == 12346`
2. Capturar tráfego durante uso do chat.
3. Exportar: `File > Export Packet Dissections > As Plain Text`
4. Salvar em: `capturas/tcpdump-output.txt`

---

## 👨‍💻 Desenvolvido para:

**1º Trabalho de Sistemas Distribuídos - 2025**  
**Prof. Renato Bobsin Machado**  
Entrega: 06/06/2025
