# ===================================================================================
#  The OpenCV CMake configuration file
#
#             ** File generated automatically, do not modify **
#
#  Usage from an external project:
#    In your CMakeLists.txt, add these lines:
#
#    find_package(OpenCV REQUIRED)
#    include_directories(${OpenCV_INCLUDE_DIRS}) # Not needed for CMake >= 2.8.11
#    target_link_libraries(MY_TARGET_NAME ${OpenCV_LIBS})
#
#    Or you can search for specific OpenCV modules:
#
#    find_package(OpenCV REQUIRED core videoio)
#
#    If the module is found then OPENCV_<MODULE>_FOUND is set to TRUE.
#
#    This file will define the following variables:
#      - OpenCV_LIBS                     : The list of all imported targets for OpenCV modules.
#      - OpenCV_INCLUDE_DIRS             : The OpenCV include directories.
#      - OpenCV_ANDROID_NATIVE_API_LEVEL : Minimum required level of Android API.
#      - OpenCV_VERSION                  : The version of this OpenCV build: "4.0.1"
#      - OpenCV_VERSION_MAJOR            : Major version part of OpenCV_VERSION: "4"
#      - OpenCV_VERSION_MINOR            : Minor version part of OpenCV_VERSION: "0"
#      - OpenCV_VERSION_PATCH            : Patch version part of OpenCV_VERSION: "1"
#      - OpenCV_VERSION_STATUS           : Development status of this build: ""
#
# ===================================================================================

# Extract directory name from full path of the file currently being processed.
# Note that CMake 2.8.3 introduced CMAKE_CURRENT_LIST_DIR. We reimplement it
# for older versions of CMake to support these as well.
if(CMAKE_VERSION VERSION_LESS "2.8.3")
  get_filename_component(CMAKE_CURRENT_LIST_DIR "${CMAKE_CURRENT_LIST_FILE}" PATH)
endif()

if(NOT DEFINED OpenCV_CONFIG_SUBDIR)
  set(OpenCV_CONFIG_SUBDIR "/abi-${ANDROID_NDK_ABI_NAME}")
endif()

set(OpenCV_CONFIG_PATH "${CMAKE_CURRENT_LIST_DIR}${OpenCV_CONFIG_SUBDIR}")
if(EXISTS "${OpenCV_CONFIG_PATH}/OpenCVConfig.cmake")
  include("${OpenCV_CONFIG_PATH}/OpenCVConfig.cmake")
else()
  if(NOT OpenCV_FIND_QUIETLY)
    message(WARNING "Found OpenCV Android Pack but it has no binaries compatible with your ABI (can't find: ${OpenCV_CONFIG_SUBDIR})")
  endif()
  set(OpenCV_FOUND FALSE)
endif()
