#ifndef PROTOCOL_H
#define PROTOCOL_H

#include <stdlib.h>
#include <string.h>
#include <errno.h>
#include <stdio.h>
#include <libinetsocket.h>
#include <pthread.h>
#include <server.h>
#include <openssl/ssl.h>
#include <openssl/err.h>
#include <openssl/bio.h>

#define CONN_OK 42
#define CONN_ERR 43
#define DISCONN_OK 44
#define DISCONN_ERR 45
#define FWD_OK 46
#define STOP_OK 47
#define BKWD_OK 48
#define ROTATE_LEFT_OK 49
#define ROTATE_RIGHT_OK 50
#define FRONT_L_OK 51
#define FRONT_R_OK 52
#define BCK_L_OK 53
#define BCK_R_OK 54
#define CMD_ERR 55
#define PING 56
#define PICTURE_OK 57
#define PICTURE_ERR 58

#define CMD_LEN 16

#define LISTENING_IMG_PORT "2026"

/**
 * @brief Shut down the given socket using the shutdown() function from the glibc socket library
 * @param prefix prefix A character string prefix for the messages printed to the console, useful to determine from where the function was called
 * @param sockdesc A character string giving a description of the socket to be shut down
 * @param sockfd the socket to be shut down
 * @return 0 if the socket was shut down successfully, -1 if there was an error
 */
int shutdown_socket(char *prefix, char *sockdesc, int sockfd);

/**
 * Destroy the given socket using the close() function from the glibc socket library
 * @param prefix A character string prefix for the messages printed to the console, useful to determine from where the function was called
 * @param sockdesc A character string giving a description of the socket to be destroyed
 * @param sockfd the socket to be destroyed
 * @return 0 if the socket was destroyed successfully, -1 if there was an error
 */
int destroy_socket(char *prefix, char *sockdesc, int sockfd);

/**
 * @brief Put the string representation of the given response code in response
 * @param response The CMD_LEN-byte character string the representation will be written into
 * @param response_code The numerical response code
 */
void put_response(char *response, int response_code);

/**
 * @brief Send a picture using the provided socket file descriptor and file pointer.
 * @param sockfd An open socket through which the picture will be sent
 * @param fp An open file pointer to the picture to send
 * @param buffer The buffer to use when sending the picture through the socket
 * @return The total number of bytes that were sent. A value less than 0 indicates an error.
 */
unsigned int send_picture(SSL *ssl, FILE *fp, char *buffer);

/**
 * @brief Process the command given in cmd, and put the response in response
 * @param cmd A pointer to a CMD_LEN-byte char array containing the command
 * @param response A pointer to a CMD_LEN-byte char array where the response will be put
 */
void process_cmd(char *cmd, char *response);

/**
 * @brief The thread that is started when the client requests a picture. Take a picture and send it to the client.
 * @param ptr Not used
 */
void *img_task(void *ptr);

#endif
