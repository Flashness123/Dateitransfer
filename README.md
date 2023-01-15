# Dateitransfer
Beleg UDP-Dateitransfer für Rechnernetze/Kommunikationssysteme

Die Aufgabenstellung besteht aus den Teilen:
* [Aufgabenstellung](Beleg-Aufgabenstellung.md)
* [Protokoll](Beleg-Protokoll.md)
* [Vorgehen](Beleg-Vorgehen.md)
* [Testverfahren](Beleg-Testverfahren.md)
* [Abgabeformat](Beleg-Abgabeformat.md)
* [Git/Github-Nutzung](https://github.com/HTWDD-RN/RTSP-Streaming/blob/master/git.md).

## Testszenarien

### Funktion des Clients + Servers ohne Fehlersimulation

#### Client

    Client started for connection to: localhost at port 42069
    Protokoll: sw
    beleg.Channel: loss: 0.0 delay: 0 ms
    C: Rcv Buffer Size: 65536
    sending: [-123, -15, 0, 83, 116, 97, 114, 116, 0, 0, 0, 0, 0, 0, 23, 21, 0, 9, 52, 57, 50, 50, 66, 46, 115, 113, 108, -60, 52, 112, -14]
    received: [-123, -15, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
    calculated crc: [-107, -128, 69, 88]
    received crc: [38, 119, -35, 44]
    Client-AW: Abort because of maximum retransmission

Process finished with exit code 1

#### Server

    Server started at port: 42069
    Rcv Buffer Size: 65536
    beleg.Channel: loss: 0.0 delay: 0 ms
    filelength: 5909	allocated: 7389
    Server-AW: file received

### Funktion des Clients + Servers mit Fehlersimulation

#### Client
    
    Client started for connection to: localhost at port 42069
    Protokoll: sw
    beleg.Channel: loss: 0.0 delay: 0 ms
    C: Rcv Buffer Size: 65536
    calculated crc: [-107, -128, 69, 88]
    received crc: [38, 119, -35, 44]
    Client-AW: Abort because of maximum retransmission

    Process finished with exit code 1

### Server

    Server started at port: 42069
    Rcv Buffer Size: 65536
    beleg.Channel: loss: 0.1 delay: 100 ms
    filelength: 5909	allocated: 7389
    Server-AW: file received

### Funktion des Clients + Servers über Hochschulproxy
    dasselbige? Hochschulprxy?

### Funktion des Clients + Hochschulservers mit Fehlersimulation

#### Client

    Client started for connection to: idefix.informatik.htw-dresden.de at port 3333
    Protokoll: sw
    beleg.Channel: loss: 0.0 delay: 0 ms
    C: Rcv Buffer Size: 65536

### Server

    ALL	FileTransfer	Sun Jan 15 13:13:27 CET 2023 ******** Server v2.12: Waiting for new connection... ************
    FINEST	SW	waiting for packet nr.: 0
    FINEST	SW	Paket received, nr: 0
    ALL	SW	Start packet with: Start
    INFO	SW	Start received
    ALL	SW	IP / Port: /141.56.63.10 60849
    INFO	SW	new session ID: 3384
    FINEST	SW	send ACK Nr: 0
    FINE	FileTransfer	Check Start packet: length: 26
    FINE	FileTransfer	Length: 5 Bytes, Filename: 5B.test
    FINE	FileTransfer	CRC Packet:9fe8f730 CRC field:9fe8f730
    ALL	FileTransfer	Correct Start packet received
    FINEST	SW	waiting for packet nr.: 1
    FINEST	SW	Paket received, nr: 1
    FINEST	SW	send ACK Nr: 1
    FINE	FileTransfer	Total 1480 Bytes received...
    FINEST	SW	waiting for packet nr.: 2
    FINEST	SW	Paket received, nr: 2
    FINEST	SW	send ACK Nr: 2
    INFO	FileTransfer	All data received
    INFO	FileTransfer	CRC (received data): d6622193
    INFO	FileTransfer	CRC (source field) : d9a3b9eb
    WARNING	FileTransfer	CRC not OK !
    ALL	FileTransfer	Transmission time: 0.0s, data rate: Infinity kbit/s
    INFO	SW	waiting for repeated packets because of lost ACK
    FINEST	SW	waiting for packet nr.: 3
    FINER	SW	Data packet receive timed out
    INFO	SW	Finish waiting for packets...
    INFO	FileCopy	file received correctly