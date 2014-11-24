#include "connection.h"

Connection::Connection(unsigned listenPort, unsigned sendPort) {
	this->listenPort = listenPort;
	this->sendPort = sendPort;
}
