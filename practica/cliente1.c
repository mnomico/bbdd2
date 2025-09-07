#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include <libssh2.h>
#include <netdb.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>

#include "cliente1.h"

int crearSocket(const char *, int);

int main(int argc, char **argv)
{
    const char *hostname = HOSTNAME;
    const char *username = USERNAME;
    const char *password = PASSWORD;
    const char *localhost = LOCALHOST;
    int localport = LOCALPORT;
    const char *remotehost = REMOTEHOST;
    int remoteport = REMOTEPORT;
    int remoteportssh = REMOTEPORTSSH;

    // Configuración del socket origen y destino para la comunicación
    int sock = crearSocket(remotehost, remoteport);

} 

int crearSocket(const char *remotehost, int remoteport)
{
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    struct sockaddr_in sin;
    sin.sin_family = AF_INET;
    sin.sin_port = htons(remoteport);
    sin.sin_addr.s_addr = inet_addr(remotehost);
    if (connect(sock, (struct sockaddr*) (&sin), sizeof(struct sockaddr_in)) != 0)
    {
        printf("Error en connect()\n");
        return -1;
    } 
    printf("connect() realizado con éxito\n" ); 
    return sock;
}