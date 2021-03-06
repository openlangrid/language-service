@echo off

set TAGDIR=%1

set BIN=%TAGDIR%\bin
set CMD=%TAGDIR%\cmd
set LIB=%TAGDIR%\lib
set TAGOPT=%LIB%\english.par -token -lemma -sgml -no-unknown

if "%3"=="" goto label1
perl %CMD%\tokenize.pl -e -a %LIB%\english-abbreviations %2 | %BIN%\tree-tagger %TAGOPT% > %3
goto end

:label1
if "%2"=="" goto label2
perl %CMD%\tokenize.pl -e -a %LIB%\english-abbreviations %2 | %BIN%\tree-tagger %TAGOPT% 
goto end

:label2
echo.
echo Usage: tag-english file {file}
echo.

:end
