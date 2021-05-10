FROM ubuntu:focal

ENV RPI_TOOLCHAIN_DIR=/opt/Rpi-Toolchain
ENV DEBIAN_FRONTEND=noninteractive

# install requisite packages
RUN apt-get update && apt-get install -y \
    sudo \
    make \
    ssh \
    vim \
    git

# install toolchain
COPY x-tools/ /opt/x-tools/

RUN git clone https://github.com/RobotPiProject/RobotPiHEIG.git && \
    cd RobotPiHEIG/Server && \
    git checkout server_workspace && \
    sudo make install_bcm2835 && \
    sudo make install_libMotor
