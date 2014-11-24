#ifndef CONNECTION_H_
#define CONNECTION_H_

class Connection {
public:
	//Main constructor
	Connection(unsigned listenPort, unsigned sendPort);
	//Setters and Getters
	unsigned getListenPort(){return this->listenPort;}
	unsigned getSendPort(){return this->sendPort;}
	
private:
	unsigned listenPort, sendPort;

};

#endif

