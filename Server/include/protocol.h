#ifndef PROTOCOL_H
#define PROTOCOL_H

#include <stdlib.h>
#include <string.h>
#include <stdio.h>

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

#define CMD_LEN 16

void put_response(char *response, int response_code);

int process_cmd(char *cmd, char *response);

#endif