#include <protocol.h>
#include <motor.h>
#include <camera.h>

extern int client_connected;

/**
 * Put the string representation of the given response code in response
 * @param response the CMD_LEN-byte character string the representation will be written into
 * @param response_code the numerical response code
 */
void put_response(char *response, int response_code) {
    switch (response_code) {
        case CONN_OK:
            strncpy(response, "CONN_OK", CMD_LEN);
            break;
        case CONN_ERR:
            strncpy(response, "CONN_ERR", CMD_LEN);
            break;
        case FWD_OK:
            strncpy(response, "FWD_OK", CMD_LEN);
            break;
        case BKWD_OK:
            strncpy(response, "BKWD_OK", CMD_LEN);
            break;
        case STOP_OK:
            strncpy(response, "STOP_OK", CMD_LEN);
            break;
        case ROTATE_LEFT_OK:
            strncpy(response, "ROTATE_LEFT_OK", CMD_LEN);
            break;
        case ROTATE_RIGHT_OK:
            strncpy(response, "ROTATE_RIGHT_OK", CMD_LEN);
            break;
        case FRONT_L_OK:
            strncpy(response, "FRONT_L_OK", CMD_LEN);
            break;
        case FRONT_R_OK:
            strncpy(response, "FRONT_R_OK", CMD_LEN);
            break;
        case BCK_L_OK:
            strncpy(response, "BCK_L_OK", CMD_LEN);
            break;
        case BCK_R_OK:
            strncpy(response, "BCK_R_OK", CMD_LEN);
            break;
        case DISCONN_OK:
            strncpy(response, "DISCONN_OK", CMD_LEN);
            break;
        case DISCONN_ERR:
            strncpy(response, "DISCONN_ERR", CMD_LEN);
            break;
        case PING:
            strncpy(response, "PING", CMD_LEN);
            break;
        case PICTURE_OK:
            strncpy(response, "PICTURE_OK", CMD_LEN);
            break;
        case PICTURE_ERR:
            strncpy(response, "PICTURE_ERR", CMD_LEN);
            break;
        default:
            strncpy(response, "CMD_ERR", CMD_LEN);
            break;
    }
}

/**
 * Process the command given in cmd, and put the response in response
 * @param cmd pointer to a CMD_LEN-byte char array containing the command
 * @param response pointer to a CMD_LEN-byte char array where the response will be put
 */
void process_cmd(char *cmd, char *response) {
    int response_code = CMD_ERR;
    if (!strncmp(cmd, "CONN", CMD_LEN)) {
        response_code = CONN_OK;
    } else if (!strncmp(cmd, "FWD", CMD_LEN)) {
        runForward(DEFAULT_SPEED);
        response_code = FWD_OK;
    } else if (!strncmp(cmd, "BKWD", CMD_LEN)) {
        runBackward(DEFAULT_SPEED);
        response_code = BKWD_OK;
    } else if (!strncmp(cmd, "STOP", CMD_LEN)) {
        idle();
        response_code = STOP_OK;
    } else if (!strncmp(cmd, "ROTATE_LEFT", CMD_LEN)) {
        rotateLeft(DEFAULT_SPEED);
        response_code = ROTATE_LEFT_OK;
    } else if (!strncmp(cmd, "ROTATE_RIGHT", CMD_LEN)) {
        rotateRight(DEFAULT_SPEED);
        response_code = ROTATE_RIGHT_OK;
    } else if (!strncmp(cmd, "FRONT_L", CMD_LEN)) {
        turnLeftF(DEFAULT_SPEED);
        response_code = FRONT_L_OK;
    } else if (!strncmp(cmd, "FRONT_R", CMD_LEN)) {
        turnRightF(DEFAULT_SPEED);
        response_code = FRONT_R_OK;
    } else if (!strncmp(cmd, "BCK_L", CMD_LEN)) {
        turnLeftB(DEFAULT_SPEED);
        response_code = BCK_L_OK;
    } else if (!strncmp(cmd, "BCK_R", CMD_LEN)) {
        turnRightB(DEFAULT_SPEED);
        response_code = BCK_R_OK;
    } else if (!strncmp(cmd, "DISCONN", CMD_LEN)) {
        response_code = DISCONN_OK;
    } else if (!strncmp(cmd, "PING", CMD_LEN)) {
        response_code = PING;
    } else if (!strncmp(cmd, "PICTURE", CMD_LEN)) {
        response_code = PICTURE_ERR;
    } else {
        fprintf(stdout, "Commande non reconnue : %s\n", cmd);
    }
    put_response(response, response_code);
}

int shutdown_socket(char *prefix, char *sockdesc, int sockfd) {
    fprintf(stdout, "[%s] Closing %s socket\n", prefix, sockdesc);
    int ret = shutdown_inet_stream_socket(sockfd, LIBSOCKET_WRITE|LIBSOCKET_READ);
    if (ret == -1) {
        fprintf(stderr, "[%s] Error closing %s socket: %s\n", prefix, sockdesc, strerror(errno));
    }
    return ret;
}

int destroy_socket(char *prefix, char *sockdesc, int sockfd) {
    fprintf(stdout, "[%s] Destroying %s socket\n", prefix, sockdesc);
    int ret = destroy_inet_socket(sockfd);
    if (ret == -1) {
        fprintf(stderr, "[%s] Error destroying %s socket: %s\n", prefix, sockdesc, strerror(errno));
    }
    return ret;
}

/**
 * The thread that is started when the client requests a picture. Take a picture and send it to the client.
 * @param ptr not used
 */
void *img_task(void *ptr) {
    SSL *sslImg = (SSL*) ptr;
    int img_client_sockfd = 0;
    char buffer[BUFFER_SIZE], cmd[CMD_LEN], response[CMD_LEN];
    explicit_bzero(buffer, BUFFER_SIZE);
    explicit_bzero(cmd, CMD_LEN);
    explicit_bzero(response, CMD_LEN);

    int img_server_sockfd = create_inet_server_socket("::", LISTENING_IMG_PORT, LIBSOCKET_TCP, LIBSOCKET_BOTH, 0);
    if (img_server_sockfd == -1) {
        fprintf(stderr, "[pic] Could not create server socket: %s\n", strerror(errno));
        pthread_exit(NULL);
    }
    while(client_connected == 0); // ?? chelou
    while (client_connected == 1) {
        fprintf(stdout, "[pic] Waiting for clients...\n");
        img_client_sockfd = accept_inet_stream_socket(img_server_sockfd, 0, 0, 0, 0, 0, 0);
        if (img_client_sockfd < 0) {
            fprintf(stderr, "[pic] Could not create client socket: %s\n", strerror(errno));
            destroy_socket("pic", "image server", img_server_sockfd);
            pthread_exit(NULL);
        }

	    // Init TLS
        SSL_set_fd(sslImg, img_client_sockfd);
        SSL_set_mode(sslImg, SSL_MODE_AUTO_RETRY);

		if(SSL_accept(sslImg) <= 0){
			ERR_print_errors_fp(stderr);
            fprintf(stderr, "[server] Error on ssl accept img");
			pthread_exit(NULL);
		} else {
            fprintf(stdout, "[pic] Connection established\n");
            read_msg("[pic] ", sslImg, buffer, cmd, BUFFER_SIZE);
            fprintf(stdout, "[pic] Command received: %s\n", cmd);
            if (strncmp(cmd, "PICTURE", CMD_LEN) != 0) {
                fprintf(stderr, "[pic] Invalid command: %s\n", cmd);
                put_response(response, PICTURE_ERR);
                unsigned int res_len = prepare_response(response);
                send_msg("[pic] ", sslImg, response, res_len);
                shutdown_inet_stream_socket(img_client_sockfd, LIBSOCKET_WRITE | LIBSOCKET_READ);
                continue;
            } else {
                if (client_connected == 1) {
                    put_response(response, PICTURE_OK);
                    unsigned int res_len = prepare_response(response);
                    fprintf(stdout, "Sending message: %s\n", response);
                    send_msg("[pic] ", sslImg, response, res_len);
                } else {
                    put_response(response, PICTURE_ERR);
                    unsigned int res_len = prepare_response(response);
                    send_msg("[pic] ", sslImg, response, res_len);
                    shutdown_inet_stream_socket(img_client_sockfd, LIBSOCKET_WRITE | LIBSOCKET_READ);
                    continue;
                }
            }
            /* start sending image */
            camSnap("/home/pi", "pic");
            char *fname = "/home/pi/pic.jpg";
            FILE *file_handle = fopen(fname, "r");
            send_picture(sslImg, file_handle, buffer);
            explicit_bzero(cmd, CMD_LEN);

            fclose(file_handle);
            shutdown_socket("pic", "image client", img_client_sockfd);
            explicit_bzero(cmd, CMD_LEN);
        }
    }
    fprintf(stdout, "[pic] Client disconnected\n");
    shutdown_socket("pic", "image client", img_client_sockfd);
    shutdown_socket("pic", "image server", img_server_sockfd);
    destroy_socket("pic", "image server", img_server_sockfd);
    pthread_exit(NULL);
}

/**
 * Send a picture using the provided socket file descriptor and file pointer.
 * @param sockfd an open socket through which the picture will be sent
 * @param fp an open file pointer to the picture to send
 * @param buffer the buffer to use when sending the picture through the socket
 * @return the total number of bytes that were sent
 */
unsigned int send_picture(SSL *sslImg, FILE *fp, char *buffer) {
    fseek(fp, 0, SEEK_END);
    unsigned long filesize = ftell(fp);
    rewind(fp);
    size_t nb_chunks = filesize / BUFFER_SIZE;
    size_t rem = filesize % BUFFER_SIZE;
    unsigned int total_bytes_sent = 0;
    unsigned int bytes_sent = 0;
    explicit_bzero(buffer, BUFFER_SIZE);
    for (int i = 0; i < nb_chunks; i++) {
        fread(buffer, sizeof(char), BUFFER_SIZE, fp);
        total_bytes_sent += send_msg("[pic] ", sslImg, buffer, BUFFER_SIZE);
    }
    if (rem > 0) {
        explicit_bzero(buffer, BUFFER_SIZE);
        fread(buffer, sizeof(char), rem, fp);
        bytes_sent = send_msg("[pic] ", sslImg, buffer, BUFFER_SIZE);
        total_bytes_sent += bytes_sent;
    }
    fprintf(stdout,"[pic] Sent %d picture bytes\n", total_bytes_sent);
    return total_bytes_sent;
}