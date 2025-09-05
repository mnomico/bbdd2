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

int main()
{
    const char *hostname = HOSTNAME;
    const char *username = USERNAME;
    const char *password = PASSWORD;
    const char *localhost = LOCALHOST;
    int localport = LOCALPORT;
    const char *remotehost = REMOTEHOST;
    int remoteport = REMOTEPORT;

    // Configuración del socket origen y destino para la comunicación
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
    printf("connect() realizado con éxito\n");

    // Iniciar la sesión con el socket definido para el origen
    LIBSSH2_SESSION *session = libssh2_session_init();
    if (libssh2_session_handshake(session, sock))
    {
        printf("Error en libssh2_session_init()\n");
        return -1;
    }
    printf("libssh2_session_init() realizado con éxito\n");

    // Autenticar la sesión SSH con usuario y contraseña para ingresar al sistema operativo.
    if (libssh2_userauth_password(session, username, password))
    {
        printf("Error en libssh2_userauth_password()\n");
        return -1;
    }
    printf("libssh2_userauth_password() realizado con éxito\n");

    // Establecer la conexión TCP/IP mediante SSH
    LIBSSH2_CHANNEL *channel = libssh2_channel_direct_tcpip_ex(session,
                                                               remotehost,
                                                               remoteport,
                                                               localhost,
                                                               localport);
    
    if (!channel)
    {
        printf("Error en libssh2_channel_direct_tcpip_ex()\n");
        return -1;
    }
    printf("libssh2_channel_direct_tcpip_ex() realizado con éxito\n");

    // TODO ejecutar el procedure en la base de datos
}