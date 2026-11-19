PFC portable package

Files:
- pfc.jar: executable application jar
- lib\: Java dependencies and OpenCV DLLs
- run-client.bat: network client mode
- run-standalone.bat: standalone mode
- run-server.bat: server mode

Requirements on target PC:
- 64-bit Java 17 or newer installed and available on PATH
- OpenCV DLLs are already included in lib

Notes:
- Start the app from the included .bat files so relative folders like audio, routes and video resolve correctly.
- You can also run manually with: java -jar pfc.jar [server|alone]
