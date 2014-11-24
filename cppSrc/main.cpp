#include <iostream>
#include "connection.h"
using namespace std;

int main() {
	cout << "Hello World!\n";
	cout << "This means that the Makefile works!\n";
	Connection connect1(1,2);
	cout << "Sending Port: " << connect1.getSendPort() << " Listening Port: " << connect1.getListenPort();
	return 0;
}
