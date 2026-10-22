@echo off
SETLOCAL

:: Check if both arguments are provided
IF "%~1"=="" GOTO Error
IF "%~2"=="" GOTO Error

:: Assign arguments to variables
SET TAG_NAME=%~1
SET TAG_MESSAGE=%~2

echo Creating local annotated tag: %TAG_NAME%
git tag -a "%TAG_NAME%" -m "%TAG_MESSAGE%"

:: Check if the local tag was created successfully
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to create local tag.
    exit /b %ERRORLEVEL%
)

echo Pushing tag "%TAG_NAME%" to origin...
git push origin "%TAG_NAME%"

:: Check if the push was successful
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Failed to push tag to remote repository.
    exit /b %ERRORLEVEL%
)

echo [SUCCESS] Tag "%TAG_NAME%" successfully created and pushed!
goto End

:Error
echo [ERROR] Missing required arguments.
echo.
echo Usage:   %~nx0 [tag_name] [tag_message]
echo Example: %~nx0 v1.2.0 "Release version 1.2.0"
echo.
exit /b 1

:End
ENDLOCAL