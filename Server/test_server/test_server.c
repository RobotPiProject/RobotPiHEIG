/**
 * @file test_protocol.c
 * @brief Test program verifying the correct implementation of the RoboPi protocol. For now, these tests must be run on an arm platform
 * @author Basile Thullen
 * @version 0.1
 * @date 2021-06-05
 *
 * @copyright Copyright (c) 2021
 */

#include <stdarg.h>
#include <stddef.h>
#include <stdint.h>
#include <setjmp.h>
#include <cmocka.h>
#include <math.h>
#include <stdio.h>
#include <protocol.h>

/* we need to mock motor commands */
void __wrap_setMotor(char motor, signed char velocity){};
void __wrap_runForward(unsigned char speed){};
void __wrap_runBackward(unsigned char speed){};
void __wrap_turnRightF(unsigned char speed){};
void __wrap_turnLeftF(unsigned char speed){};
void __wrap_turnRightB(unsigned char speed){};
void __wrap_turnLeftB(unsigned char speed){};
void __wrap_rotateRight(unsigned char speed){};
void __wrap_rotateLeft(unsigned char speed){};
void __wrap_idle(){};
int __wrap_motorInit() {
    return 0;
}
void __wrap_motorQuit(){};

/* mock ssl socket communication */
int __wrap_SSL_read(SSL *ssl, void *buf, int num) {
    char tmp[num];
    for (int i = 0; i < num-1; i++) {
        tmp[i] = 'A' + (i % 26);
    }
    tmp[num-1] = '\n';
    memcpy(buf, tmp, num);
    return num;
}

char cmd[CMD_LEN];
char res[CMD_LEN];
char buffer[CMD_LEN];

int reset_arrays() {
    explicit_bzero(cmd, CMD_LEN);
    explicit_bzero(res, CMD_LEN);
    explicit_bzero(buffer, CMD_LEN);
    return 0;
}

/**
 * @brief Tests that the server sets the correct response string for the command CONN
 * @param state not used
 */
void test_protocol_conn(void **state) {
    (void) state; /* osef */
    strncpy(cmd, "CONN", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "CONN_OK");
}

/**
 * @brief Tests that the server correctly sets the CMD_ERR response string after receiving an invalid command
 * @param state not used
 */
void test_protocol_cmd_err(void **state) {
    (void) state; /* osef */
    strncpy(cmd, "GARBAGE", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "CMD_ERR");
}

/**
 * @brief Tests that the server sets the correct response string for the command DISCONN
 * @param state not used
 */
void test_protocol_disconn(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "DISCONN", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "DISCONN_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command PING
 * @param state not used
 */
void test_protocol_ping(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "PING", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "PING");
}

/**
 * @brief Tests that the server sets the correct response string for the command FWD
 * @param state not used
 */
void test_protocol_fwd(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "FWD", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "FWD_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command BKWD
 * @param state not used
 */
void test_protocol_bkwd(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "BKWD", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "BKWD_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command STOP
 * @param state not used
 */
void test_protocol_stop(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "STOP", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "STOP_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command ROTATE_LEFT
 * @param state not used
 */
void test_protocol_rl(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "ROTATE_LEFT", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "ROTATE_LEFT_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command ROTATE_RIGHT
 * @param state not used
 */
void test_protocol_rr(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "ROTATE_RIGHT", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "ROTATE_RIGHT_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command FRONT_L
 * @param state not used
 */
void test_protocol_fl(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "FRONT_L", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "FRONT_L_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command FRONT_R
 * @param state not used
 */
void test_protocol_fr(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "FRONT_R", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "FRONT_R_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command FRONT_R
 * @param state not used
 */
void test_protocol_bl(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "BCK_L", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "BCK_L_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command BCK_R
 * @param state not used
 */
void test_protocol_br(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "BCK_R", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "BCK_R_OK");
}

/**
 * @brief Tests that the server sets the correct response string for the command PICTURE
 * @param state not used
 */
void test_protocol_pic_server(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "PICTURE", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "PICTURE_ERR");
}

/**
 * @brief Tests that read_msg reads the right number of bytes, and especially not the line-terminating character
 * @param state not used
 */
void test_read_msg_server(void** state) {
    (void) state; /* osef */
    SSL *phony;
    char dest[CMD_LEN];
    explicit_bzero(dest, CMD_LEN);
    int nread = read_msg("[TEST] ", phony, buffer, dest, CMD_LEN);
    assert_int_equal(nread, CMD_LEN);
    char cmp[CMD_LEN];
    explicit_bzero(cmp, CMD_LEN);
    for (int i = 0; i < CMD_LEN-1; i++) {
        cmp[i] = 'A' + i;
    }
    assert_memory_equal(cmp, dest, CMD_LEN);
}

int main(void) {
    const struct CMUnitTest protocol_normal_test[] = {
            cmocka_unit_test_setup(test_protocol_conn, reset_arrays),
            cmocka_unit_test_setup(test_protocol_disconn, reset_arrays),
            cmocka_unit_test_setup(test_protocol_cmd_err, reset_arrays),
            cmocka_unit_test_setup(test_protocol_ping, reset_arrays),
            cmocka_unit_test_setup(test_protocol_fwd, reset_arrays),
            cmocka_unit_test_setup(test_protocol_bkwd, reset_arrays),
            cmocka_unit_test_setup(test_protocol_stop, reset_arrays),
            cmocka_unit_test_setup(test_protocol_rl, reset_arrays),
            cmocka_unit_test_setup(test_protocol_rr, reset_arrays),
            cmocka_unit_test_setup(test_protocol_fl, reset_arrays),
            cmocka_unit_test_setup(test_protocol_fr, reset_arrays),
            cmocka_unit_test_setup(test_protocol_bl, reset_arrays),
            cmocka_unit_test_setup(test_protocol_br, reset_arrays),
            cmocka_unit_test_setup(test_protocol_pic_server, reset_arrays),
            cmocka_unit_test_setup(test_read_msg_server, reset_arrays),
    };

    return cmocka_run_group_tests(protocol_normal_test, NULL, NULL);
}