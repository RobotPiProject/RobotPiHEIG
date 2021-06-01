#include <server.h>
#include <protocol.h>
#include <motor.h>

int client_connected = 0;
int img_server_sockfd = 0;

void init_openssl()
{
    // Provide human-readable error messages.
    SSL_load_error_strings();
    // Register ciphers.
    SSL_library_init();
    OpenSSL_add_all_algorithms();
}

void cleanup_openssl()
{
    ERR_free_strings();
    EVP_cleanup();
}

void shutdown_openssl(SSL *sslCmd, SSL *sslImg)
{
    SSL_shutdown(sslCmd);
    SSL_free(sslCmd);
    SSL_shutdown(sslImg);
    SSL_free(sslImg);
}

SSL_CTX *create_context()
{
    const SSL_METHOD *method;
    SSL_CTX *ctx;

    method = TLS_method();

    ctx = SSL_CTX_new(method);
    if (!ctx)
    {
        perror("Unable to create SSL context");
        ERR_print_errors_fp(stderr);
        exit(EXIT_FAILURE);
    }

    return ctx;
}

void configure_context(SSL_CTX *ctx)
{
    SSL_CTX_set_ecdh_auto(ctx, 1);

    if (SSL_CTX_use_certificate_file(ctx, "/etc/ssl/certs/robotpi.pem", SSL_FILETYPE_PEM) <= 0)
    {
        ERR_print_errors_fp(stderr);
        exit(EXIT_FAILURE);
    }

    if (SSL_CTX_use_PrivateKey_file(ctx, "/etc/ssl/certs/key.pem", SSL_FILETYPE_PEM) <= 0)
    {
        ERR_print_errors_fp(stderr);
        exit(EXIT_FAILURE);
    }
}

/**
 * Prepare a response so that it conforms to the RoboPi protocol, mainly so that the response ends with the correct line-terminating character
 * @param response the response to be prepared
 * @return the total number of bytes of the response (including the newly added line-terminating character)
 */
unsigned int prepare_response(char *response)
{
    unsigned int res_len = strlen(response);
    response[res_len] = '\n';
    res_len++;
    return res_len;
}

void print_ssl_err(char *prefix, SSL *ssl, int retcode) {
    int errcode = SSL_get_error(ssl, retcode);
    switch (errcode) {
        case SSL_ERROR_NONE:
            fprintf(stderr, "[%s] SSL_ERROR_NONE\n", prefix);
            break;
        case SSL_ERROR_WANT_WRITE:
            fprintf(stderr, "[%s] SSL_ERROR_WANT_WRITE\n", prefix);
            break;
        case SSL_ERROR_WANT_READ:
            fprintf(stderr, "[%s] SSL_ERROR_WANT_READ\n", prefix);
            break;
        case SSL_ERROR_ZERO_RETURN:
            fprintf(stderr, "[%s] SSL_ERROR_ZERO_RETURN\n", prefix);
            break;
        case SSL_ERROR_WANT_CONNECT:
            fprintf(stderr, "[%s] SSL_ERROR_WANT_CONNECT\n", prefix);
            break;
        case SSL_ERROR_WANT_ACCEPT:
            fprintf(stderr, "[%s] SSL_ERROR_WANT_ACCEPT\n", prefix);
            break;
        case SSL_ERROR_WANT_X509_LOOKUP:
            fprintf(stderr, "[%s] SSL_ERROR_WANT_X509_LOOKUP\n", prefix);
            break;
        case SSL_ERROR_SYSCALL :
            fprintf(stderr, "[%s] SSL_ERROR_SYSCALL \n", prefix);
            break;
        case SSL_ERROR_SSL:
            // More info in the OpenSSL error queue
            fprintf(stderr, "[%s] SSL_ERROR_SSL\n", prefix);
            break;
    }
}

/**
 * Read from the given socket until we come across a line-terminating character ('\n')
 * @param prefix a character prefix for the messages printed to the console, useful to determine from where the function was called
 * @param sockfd an open socket from which the next message will be read
 * @param buffer a character buffer used to receive from the socket
 * @param dest a character buffer to which the received message will be copied, up to and not including the line-terminating character
 * @param buffer_size the size of the given character buffer
 * @return the total number of bytes that were received, -1 if there was an error
 */
int read_msg(char *prefix, SSL *sslCmd, char *buffer, char *dest, size_t buffer_size)
{
    int bytes_read;
    int cmd_end = 0;
    unsigned int current_offet = 0;
    unsigned int total_bytes = 0;
    while (!cmd_end)
    {
        bytes_read = SSL_read(sslCmd, buffer, buffer_size);
        total_bytes += bytes_read;
        if (bytes_read < 0)
        {
            fprintf(stderr, "%sError reading socket\n", prefix);
            print_ssl_err("server", sslCmd, bytes_read);
            return -1;
        }
        if (bytes_read == 0)
        { // connection is closed
            fprintf(stderr, "%sClient disconnected: \n", prefix);
            print_ssl_err("server", sslCmd, bytes_read);
            return -1;
        }

        fprintf(stdout, "%s%d bytes received:", prefix, bytes_read);
        for (int i = 0; i < bytes_read; i++)
        {
            fprintf(stdout, " 0x%X", buffer[i]);
        }
        fprintf(stdout, "\n");
        for (int i = 0; i < bytes_read; i++)
        {
            if (buffer[i] == '\n')
            {
                cmd_end = 1;
            }
        }
        size_t buffer_len = bytes_read;
        /* Do not write past the end of the dest array */
        if (total_bytes > CMD_LEN)
        {
            fprintf(stderr, "%sCommand too long, truncating\n", prefix);
            buffer_len = total_bytes - CMD_LEN;
        }
        memcpy(dest + current_offet * sizeof(char), buffer, buffer_len);
        current_offet += bytes_read;
        explicit_bzero(buffer, buffer_size);
    }
    // strip new line
    dest[strcspn(dest, "\n")] = 0;
    return total_bytes;
}

unsigned int send_msg(char *prefix, SSL *sslCmd, char *msg, size_t msg_len)
{
    unsigned int bytes_sent = SSL_write(sslCmd, msg, msg_len);
    fprintf(stdout, "%s%d bytes sent\n", prefix, bytes_sent);
    for (int i = 0; i < bytes_sent; i++)
    {
        fprintf(stdout, " 0x%X", msg[i]);
    }
    fprintf(stdout, "\n");
    if (bytes_sent < 0)
    {
        fprintf(stderr, "%sError sending response\n", prefix);
    }
    if (bytes_sent < msg_len)
    {
        fprintf(stderr, "%sCould not send all bytes\n", prefix);
    }
    return bytes_sent;
}

void *session_task(void *ssl)
{
    SSL *sslCmd = (SSL *)ssl;
    char buffer[BUFFER_SIZE], cmd[CMD_LEN], response[CMD_LEN];
    explicit_bzero(buffer, BUFFER_SIZE);
    explicit_bzero(cmd, CMD_LEN);
    explicit_bzero(response, CMD_LEN);
    while (1)
    {
        int total_bytes = read_msg("[server] ", sslCmd, buffer, cmd, BUFFER_SIZE);
        if (total_bytes == -1) {
            pthread_exit(NULL);
        }
        fprintf(stdout, "[server] Command received: %s", cmd);
        for (int i = 0; i < total_bytes - 1; i++)
        {
            fprintf(stdout, " 0x%X", cmd[i]);
        }
        fprintf(stdout, "\n");
        int should_quit = 0;
        /* We don't accept commands if there is no application-level connection */
        if (!client_connected)
        {
            if (!strncmp(cmd, "DISCONN", CMD_LEN))
            {
                put_response(response, DISCONN_ERR);
            }
            else if (strncmp(cmd, "CONN", CMD_LEN) != 0)
            {
                put_response(response, CMD_ERR);
                should_quit = 1;
            }
            else
            {
                client_connected = 1;
                put_response(response, CONN_OK);
            }
        }
        else
        {
            if (!strncmp(cmd, "CONN", CMD_LEN))
            {
                fprintf(stderr, "[server] There is another client already connected\n");
                put_response(response, CONN_ERR);
                should_quit = 1;
            }
            else if (!strncmp(cmd, "DISCONN", CMD_LEN))
            {
                client_connected = 0;
                put_response(response, DISCONN_OK);
                should_quit = 1;
            }
            else
            {
                /* Only then can we process the command */
                process_cmd(cmd, response);
            }
        }
        fprintf(stdout, "[server] Sending message: %s\n", response);

        unsigned int res_len = prepare_response(response);
        send_msg("[server] ", sslCmd, response, res_len);
        bzero(cmd, CMD_LEN);
        bzero(response, CMD_LEN);
        if (should_quit == 1)
        {
            //shutdown_socket("server", "client", client_sockfd);
            //shutdown_socket("server", "image server", img_server_sockfd);
            client_connected = 0;
            pthread_exit(NULL);
        }
    }
}

int server()
{
    int server_sockfd = 0;
    SSL *sslCmd, *sslImg;

    server_sockfd = create_inet_server_socket("::", LISTENING_PORT, LIBSOCKET_TCP, LIBSOCKET_BOTH, 0);
    if (server_sockfd == -1)
    {
        fprintf(stderr, "[server] Could not create server socket: %s\n", strerror(errno));
        return EXIT_FAILURE;
    }

    if (motorInit())
    {
        fprintf(stderr, "[server] Unable to initialise motors\n");
        return EXIT_FAILURE;
    }

    // Init TLS
    init_openssl();
    SSL_CTX *ctx = create_context();
    configure_context(ctx);

    while (1)
    {
        int client_sockfd = 0;
        pthread_t img_t = 0; // pourquoi créé ici ? et pas comme session_t
        fprintf(stdout, "[server] Waiting for clients...\n");
        client_sockfd = accept_inet_stream_socket(server_sockfd, 0, 0, 0, 0, 0, 0);
        if (client_sockfd < 0)
        {
            fprintf(stderr, "[server] Error on accept: %s\n", strerror(errno));
        }

        // Create TLS socket for the commands
        sslCmd = SSL_new(ctx);
        SSL_set_fd(sslCmd, client_sockfd);
        SSL_set_mode(sslCmd, SSL_MODE_AUTO_RETRY);

        // Create TLS socket for the images
        sslImg = SSL_new(ctx);

        if (SSL_accept(sslCmd) <= 0)
        {
            fprintf(stderr, "[server] Error on sslCmd accept");
            ERR_print_errors_fp(stderr);
            return EXIT_FAILURE;
        }
        else
        {
            fprintf(stdout, "[server] Connection established\n");
            pthread_t session_t;
            pthread_create(&session_t, NULL, session_task, (void *)sslCmd);
            pthread_create(&img_t, NULL, img_task, (void *)sslImg);

            // Pourquoi pas de join? C moi qui les ai ajouté
            pthread_join(session_t, NULL);
            pthread_join(img_t, NULL);

            shutdown_openssl(sslCmd, sslImg);
            shutdown_socket("server", "client", client_sockfd);
            shutdown_socket("server", "image server", img_server_sockfd);
        }
    }

    SSL_CTX_free(ctx);
    cleanup_openssl();
}
