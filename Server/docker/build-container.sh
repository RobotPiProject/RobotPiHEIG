#!/usr/bin/env bash

if [[ ! -d RPi-Cpp-Toolchain ]]; then
  git clone git@github.com:tttapa/RPi-Cpp-Toolchain.git
  cd RPi-Cpp-Toolchain/toolchain
  ./toolchain.sh rpi3-armv8-dev --pull --export
  cp x-tools -r ../..
  cd ../..
fi

echo "Toolchain built, starting image build"

docker build -t robopi/robopi_server .
