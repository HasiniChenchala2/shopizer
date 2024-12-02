@echo off

:: Define the output directory
set OUTPUT_DIR=output_directory

:: Create the output directory if it doesn't exist
if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

:: Process the grammar
grammarinator-process shopizer.g4 -o "%OUTPUT_DIR%" --language py

:: Generate JSON files for different operations
grammarinator-generate ShopizerGenerator.ShopizerGenerator -r getCatalog -d 20 --stdout -n 1000 --sys-path "%OUTPUT_DIR%" > ./sm-shop/src/test/resources/getCatalog.json
grammarinator-generate ShopizerGenerator.ShopizerGenerator -r postCatalog -d 20 --stdout -n 1000 --sys-path "%OUTPUT_DIR%" > ./sm-shop/src/test/resources/postCatalog.json
grammarinator-generate ShopizerGenerator.ShopizerGenerator -r patchCatalog -d 20 --stdout -n 1000 --sys-path "%OUTPUT_DIR%" > ./sm-shop/src/test/resources/patchCatalog.json
grammarinator-generate ShopizerGenerator.ShopizerGenerator -r deleteCatalog -d 20 --stdout -n 1000 --sys-path "%OUTPUT_DIR%" > ./sm-shop/src/test/resources/deleteCatalog.json
grammarinator-generate ShopizerGenerator.ShopizerGenerator -r getManufacturer -d 20 --stdout -n 1000 --sys-path "%OUTPUT_DIR%" > ./sm-shop/src/test/resources/getManufacturer.json
