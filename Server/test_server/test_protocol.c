#include <stdarg.h>
#include <stddef.h>
#include <stdint.h>
#include <setjmp.h>
#include <cmocka.h>
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

char cmd[CMD_LEN];
char res[CMD_LEN];

int reset_arrays() {
    explicit_bzero(cmd, CMD_LEN);
    explicit_bzero(res, CMD_LEN);
    return 0;
}

void test_protocol_conn(void **state) {
    (void) state; /* osef */
    strncpy(cmd, "CONN", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "CONN_OK");
}

void test_protocol_cmd_err(void **state) {
    (void) state; /* osef */
    strncpy(cmd, "GARBAGE", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "CMD_ERR");
}

void test_protocol_disconn(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "DISCONN", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "DISCONN_OK");
}

void test_protocol_ping(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "PING", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "PING");
}

void test_protocol_fwd(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "FWD", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "FWD_OK");
}

void test_protocol_bkwd(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "BKWD", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "BKWD_OK");
}

void test_protocol_stop(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "STOP", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "STOP_OK");
}

void test_protocol_rl(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "ROTATE_LEFT", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "ROTATE_LEFT_OK");
}

void test_protocol_rr(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "ROTATE_RIGHT", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "ROTATE_RIGHT_OK");
}

void test_protocol_fl(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "FRONT_L", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "FRONT_L_OK");
}

void test_protocol_fr(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "FRONT_R", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "FRONT_R_OK");
}

void test_protocol_bl(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "BCK_L", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "BCK_L_OK");
}

void test_protocol_br(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "BCK_R", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "BCK_R_OK");
}

void test_protocol_pic_server(void** state) {
    (void) state; /* osef */
    strncpy(cmd, "PICTURE", CMD_LEN);
    process_cmd(cmd, res);
    assert_string_equal(res, "PICTURE_ERR");
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
    };

    return cmocka_run_group_tests(protocol_normal_test, NULL, NULL);
}