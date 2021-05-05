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
CFLAGS = -g -Wall -I$(HEADERS) -I$(INCLUDE_DIR)
LDFLAGS = -lc -pthread -lm -lMotor -lbcm2835
ARFLAGS = -crs
APPNAME = RoboPiServer
TEST_SERVER_APPNAME = serverTest
TEST_ROBOT_APPNAME = robotTest
LIBNAME = libMotor.a

SUBDIR = src
INCLUDE_DIR = include
TEST_SERVER_SUBDIR = test_server
TEST_ROBOT_SUBDIR = test_robot
LIB_DIR = lib
OUT_DIR = bin
MOTORLIB_SRCDIR = src/motorLib

SRCS = $(wildcard $(SUBDIR)/*.c)
TEST_SERVER_SRCS = $(wildcard $(TEST_SERVER_SUBDIR)/*.c)
TEST_ROBOT_SRCS = $(wildcard $(TEST_ROBOT_SUBDIR)/*.c)
OBJS = $(SRCS:.c=.o)
TEST_SERVER_OBJS = $(TEST_SERVER_SRCS:.c=.o)
TEST_ROBOT_OBJS = $(TEST_ROBOT_SRCS:.c=.o)
DEPS = $(wildcard $(INCLUDE_DIR)/*.h)

LIBSRCS = $(wildcard $(MOTORLIB_SRCDIR)/*.c)
LIBOBJS = $(LIBSRCS:.c=.o)

.PHONY: clean
.PHONY: lib
.PHONY: install_libs
.PHONY: install

#4)
all: app

app: $(OUT_DIR)/$(APPNAME)

$(OUT_DIR)/$(APPNAME): $(OBJS)
	mkdir -p $(OUT_DIR)
	$(BUILDTOOLS)-$(CC) $(OBJS) $(LDFLAGS) -o $@

%.o: %.c $(DEPS)
	$(BUILDTOOLS)-$(CC) -o $@ -c $< $(CFLAGS)

clean:
	rm *.o
	rm -f $(LIBNAME)

deploy: $(OUT_DIR)/$(APPNAME)
	scp $^ pi@$(PI_ADDR):/home/pi
	ssh pi@$(PI_ADDR) 'sudo setcap cap_sys_rawio+ep $(APPNAME)'

test: test_server test_robot

test_server: $(OUT_DIR)/$(TEST_SERVER_APPNAME)

test_robot: $(OUT_DIR)/$(TEST_ROBOT_APPNAME)

$(OUT_DIR)/$(TEST_SERVER_APPNAME): $(TEST_SERVER_OBJS)
	$(BUILDTOOLS)-$(CC) $(TEST_SERVER_OBJS) $(LDFLAGS) -o $@

$(OUT_DIR)/$(TEST_ROBOT_APPNAME): $(TEST_ROBOT_OBJS) src/motor.o
	$(BUILDTOOLS)-$(CC) $^ $(LDFLAGS) -o $@

new_file:
	@echo "Entrez le nom des fichiers que vous voulez créer"
	@read fileName; \
	touch src/$$fileName.c src/include/$$fileName.h test/test_$$fileName.c

#2)
lib: $(LIBNAME)

#1) [root] Execute once per toolchain reset
install_bcm2835:
	cp $(MOTORLIB_SRCDIR)/bcm2835.h $(HEADERS)/
	cp $(LIB_DIR)/libbcm2835.a $(SYSLIBS)/

#3) [root] Execute each time the library is recompiled
install_motorLib: lib
	cp $(LIB_DIR)/$(LIBNAME) $(SYSLIBS)

$(LIBNAME): $(LIBOBJS)
	$(BUILDTOOLS)-$(AR) $(ARFLAGS) $(LIB_DIR)/$@ $(LIBOBJS)

install: app
	cp $(OUT_DIR)/$(APPNAME) $(PI_INSTALL_DIR)
	setcap cap_sys_rawio+ep $(PI_INSTALL_DIR)/$(APPNAME)
