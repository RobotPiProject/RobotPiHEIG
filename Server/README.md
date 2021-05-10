# RoboPiHEIG - Server

The source for the server/robot part of the RoboPiHEIG project.

## Raspberry Pi setup

We use Raspberry Pi OS Lite 32-bit, because we don't need a desktop environment. Download the Raspberry Pi OS Lite version from here :

[https://www.raspberrypi.org/software/operating-systems/#raspberry-pi-os-32-bit](https://www.raspberrypi.org/software/operating-systems/#raspberry-pi-os-32-bit)

Unzip the downloaded file, and then flash the *.img file onto the Raspberry Pi's SD card:

```bash
$ sudo dd if=2021-03-04-raspios-buster-armhf-lite.img of=/dev/sdX bs=4M status=progress
```

Where `/dev/sdX` is the block device for your sdcard, usually `/dev/sdc` or `/dev/sdb`. Use the command `lsblk` to identify the correct block device, and do not mount the SD card. **Be sure to double-check that you have the right block device, especially not the one for your hard drive! You could brick your computer.**

To enable the ssh server on the Raspberry Pi, mount your newly flashed SD card and create an empty file named `ssh` at the root of the `boot` partition:

```bash
$ cd sdcard_boot_mount_dir
$ touch ssh
```

Replace `sdcard_boot_mount_dir` with the path to the directory where the `boot` partition of the SD card was mounted by your operating system.

To enable passwordless SSH access, copy your SSH public key to the Raspberry Pi's SD card, in the home directory in the `rootfs` partition:

```bash
$ mkdir sdcard_rootfs_mount_dir/home/pi/.ssh
$ cat ~/.ssh/id_rsa.pub >> rootfs_mount_dir/home/pi/.ssh/authorized_keys
```

Replace `sdcard_rootfs_mount_dir` with the path to the directory where the `rootfs` partition of the SD card was mounted by your operating system.

### bcm2835 setup

To enable non-root access to `/dev/mem` on the Raspberry Pi, the server executable must have the `cap_sys_rawio` capability :

```pi@raspberry$ sudo setcap cap_sys_rawio+ep RoboPiServer```

This step is done in the `deploy` target of the Makefile.

The following must be done only once for each time Raspbian is reinstalled :

0. Enable `libcap` support

```
sudo apt-get update
sudo apt-get install libcap2 libcap-dev
```

1. Add the current user to `kmem` group

```
pi@raspberry$ sudo adduser $USER kmem
```

2. Allow write access to /dev/mem by members of kmem group

```
pi@raspberry$ echo 'SUBSYSTEM=="mem", KERNEL=="mem", GROUP="kmem", MODE="0660"' | sudo tee /etc/udev/rules.d/98-mem.rules
pi@raspberry$ sudo reboot
```

##  Toolchain setup

We use the toolchain provided by the [RPi-Cpp-Toolchain](https://github.com/tttapa/RPi-Cpp-Toolchain) project. It uses docker to retrieve and generate the appropriate toolchain and sysroot for the Raspberry Pi Robot.

To do cross compilation, we need a *toolchain* and a *sysroot*. The toolchain contains the compiler suite for our target architecture. The sysroot is a minimal copy of the target filesystem.

1. Clone the RPi-Cpp-Toolchain :

```sh
$ git clone git@github.com:tttapa/RPi-Cpp-Toolchain.git
```

2. In the `toolchain` directory, run `./toolchain.sh rpi3-armv8-dev --pull --export`. The `rpi3-armv8-dev` is suitable for the Raspberry Pi 3 with a 32-bit raspbian OS. When the script is done, the `x-tools` directory contains the toolchain. You will need docker for this step.

3. Install the toolchain into your `/opt` directory :

```sh
$ cp x-tools /opt -r
```

### Library install locations

For cross-compiling purposes, header files shall be put in the following location in the toolchain:
```
/opt/x-tools/armv8-rpi3-linux-gnueabihf/armv8-rpi3-linux-gnueabihf/include
```

Compiled library files shall be put in the following location in the toolchain:

```
/opt/x-tools/armv8-rpi3-linux-gnueabihf/armv8-rpi3-linux-gnueabihf/lib
```

You can do both these by calling `make install_bcm2835` as root.
