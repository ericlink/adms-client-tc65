@echo off
echo.
echo -------------------------------------------------------------------------------
echo Building debug version "%1" ...
echo -------------------------------------------------------------------------------

pushd ..\..

if exist tmpclasses rmdir /S /Q tmpclasses
if exist jarclasses rmdir /S /Q jarclasses

md tmpclasses
md jarclasses

echo Compiling...
"%JAVA_HOME%\bin\javac" -g -bootclasspath "../lib/classes.zip" -d tmpclasses example/%1/*.java

echo Preverifying...
"../bin/preverify" -classpath "../lib/classes.zip" -d jarclasses tmpclasses

echo Packaging...
"%JAVA_HOME%\bin\jar" cfm example/%1/%1.jar example/%1/%1.jad -C jarclasses example

rmdir /S /Q tmpclasses
rmdir /S /Q jarclasses

popd
