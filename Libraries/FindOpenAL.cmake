if(APPLE)
	FIND_PATH(OPENAL_INCLUDES al.h)
else()
	set(OPENAL_INCLUDES "${PROJECT_SOURCE_DIR}/Libraries/openal/include")
endif()

find_library(OPENAL_LIBRARY NAMES OpenAL32 OpenAL openal PATHS "${PROJECT_SOURCE_DIR}/Libraries/openal/lib/")

set(LIBRARIES_INCLUDES ${LIBRARIES_INCLUDES} ${OPENAL_INCLUDES})
set(LIBRARIES_LINKS ${LIBRARIES_LINKS} "${OPENAL_LIBRARY}")
message(STATUS "OpenAL: ${OPENAL_LIBRARY}")

if(NOT OPENAL_LIBRARY)
	message(FATAL_ERROR "OpenAL library not found!")
endif()
