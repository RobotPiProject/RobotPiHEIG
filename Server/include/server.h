#ifndef SERVER_H
#define SERVER_H

#include <stdio.h>
#include <stdlib.h>
#include <errno.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <unistd.h>
#include <pthread.h>
#include <string.h>
#include <signal.h>
#include <libinetsocket.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <openssl/bio.h>

#define LISTENING_PORT "2025"
#define BUFFER_SIZE 32

int server();
void *session_task(void *sockfd);
unsigned int read_msg(char *prefix, SSL *ssl, char *buffer, char *dest, size_t buffer_size);
unsigned int send_msg(char *prefix, SSL *ssl, char *msg, size_t msg_len);
unsigned int prepare_response(char *response);
void configure_context(SSL_CTX *ctx);
SSL_CTX *create_context();

#endif
