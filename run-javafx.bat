@echo off
rem Placez ici le chemin vers le dossier lib de votre JavaFX SDK.
set "PATH_TO_FX=C:\Uopenjfx-21.0.11_windows-x64_bin-sdk\javafx-sdk-21.0.11\lib"

if not exist "%PATH_TO_FX%" (
  echo JavaFX SDK non trouve dans %PATH_TO_FX%
  echo Mettez a jour run-javafx.bat avec le bon chemin vers le dossier lib.
  pause
  exit /b 1
)

javac --module-path "%PATH_TO_FX%" --add-modules javafx.controls MainApp.java
if errorlevel 1 exit /b 1

java --module-path "%PATH_TO_FX%" --add-modules javafx.controls -cp . MainApp
