#include <protocol.h>
#include <motor.h>

int img_req = 0;
pthread_t img_t;

/**
 * Put the string representation of the given response code in response
 * @param response the character string the representation will be written into
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
        case IMG_OK:
            img_req = 1;
            strncpy(response, "IMG_OK", CMD_LEN);
            break;
        case IMG_ERR:
            strncpy(response, "IMG_ERR", CMD_LEN);
            break;
        case IMG_SIZE_OK:
            strncpy(response, "IMG_SIZE_OK", CMD_LEN);
            break;
        default:
            strncpy(response, "CMD_ERR", CMD_LEN);
            break;
    }
}

/**
 * Process the command given in cmd, and put the response in response
 * @param cmd pointer to a char array containing the command
 * @param response pointer to a char array where the response will be put
 * @return a return value different from 0 indicates that an error has occured
 */
int process_cmd(char *cmd, char *response) {
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
    } else if (!strncmp(cmd, "IMG", CMD_LEN)) {
        response_code = IMG_OK;
        pthread_create(&img_t, NULL, img_task, NULL);
    } else {
        fprintf(stdout, "Commande non reconnue : %s\n", cmd);
    }
    put_response(response, response_code);
    return 0;
}

void *img_task(void *ptr) {
    #define SIZE 2048
    char buffer[BUFFER_SIZE];
    char cmd[CMD_LEN];
    char response[CMD_LEN];
    int img_server_sockfd = create_inet_server_socket("::", LISTENING_IMG_PORT, LIBSOCKET_TCP, LIBSOCKET_BOTH, 0);
    if (img_server_sockfd == -1) {
        fprintf(stderr, "Could not create socket\n");
        pthread_exit(NULL);
    }
    int img_client_sockfd = accept_inet_stream_socket(img_server_sockfd, 0, 0, 0, 0, 0, 0);
    if (img_client_sockfd < 0) {
        fprintf(stderr, "Could not create socket\n");
        pthread_exit(NULL);
    }
    unsigned int total_bytes = read_msg(img_client_sockfd, cmd, buffer, BUFFER_SIZE);
    cmd[strcspn(cmd, "\n")] = 0;
    fprintf(stdout, "Command received: %s", cmd);
    if (!strncmp(cmd, "IMG_SIZE", CMD_LEN)){
        fprintf(stderr, "Invalid client response: %s\n", cmd);
        put_response(response, IMG_ERR);
        send_msg(img_client_sockfd, response, strlen(response));
        shutdown_inet_stream_socket(img_server_sockfd, LIBSOCKET_WRITE|LIBSOCKET_READ);
        pthread_exit(NULL);
    } else {
        put_response(response, IMG_SIZE_OK);
        response[strlen(response)] = '\n';
        send_msg(img_client_sockfd, response, strlen(response));
        explicit_bzero(buffer, BUFFER_SIZE);
        sprintf(buffer, "%d", SIZE);
        buffer[strlen(buffer)] = '\n';
        send_msg(img_client_sockfd, buffer, strlen(buffer));
    }
    shutdown_inet_stream_socket(img_server_sockfd, LIBSOCKET_WRITE|LIBSOCKET_READ);
    pthread_exit(NULL);
}