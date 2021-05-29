PI_ADDR = raspberrypi
PI_INSTALL_DIR = /usr/bin

TOOLCHAIN = armv8-rpi3-linux-gnueabihf
TOOLCHAIN_DIR = /opt/x-tools/$(TOOLCHAIN)
SYSROOT = $(TOOLCHAIN_DIR)/$(TOOLCHAIN)/sysroot
HEADERS = $(TOOLCHAIN_DIR)/$(TOOLCHAIN)/include
SYSLIBS = $(TOOLCHAIN_DIR)/$(TOOLCHAIN)/lib
BUILDTOOLS = $(TOOLCHAIN_DIR)/bin/$(TOOLCHAIN)

CC = gcc
LD = ld
AR = ar
CFLAGS = -g -Wall -I$(HEADERS) -I$(INCLUDE_DIR) $(TESTING)
LDFLAGS = -lc -pthread -lm -lMotor -lbcm2835 -lsocket -lssl -lcrypto
TEST_SERVER_LDFLAGS = -lc -pthread -lm -lMotor -lbcm2835 -lsocket -l:$(CMOCKA_STATIC) \
					  -Wl,--wrap=runForward \
					  -Wl,--wrap=runBackward \
					  -Wl,--wrap=setMotor \
					  -Wl,--wrap=turnRightF \
					  -Wl,--wrap=turnLeftF \
					  -Wl,--wrap=turnRightB \
					  -Wl,--wrap=turnLeftB \
					  -Wl,--wrap=rotateRight \
					  -Wl,--wrap=rotateLeft \
					  -Wl,--wrap=idle \
					  -Wl,--wrap=motorInit \
					  -Wl,--wrap=motorQuit

TEST_SERVER_CFLAGS = -I$(HEADERS) -I$(INCLUDE_DIR)
TEST_ROBOT_LDFLAGS = -lc -pthread -lm -lMotor -lbcm2835
ARFLAGS = -crs
APPNAME = RoboPiServer
TEST_SERVER_APPNAME = serverTest
TEST_ROBOT_APPNAME = robotTest
LIBNAME = libMotor.a
CMOCKA_STATIC = libcmocka-static.a

SRC_DIR = src
INCLUDE_DIR = include
TEST_SERVER_SRC_DIR = test_server
TEST_ROBOT_SRC_DIR = test_robot
LIB_DIR = lib
OUT_DIR = bin
MOTORLIB_SRCDIR = src/motorLib

SRCS = $(wildcard $(SRC_DIR)/*.c)
TEST_SERVER_SRCS = $(wildcard $(TEST_SERVER_SRC_DIR)/*.c)
TEST_ROBOT_SRCS = $(wildcard $(TEST_ROBOT_SRC_DIR)/*.c)
OBJS = $(SRCS:.c=.o)
TEST_SERVER_OBJS = $(TEST_SERVER_SRCS:.c=.o)
TEST_ROBOT_OBJS = $(TEST_ROBOT_SRCS:.c=.o)
DEPS = $(wildcard $(INCLUDE_DIR)/*.h)

LIBSRCS = $(wildcard $(MOTORLIB_SRCDIR)/*.c)
LIBOBJS = $(LIBSRCS:.c=.o)

DISCOSERVICE = robopi-server.service

.PHONY: clean
.PHONY: install
.PHONY: install_libs
.PHONY: install_libMotor
.PHONY: install_bcm2835
.PHONY: install_libcmocka
.PHONY: install_libsocket
.PHONY: deploy
.PHONY: deploy_discovery

#4)
all: app

app: $(OUT_DIR)/$(APPNAME)

$(OUT_DIR)/$(APPNAME): $(OBJS)
	mkdir -p $(OUT_DIR)
	$(BUILDTOOLS)-$(CC) $^ $(LDFLAGS) -o $@

$(SRC_DIR)/%.o: $(SRC_DIR)/%.c $(DEPS)
	$(BUILDTOOLS)-$(CC) -o $@ -c $< $(CFLAGS)

$(TEST_SERVER_SRC_DIR)/%.o: $(TEST_SERVER_SRC_DIR)/%.c
	$(BUILDTOOLS)-$(CC) -o $@ -c $< $(TEST_SERVER_CFLAGS)

$(TEST_ROBOT_SRC_DIR)/%.o: $(TEST_ROBOT_SRC_DIR)/%.c
	$(BUILDTOOLS)-$(CC) -o $@ -c $< $(TEST_SERVER_CFLAGS)

test: test_server test_robot

test_server: $(OUT_DIR)/$(TEST_SERVER_APPNAME)

test_robot: $(OUT_DIR)/$(TEST_ROBOT_APPNAME)

$(OUT_DIR)/$(TEST_SERVER_APPNAME): $(TEST_SERVER_OBJS) $(SRC_DIR)/server.o $(SRC_DIR)/protocol.o $(SRC_DIR)/camera.o $(SRC_DIR)/motor.o
	$(BUILDTOOLS)-$(CC) $^ $(TEST_SERVER_LDFLAGS) -o $@

$(OUT_DIR)/$(TEST_ROBOT_APPNAME): $(TEST_ROBOT_OBJS) $(SRC_DIR)/motor.o $(SRC_DIR)/camera.o
	$(BUILDTOOLS)-$(CC) $^ $(TEST_ROBOT_LDFLAGS) -o $@

#2)
lib: $(LIBNAME)

$(LIBNAME): $(LIBOBJS)
	$(BUILDTOOLS)-$(AR) $(ARFLAGS) $(LIB_DIR)/$@ $^

install_libs: install_bcm2835 install_libMotor install_libcmocka install_libsocket install_sslLib

#1) [root] Execute once per toolchain reset
install_bcm2835:
	cp $(MOTORLIB_SRCDIR)/bcm2835.h $(HEADERS)/
	cp $(MOTORLIB_SRCDIR)/DEV_Config.h $(HEADERS)/
	cp $(MOTORLIB_SRCDIR)/PCA9685.h $(HEADERS)/
	cp $(MOTORLIB_SRCDIR)/MotorDriver.h $(HEADERS)/
	cp $(LIB_DIR)/libbcm2835.a $(SYSLIBS)/

#3) [root] Execute each time the library is recompiled
install_libMotor: lib
	cp $(LIB_DIR)/$(LIBNAME) $(SYSLIBS)

install_libcmocka:
	cp $(LIB_DIR)/libcmocka-static.a $(SYSLIBS)
	cp $(SRC_DIR)/cmocka/*.h $(HEADERS)/

install_libsocket:
	cp $(LIB_DIR)/libsocket.a $(SYSLIBS)
	cp $(SRC_DIR)/libsocket/*.h $(HEADERS)/

install_sslLib:
	cp -r $(LIB_DIR)/openssl_libs/* $(SYSLIBS)
	cp -r $(SRC_DIR)/openssl $(HEADERS)

install: app
	cp $(OUT_DIR)/$(APPNAME) $(PI_INSTALL_DIR)
	setcap cap_sys_rawio+ep $(PI_INSTALL_DIR)/$(APPNAME)

deploy: $(OUT_DIR)/$(APPNAME)
	scp $^ pi@$(PI_ADDR):/home/pi
	ssh pi@$(PI_ADDR) 'sudo setcap cap_sys_rawio+ep $(APPNAME)'
	ssh pi@$(PI_ADDR) 'sudo mv $(APPNAME) $(PI_INSTALL_DIR)/$(APPNAME)'

deploy_tests: deploy_server_tests deploy_motor_tests

deploy_server_tests: $(OUT_DIR)/$(TEST_SERVER_APPNAME)
	scp $^ pi@$(PI_ADDR):/home/pi
	ssh pi@$(PI_ADDR) 'sudo setcap cap_sys_rawio+ep $(TEST_SERVER_APPNAME)'
	ssh pi@$(PI_ADDR) 'sudo mv $(TEST_SERVER_APPNAME) $(PI_INSTALL_DIR)/$(TEST_SERVER_APPNAME)'

deploy_motor_tests: $(OUT_DIR)/$(TEST_ROBOT_APPNAME)
	scp $^ pi@$(PI_ADDR):/home/pi
	ssh pi@$(PI_ADDR) 'sudo setcap cap_sys_rawio+ep $(TEST_ROBOT_APPNAME)'
	ssh pi@$(PI_ADDR) 'sudo mv $(TEST_ROBOT_APPNAME) $(PI_INSTALL_DIR)/$(TEST_ROBOT_APPNAME)'

deploy_discovery:
	scp $(DISCOSERVICE) pi@$(PI_ADDR):/home/pi
	ssh pi@$(PI_ADDR) 'sudo mv $(DISCOSERVICE) /etc/avahi/services/$(DISCOSERVICE)'

clean:
	rm -f $(SRC_DIR)/*.o
	rm -f $(LIB_DIR)/$(LIBNAME)
	rm -f $(OUT_DIR)/*
	rm -f $(TEST_SERVER_SRC_DIR)/*.o
	rm -f $(TEST_ROBOT_SRC_DIR)/*.o
