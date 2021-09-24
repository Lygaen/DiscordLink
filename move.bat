@echo off
cls
echo Moving file...
copy /Y /B "C:\Code\Java\Discord\out\artifacts\Discord_jar\Discord.jar" "C:\Code\Server\plugins" > nul
echo Done !
exit