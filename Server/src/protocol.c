#include "include/protocol.h"

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
        response_code = FWD_OK;
    } else if (!strncmp(cmd, "BKWD", CMD_LEN)) {
        response_code = BKWD_OK;
    } else if (!strncmp(cmd, "STOP", CMD_LEN)) {
        response_code = STOP_OK;
    } else if (!strncmp(cmd, "ROTATE_LEFT", CMD_LEN)) {
        response_code = ROTATE_LEFT_OK;
    } else if (!strncmp(cmd, "ROTATE_RIGHT", CMD_LEN)) {
        response_code = ROTATE_RIGHT_OK;
    } else if (!strncmp(cmd, "FRONT_L", CMD_LEN)) {
        response_code = FRONT_L_OK;
    } else if (!strncmp(cmd, "FRONT_R", CMD_LEN)) {
        response_code = FRONT_R_OK;
    } else if (!strncmp(cmd, "BCK_L", CMD_LEN)) {
        response_code = BCK_L_OK;
    } else if (!strncmp(cmd, "BCK_R", CMD_LEN)) {
        response_code = BCK_R_OK;
    } else if (!strncmp(cmd, "DISCONN", CMD_LEN)) {
        response_code = DISCONN_OK;
    } else if (!strncmp(cmd, "PING", CMD_LEN)) {
        response_code = PING;
    } else {
        fprintf(stdout, "Commande non reconnue : %s\n", cmd);
    }
    put_response(response, response_code);
    return 0;
}