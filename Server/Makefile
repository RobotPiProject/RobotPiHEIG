PI_ADDR = 192.168.0.66

TOOLCHAIN = armv8-rpi3-linux-gnueabihf
TOOLCHAIN_DIR = /opt/x-tools/$(TOOLCHAIN)
SYSROOT = $(TOOLCHAIN_DIR)/$(TOOLCHAIN)/sysroot
HEADERS = $(TOOLCHAIN_DIR)/$(TOOLCHAIN)/include
LIBS = $(TOOLCHAIN_DIR)/$(TOOLCHAIN)/lib
BUILDTOOLS = $(TOOLCHAIN_DIR)/bin/$(TOOLCHAIN)

CC = gcc
LD = ld
AR = ar
CFLAGS = -g -Wall
LDFLAGS = -lc -pthread -lbcm2835 -lm
ARFLAGS = -crs
TARGETFOLDER = bin
APPNAME = RoboPiServer
TEST_APPNAME = RoboPiServerTest
LIBNAME = libMotor.a

SUBDIR = src
INCLUDE_DIR = $(SUBDIR)/include
TEST_SUBDIR = test
OUT_DIR = ./bin
TEST_OUT_DIR = ./test_bin
OBJ_DIR = ./build
TEST_OBJ_DIR = ./test_build
MOTORLIB_SRCDIR = src/motorLib

SRCS = $(wildcard *.c $(foreach dir, $(SUBDIR), $(dir)/*.c))
TEST_SRCS = $(wildcard *.c $(foreach dir, $(TEST_SUBDIR), $(dir)/*.c))
OBJS = $(addprefix $(OBJ_DIR)/, $(SRCS:c=o))
TEST_OBJS = $(addprefix $(TEST_OBJ_DIR)/, $(TEST_SRCS:c=o))
DEPS = $(wildcard *.h $(foreach dir, $(INCLUDE_DIR), $(dir)/*.h))

LIBSRCS = $(wildcard $(MOTORLIB_SRCDIR)/*.c)
LIBOBJS = $(addprefix $(OBJ_DIR)/, $(LIBSRCS:.c=.o))

.PHONY: clean
.PHONY: lib

all: $(OUT_DIR)/$(APPNAME) $(TEST_OUT_DIR)/$(TEST_APPNAME)

$(OUT_DIR)/$(APPNAME): lib $(OBJS)
	mkdir -p $(@D)
	$(BUILDTOOLS)-$(CC) $(OBJS) $(LDFLAGS) -o $@

$(OBJ_DIR)/%.o: %.c $(DEPS)
	mkdir -p $(@D)
	$(BUILDTOOLS)-$(CC) -o $@ -c $< $(CFLAGS)

$(TEST_OUT_DIR)/$(TEST_APPNAME): $(TEST_OBJS)
	mkdir -p $(@D)
	$(BUILDTOOLS)-$(CC) $(TEST_OBJS) $(LDFLAGS) -o $@

$(TEST_OBJ_DIR)/%.o: %.c
	mkdir -p $(@D)
	$(BUILDTOOLS)-$(CC) -o $@ -c $< $(CFLAGS)

clean:
	rm -rf $(OUT_DIR)/*
	rm -rf $(OBJ_DIR)/*
	rm -rf $(TEST_OUT_DIR)/*
	rm -rf $(TEST_OBJ_DIR)/*
	rm -f $(LIBNAME)

deploy: $(OUT_DIR)/$(APPNAME)
	scp $^ pi@$(PI_ADDR):/home/pi

test:

new_file:
	@echo "Entrez le nom des fichiers que vous voulez créer"
	@read fileName; \
	touch src/$$fileName.c src/include/$$fileName.h test/test_$$fileName.c

lib: $(LIBNAME)

$(LIBNAME): $(LIBOBJS)
	$(BUILDTOOLS)-$(AR) $(ARFLAGS) $(LIBS)/$@ $(LIBOBJS)
	cp $(MOTORLIB_SRCDIR)/*.h $(HEADERS)/
