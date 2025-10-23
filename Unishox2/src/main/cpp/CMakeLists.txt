cmake_minimum_required(VERSION 3.22.1)
project(unishox2_jni)

# Add the Unishox2 source files
set(UNISHOX2_SOURCES
    unishox2.c
    unishox2_jni.c
)

# Create shared library
add_library(unishox2_jni SHARED ${UNISHOX2_SOURCES})

# Find and link log library (Android logging)
find_library(log-lib log)
target_link_libraries(unishox2_jni ${log-lib})
