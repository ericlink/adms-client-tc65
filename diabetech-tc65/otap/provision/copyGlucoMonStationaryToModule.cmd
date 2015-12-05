cd "C:\Documents and Settings\elink\My Documents\GlucoMON2\src\diabetech-glucomon-st\deploy"

REM Remove directory and subdirectories (e.g. storage if it is there)
MESrmdir mod:a:\ota


MESmkdir mod:a:\ota
MEScopy GlucoMonStationary.jad mod:a:\ota\GlucoMonStationary.jad
MEScopy GlucoMonStationary.jar mod:a:\ota\GlucoMonStationary.jar

MESdir  mod:a:\ota


pause

















