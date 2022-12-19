#!/usr/bin/env bash

set -e

cd rabbitmq-c

if [ "$TARGET" == "mingw" ]; then
  CONF_OPTS="-fPIC --host=x86_64-w64-mingw32"
elif [ "$TARGET" == "linux" ]; then
  CONF_OPTS="-fPIC"
elif [ "$TARGET" == "darwin" ]; then
  CONF_OPTS="--host=x86_64-w64-darwin"
else
  echo "Unknown TARGET=$TARGET"
  exit 1
fi

if [ -d build ]; then rm -Rf build; fi
mkdir -p build && cd build
cmake -DCMAKE_C_FLAGS=$CONF_OPTS ..
cmake --build . --config Release

cd ../..
mkdir -p "build/$TARGET"
cp -v rabbitmq-c/build/librabbitmq/librabbitmq.a "build/$TARGET"

echo "Done building librabbitmq"
