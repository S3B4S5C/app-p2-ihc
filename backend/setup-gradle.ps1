# Bootstrap de una sola vez para generar el Gradle Wrapper oficial sin Gradle global.
# Requiere Windows PowerShell o PowerShell 7 y conexión a Internet.
$ErrorActionPreference = "Stop"
$version = "9.3.0"
$projectDir = $PSScriptRoot
$installDir = Join-Path $projectDir ".gradle-local"
$zipFile = Join-Path $installDir "gradle-$version-bin.zip"
$gradleBat = Join-Path $installDir "gradle-$version\bin\gradle.bat"
$distributionUrl = "https://services.gradle.org/distributions/gradle-$version-bin.zip"
$shaUrl = "$distributionUrl.sha256"

New-Item -ItemType Directory -Force -Path $installDir | Out-Null
if (-not (Test-Path $gradleBat)) {
    Write-Host "Descargando Gradle $version desde el servidor oficial..."
    Invoke-WebRequest -Uri $distributionUrl -OutFile $zipFile
    $expectedSha = ((Invoke-WebRequest -Uri $shaUrl).Content -split '\s+')[0].Trim().ToLowerInvariant()
    $actualSha = (Get-FileHash -Path $zipFile -Algorithm SHA256).Hash.ToLowerInvariant()
    if ($actualSha -ne $expectedSha) {
        Remove-Item $zipFile -ErrorAction SilentlyContinue
        throw "El SHA-256 de la descarga no coincide. Se ha cancelado la instalación."
    }
    Expand-Archive -LiteralPath $zipFile -DestinationPath $installDir -Force
    Remove-Item $zipFile
}

Push-Location $projectDir
try {
    & $gradleBat wrapper --gradle-version $version --distribution-type bin
    if ($LASTEXITCODE -ne 0) { throw "No se pudo generar el Gradle Wrapper." }
    Write-Host "Gradle Wrapper creado: .\gradlew.bat"
} finally {
    Pop-Location
}
