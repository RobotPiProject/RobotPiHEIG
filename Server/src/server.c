#include <server.h>
#include <protocol.h>
#include <openssl/ssl.h>
#include <openssl/err.h>

int client_connected = 0;

void InitializeSSL()
{
    // Provide human-readable error messages.
    SSL_load_error_strings();
    // Register ciphers.
    SSL_library_init();
    OpenSSL_add_all_algorithms();
}

void DestroySSL()
{
    ERR_free_strings();
    EVP_cleanup();
}

void ShutdownSSL()
{
    SSL_shutdown(ssl);
    SSL_free(ssl);
}

SSL_CTX *create_context()
{
    const SSL_METHOD *method;
    SSL_CTX *ctx;

    method = TLS_method();

    ctx = SSL_CTX_new(method);
    if (!ctx) {
	perror("Unable to create SSL context");
	ERR_print_errors_fp(stderr);
	exit(EXIT_FAILURE);
    }

    return ctx;
}

void configure_context(SSL_CTX *ctx)
{
    SSL_CTX_set_ecdh_auto(ctx, 1);

    /* Set the key and cert */
    if (SSL_CTX_use_certificate_file(ctx, "./robotpi.pem", SSL_FILETYPE_PEM) <= 0) {
        ERR_print_errors_fp(stderr);
	exit(EXIT_FAILURE);
    }
// /etc/pki/tls/private/key.pem
// home/maude/Documents/PRO/FORK/RobotPiHEIG/Server/src/key.pem
    if (SSL_CTX_use_PrivateKey_file(ctx, "./key.pem", SSL_FILETYPE_PEM) <= 0 ) {
        ERR_print_errors_fp(stderr);
	exit(EXIT_FAILURE);
    }
}

void *session_task(void *ptr, void *sslPtr) {
    int client_sockfd = *(int*) ptr;
    SSL ssl = *(SSL*) sslPtr;
    char buffer[BUFFER_SIZE];
    char cmd[CMD_LEN];
    char response[CMD_LEN];
    explicit_bzero(buffer, BUFFER_SIZE);
    explicit_bzero(cmd, CMD_LEN);
    explicit_bzero(response, CMD_LEN);
    unsigned int bytes_read, bytes_sent, res_len;
    while (1) {
        int cmd_end = 0;
        unsigned int start = 0;
        unsigned int total_bytes = 0;
        while (!cmd_end) {
            //bytes_read = recv(client_sockfd, buffer, BUFFER_SIZE, 0);
            bytes_read = SSL_read(ssl, buffer, BUFFER_SIZE);
            total_bytes += bytes_read;

            if (bytes_read < 0) {
                fprintf(stderr, "Error reading socket\n");
                pthread_exit(NULL);
            }
            if (bytes_read == 0) { // connection is closed
                fprintf(stdout, "Client disconnected\n");
                pthread_exit(NULL);
            }

            fprintf(stdout, "%d bytes received:", bytes_read);
            for (int i = 0; i < bytes_read; i++) {
                fprintf(stdout, " 0x%X", buffer[i]);
            }
            fprintf(stdout, "\n");
            for (int i = 0; i < bytes_read; i++) {
                if (buffer[i] == '\n') {
                    cmd_end = 1;
                }
            }

            int overflow = 0;
            if (total_bytes > CMD_LEN) {
                fprintf(stderr, "Command too long\n");
                overflow = 1;
            }
            /* Do not write past the end of the cmd array */
            if (!overflow) {
                memcpy(cmd + start * sizeof(char), buffer, CMD_LEN-total_bytes);
            }
            start += bytes_read;
            explicit_bzero(buffer, BUFFER_SIZE);
        }

        // strip new line
        cmd[strcspn(cmd, "\n")] = 0;
        fprintf(stdout, "Command received: %s", cmd);
        for (int i = 0; i < total_bytes-1; i++) {
            fprintf(stdout, " 0x%X", cmd[i]);
        }
        fprintf(stdout, "\n");

        /* We don't accept commands if there is no application-level connection */
        if (!client_connected) {
            if (!strncmp(cmd, "DISCONN", CMD_LEN)) {
                put_response(response, DISCONN_ERR);
            } else if (strncmp(cmd, "CONN", CMD_LEN)) {
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
        fprintf(stdout, "Sending message: %s ", response);

        // append new line character
        res_len = strlen(response);
        response[res_len] = '\n';
        //bytes_sent = send(client_sockfd, response, res_len + 1, 0);
        bytes_sent = SSL_write(ssl, response, strlen(response));
        fprintf(stdout, "%d bytes sent\n", bytes_sent );
        if (bytes_read < 0) {
            fprintf(stderr, "Error sending response\n");
        }
        bzero(cmd, CMD_LEN);
        bzero(response, CMD_LEN);
    }
}

int server() {
    SSL *ssl;
    int server_sockfd = 0, client_sockfd = 0;
    server_sockfd = create_inet_server_socket("::", LISTENING_PORT, LIBSOCKET_TCP, LIBSOCKET_BOTH, 0);
    if (server_sockfd == -1) {
        fprintf(stderr, "Could not create socket\n");
        return EXIT_FAILURE;
    }

    // TLS
    InitializeSSL();
    SSL_CTX *ctx = create_context();
    configure_context(ctx);

    while (1) {
        fprintf(stdout, "Waiting for clients...\n");
        client_sockfd = accept_inet_stream_socket(server_sockfd, 0, 0, 0, 0, 0, 0);
        if (client_sockfd < 0) {
            fprintf(stderr, "Error on accept");
            return EXIT_FAILURE;
        }

        // TLS
        ssl = SSL_new(ctx);
        SSL_set_fd(ssl, client_sockfd);
	SSL_set_mode(ssl, SSL_MODE_AUTO_RETRY);
        
        if (SSL_accept(ssl) <= 0) {
            ERR_print_errors_fp(stderr);
            return EXIT_FAILURE;
        } else {
            fprintf(stdout, "Connection established\n");
            pthread_t session_t;
            pthread_create(&session_t, NULL, session_task, (void *) &client_sockfd, (void *) &ssl);
            pthread_join(session_t, NULL);
            close(client_sockfd);
            fprintf(stdout, "Bye\n");
        }
    }
}
