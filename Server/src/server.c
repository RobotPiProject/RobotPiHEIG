#include <server.h>
#include <protocol.h>

int client_connected = 0;

unsigned int read_msg(char *prefix, int sockfd, char *buffer, char *dest, size_t buffer_size) {
    unsigned int bytes_read;
    int cmd_end = 0;
    unsigned int current_offet = 0;
    unsigned int total_bytes = 0;
    while (!cmd_end) {
        bytes_read = recv(sockfd, buffer, buffer_size, 0);
        total_bytes += bytes_read;
        if (bytes_read < 0) {
            fprintf(stderr, "%sError reading socket\n", prefix);
            pthread_exit(NULL);
        }
        if (bytes_read == 0) { // connection is closed
            fprintf(stdout, "%sClient disconnected\n", prefix);
            pthread_exit(NULL);
        }

        fprintf(stdout, "%s%d bytes received:", prefix, bytes_read);
        for (int i = 0; i < bytes_read; i++) {
            fprintf(stdout, " 0x%X", buffer[i]);
        }
        fprintf(stdout, "\n");
        for (int i = 0; i < bytes_read; i++) {
            if (buffer[i] == '\n') {
                cmd_end = 1;
            }
        }
        size_t buffer_len = bytes_read;
        /* Do not write past the end of the dest array */
        if (total_bytes > CMD_LEN) {
            fprintf(stderr, "%sCommand too long, truncating\n", prefix);
            buffer_len = total_bytes-CMD_LEN;
        }
        memcpy(dest + current_offet * sizeof(char), buffer, buffer_len);
        current_offet += bytes_read;
        explicit_bzero(buffer, buffer_size);
    }
    // strip new line
    dest[strcspn(dest, "\n")] = 0;
    return total_bytes;
}

unsigned int send_msg(char *prefix, int sockfd, char *msg, size_t msg_len) {
    unsigned int bytes_sent = send(sockfd, msg, msg_len, 0);
    fprintf(stdout, "%s%d bytes sent\n", prefix, bytes_sent );
    for (int i = 0; i < bytes_sent; i++) {
        fprintf(stdout, " 0x%X", msg[i]);
    }
    fprintf(stdout, "\n");
    if (bytes_sent < 0) {
        fprintf(stderr, "%sError sending response\n", prefix);
    }
    if (bytes_sent < msg_len) {
        fprintf(stderr, "%sCould not send all bytes\n", prefix);
    }
    return bytes_sent;
}

void *session_task(void *sockfd) {
    int client_sockfd = *(int*) sockfd;
    char buffer[BUFFER_SIZE], cmd[CMD_LEN], response[CMD_LEN];
    explicit_bzero(buffer, BUFFER_SIZE);
    explicit_bzero(cmd, CMD_LEN);
    explicit_bzero(response, CMD_LEN);
    unsigned int res_len;
    while (1) {
        unsigned int total_bytes = read_msg("[server] ", client_sockfd, buffer, cmd, BUFFER_SIZE);
        fprintf(stdout, "[server] Command received: %s", cmd);
        for (int i = 0; i < total_bytes-1; i++) {
            fprintf(stdout, " 0x%X", cmd[i]);
        }
        fprintf(stdout, "\n");

        /* We don't accept commands if there is no application-level connection */
        if (!client_connected) {
            if (!strncmp(cmd, "DISCONN", CMD_LEN)) {
                put_response(response, DISCONN_ERR);
            } else if (strncmp(cmd, "CONN", CMD_LEN) != 0) {
                put_response(response, CMD_ERR);
            } else {
                client_connected = 1;
                put_response(response, CONN_OK);
            }
        } else {
            if (!strncmp(cmd, "CONN", CMD_LEN)) {
                put_response(response, CONN_ERR);
            } else if (!strncmp(cmd, "DISCONN", CMD_LEN)) {
                client_connected = 0;
                put_response(response, DISCONN_OK);
            } else {
                /* Only then can we process the command */
                process_cmd(cmd, response);
            }
        }
        fprintf(stdout, "[server] Sending message: %s\n", response);

        // append new line character
        res_len = strlen(response);
        response[res_len] = '\n';
        res_len++;
        send_msg("[server] ", client_sockfd, response, res_len);
        bzero(cmd, CMD_LEN);
        bzero(response, CMD_LEN);
    }
}

int server() {
    int server_sockfd = 0, client_sockfd = 0, img_server_sockfd = 0;
    server_sockfd = create_inet_server_socket("::", LISTENING_PORT, LIBSOCKET_TCP, LIBSOCKET_BOTH, 0);
    if (server_sockfd == -1) {
        fprintf(stderr, "[server] Could not create server socket\n");
        return EXIT_FAILURE;
    }

    while (1) {
        fprintf(stdout, "[server] Waiting for clients...\n");
        client_sockfd = accept_inet_stream_socket(server_sockfd, 0, 0, 0, 0, 0, 0);
        if (client_sockfd < 0) {
            fprintf(stderr, "[server] Error on accept");
            return EXIT_FAILURE;
        }
        fprintf(stdout, "[server] Connection established\n");
        pthread_t session_t;
        pthread_create(&session_t, NULL, session_task, (void *) &client_sockfd);
        pthread_t img_t;
        pthread_create(&img_t, NULL, img_task, (void *) &img_server_sockfd);
        pthread_join(session_t, NULL);
        close(client_sockfd);
        fprintf(stdout, "[server] Bye\n");
    }
}