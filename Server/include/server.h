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

void init_openssl();
void cleanup_openssl();
void shutdown_openssl(SSL *sslCmd, SSL *sslImg);
SSL_CTX *create_context();
void configure_context(SSL_CTX *ctx);

/**
 * @brief Pretty-print a human-readable error description for the TLS socket and return code from an SSL function call
 * @param prefix A character string prefix for the messages printed to the console, useful to determine from where the function was called
 * @param ssl The TLS socket for which a human-readable error description is to be printed
 * @param retcode The return code from the SSL function for which a human-readable error description is to be printed
 */
void print_ssl_err(char *prefix, SSL *ssl, int retcode);

/**
 * @brief Read from the given TLS socket until we come across a line-terminating character ('\n')
 * @param prefix A character string prefix for the messages printed to the console, useful to determine from where the function was called
 * @param ssl An open TLS socket from which the next message will be read
 * @param buffer A character buffer used to receive from the socket
 * @param dest A character buffer to which the received message will be copied, up to and not including the line-terminating character
 * @param buffer_size The size of the given character buffer
 * @return The total number of bytes that were received, -1 if there was an error
 */
int read_msg(char *prefix, SSL *ssl, char *buffer, char *dest, size_t buffer_size);

/**
 * @brief Send a message through the given TLS socket
 * @param prefix A character string prefix for the messages printed to the console, useful to determine from where the function was called
 * @param ssl An open TLS socket through which the message will be sent
 * @param msg A character string containing the message to be sent
 * @param msg_len The length in bytes of the message to be sent
 * @param log_enable Enable verbose logging. if set to 1, all the bytes that are sent will also be printed to the standard output
 * @return The number of bytes actually sent. A value less than 0 indicates an error.
 */
unsigned int send_msg(char *prefix, SSL *ssl, char *msg, size_t msg_len, short log_enable);

/**
 * @brief Prepare a response so that it conforms to the RoboPi protocol, mainly so that the response ends with the correct line-terminating character
 * @param response The response to be prepared
 * @return The total number of bytes of the response (including the newly added line-terminating character)
 */
unsigned int prepare_response(char *response);

/**
 * @brief The thread that is started each time a new client connects to the server
 * @param sockfd
 * @return
 */
void *session_task(void *ssl);

/**
 * @brief Starts the RoboPi server
 * @return should not return anything
 */
int server();

#endif
