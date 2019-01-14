set SMARTHOME_DIRECTORY="C:\Program Files (x86)\smarthome"

rmdir /S /Q %SMARTHOME_DIRECTORY%
xcopy ..\target\smarthome.exe %SMARTHOME_DIRECTORY%\ /Y /H /S
xcopy ..\target\lib\*.* %SMARTHOME_DIRECTORY%\lib\ /Y /H /S
echo F|xcopy resources\JIntellitype64.dll %SMARTHOME_DIRECTORY%\JIntellitype.dll /S /Q /Y /F