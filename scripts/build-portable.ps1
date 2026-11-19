param(
    [string]$OutputRoot = "dist\pfc-portable",
    [string]$ZipPath = "dist\pfc-portable.zip"
)

$ErrorActionPreference = "Stop"

$repoRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot ".."))
$outputDir = [System.IO.Path]::GetFullPath((Join-Path $repoRoot $OutputRoot))
$zipFile = [System.IO.Path]::GetFullPath((Join-Path $repoRoot $ZipPath))
$buildRoot = Join-Path $repoRoot "out\portable-build"
$classesDir = Join-Path $buildRoot "classes"
$manifestPath = Join-Path $buildRoot "MANIFEST.MF"

function Resolve-JavaTool([string]$toolName) {
    $preferredRoot = "C:\Program Files\JetBrains\IntelliJ IDEA 2025.3.3\jbr\bin"
    $preferredTool = Join-Path $preferredRoot $toolName
    if (Test-Path $preferredTool) {
        return $preferredTool
    }

    if ($env:JAVA_HOME) {
        $javaHomeTool = Join-Path $env:JAVA_HOME "bin\$toolName"
        if (Test-Path $javaHomeTool) {
            return $javaHomeTool
        }
    }

    $command = Get-Command $toolName -ErrorAction SilentlyContinue
    if ($command) {
        return $command.Source
    }

    throw "Unable to locate $toolName. Install JDK 17+ or set JAVA_HOME."
}

$javac = Resolve-JavaTool "javac.exe"
$jarTool = Resolve-JavaTool "jar.exe"

if (Test-Path $buildRoot) {
    Remove-Item -Recurse -Force $buildRoot
}
New-Item -ItemType Directory -Force -Path $classesDir | Out-Null

$compileClasspath = @(
    (Join-Path $repoRoot "lib\opencv-4130.jar"),
    (Join-Path $repoRoot "lib\jSerialComm-2.11.4.jar"),
    (Join-Path $repoRoot "lib\javax.mail.jar")
) -join ";"

$javaSources = Get-ChildItem -Path (Join-Path $repoRoot "src") -Recurse -Filter *.java | ForEach-Object { $_.FullName }
if ($javaSources.Count -eq 0) {
    throw "No Java sources found under src."
}

& $javac --release 17 -encoding windows-1252 -d $classesDir -cp $compileClasspath $javaSources

@"
Manifest-Version: 1.0
Main-Class: pfc.gui.ApplicationRunner
Class-Path: lib/opencv-4130.jar lib/jSerialComm-2.11.4.jar lib/javax.mail.jar

"@ | Set-Content -Path $manifestPath -Encoding ascii

if (Test-Path $outputDir) {
    Remove-Item -Recurse -Force $outputDir
}
New-Item -ItemType Directory -Force -Path $outputDir | Out-Null
New-Item -ItemType Directory -Force -Path (Join-Path $outputDir "lib") | Out-Null

& $jarTool --create --file (Join-Path $outputDir "pfc.jar") --manifest $manifestPath -C $classesDir .

Copy-Item -Path (Join-Path $repoRoot "lib\opencv-4130.jar") -Destination (Join-Path $outputDir "lib") -Force
Copy-Item -Path (Join-Path $repoRoot "lib\jSerialComm-2.11.4.jar") -Destination (Join-Path $outputDir "lib") -Force
Copy-Item -Path (Join-Path $repoRoot "lib\javax.mail.jar") -Destination (Join-Path $outputDir "lib") -Force
Copy-Item -Path (Join-Path $repoRoot "lib\opencv_java4130.dll") -Destination (Join-Path $outputDir "lib") -Force
Copy-Item -Path (Join-Path $repoRoot "lib\opencv_videoio_ffmpeg4130_64.dll") -Destination (Join-Path $outputDir "lib") -Force

foreach ($folderName in @("audio", "images", "routes", "video")) {
    $sourcePath = Join-Path $repoRoot $folderName
    if (Test-Path $sourcePath) {
        Copy-Item -Path $sourcePath -Destination $outputDir -Recurse -Force
    }
}

Copy-Item -Path (Join-Path $repoRoot "pfc.properties") -Destination $outputDir -Force

@"
@echo off
cd /d "%~dp0"
start "" javaw -jar pfc.jar
"@ | Set-Content -Path (Join-Path $outputDir "run-client.bat") -Encoding ascii

@"
@echo off
cd /d "%~dp0"
start "" javaw -jar pfc.jar alone
"@ | Set-Content -Path (Join-Path $outputDir "run-standalone.bat") -Encoding ascii

@"
@echo off
cd /d "%~dp0"
start "" javaw -jar pfc.jar server
"@ | Set-Content -Path (Join-Path $outputDir "run-server.bat") -Encoding ascii

@"
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
"@ | Set-Content -Path (Join-Path $outputDir "README.txt") -Encoding ascii

$zipParent = Split-Path $zipFile -Parent
if (-not (Test-Path $zipParent)) {
    New-Item -ItemType Directory -Force -Path $zipParent | Out-Null
}
if (Test-Path $zipFile) {
    Remove-Item -Force $zipFile
}
Compress-Archive -Path (Join-Path $outputDir "*") -DestinationPath $zipFile -Force

Write-Host "Portable package created in $outputDir"
Write-Host "Zip created in $zipFile"
