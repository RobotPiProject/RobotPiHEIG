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

#define LISTENING_PORT "2025"
#define MAX_TCP_SESSIONS 4
#define BUFFER_SIZE 32

int server();
void *session_task(void *ptr);

#endif