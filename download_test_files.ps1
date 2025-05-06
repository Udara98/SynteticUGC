# PowerShell script to download test files for lip sync testing

# Create test resources directory if it doesn't exist
$testResourcesDir = "src/test/resources"
if (-not (Test-Path $testResourcesDir)) {
    New-Item -ItemType Directory -Path $testResourcesDir -Force
}

# Create a WebClient object
$webClient = New-Object System.Net.WebClient

# Download sample audio file (a short speech sample)
$audioUrl = "https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav"
$audioPath = Join-Path $testResourcesDir "demo-audio.mp3"
Write-Host "Downloading sample audio file..."
try {
    $webClient.DownloadFile($audioUrl, $audioPath)
    Write-Host "Audio file downloaded successfully"
} catch {
    Write-Host "Failed to download audio file: $_"
}

# Download sample video file (a short video with a person speaking)
$videoUrl = "https://filesamples.com/samples/video/mp4/sample_640x360.mp4"
$videoPath = Join-Path $testResourcesDir "video.mp4"
Write-Host "Downloading sample video file..."
try {
    $webClient.DownloadFile($videoUrl, $videoPath)
    Write-Host "Video file downloaded successfully"
} catch {
    Write-Host "Failed to download video file: $_"
}

Write-Host "Download process completed" 